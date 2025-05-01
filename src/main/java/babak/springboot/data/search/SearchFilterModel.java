package babak.springboot.data.search;

import babak.springboot.data.domain.BaseEntity;
import babak.springboot.data.exception.ValueIsNotArrayException;
import babak.springboot.data.reflection.ReflectionUtil;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
@Getter
@Setter
public abstract class SearchFilterModel<E extends BaseEntity> {

    private int page;
    private int pageSize;
    private SearchSort sort;

    public abstract List<SearchFilterCondition> conditions();

    private String likeExpr(Object value, SearchOperand operand) {
        return switch (operand) {
            case STARTS_WITH -> value + "%";
            case ENDS_WITH -> "%" + value;
            case LIKE, NOT_LIKE -> "%" + value + "%";
            case I_LIKE -> "%" + value.toString().toLowerCase() + "%";
            default -> value.toString();
        };
    }

    private Predicate in(Path path, CriteriaBuilder criteriaBuilder, Object value) {
        if (value.getClass().isArray()) {
            Object[] values = (Object[]) value;
            CriteriaBuilder.In inPredicate = criteriaBuilder.in(path);
            for (int i = 0; i < values.length; i++) {
                inPredicate.value(values[i]);
            }
            return inPredicate;
        }
        throw new ValueIsNotArrayException();
    }

    public List<Predicate> toPredicates(Root<E> root, CriteriaBuilder criteriaBuilder) {
        return ReflectionUtil
                .getFieldsByAnnotation(this.getClass(), SearchField.class)
                .stream()
                .filter(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(this) != null;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                })
                .map(field -> {
                    SearchField annotation = field.getAnnotation(SearchField.class);
                    try {
                        field.setAccessible(true);
                        Path path;
                        if (StringUtils.isEmpty(annotation.relation())) {
                            path = root.get(annotation.column());
                        } else {
                            path = root.join(annotation.relation()).get(annotation.column());
                        }
                        Object value = field.get(this);
                        return criteriaBuilder.and(switch (annotation.operand()) {
                            case EQ -> criteriaBuilder.equal(path, value);
                            case NOT_EQ -> criteriaBuilder.notEqual(path, value);
                            case IN -> in(path, criteriaBuilder, value);
                            case NOT_IN -> in(path, criteriaBuilder, value).not();
                            case BETWEEN -> {
                                Object[] values = (Object[]) value;
                                yield criteriaBuilder.between(path, (Comparable) values[0], (Comparable) values[1]);
                            }
                            case LT -> criteriaBuilder.lessThan(path, (Comparable) value);
                            case LE -> criteriaBuilder.le(path, (Expression<? extends Number>) value);
                            case GT -> criteriaBuilder.greaterThan(path, (Comparable) value);
                            case GE -> criteriaBuilder.ge(path, (Expression<? extends Number>) value);
                            case LIKE -> criteriaBuilder.like(path, likeExpr(value, SearchOperand.LIKE));
                            case I_LIKE ->
                                    criteriaBuilder.like(criteriaBuilder.lower(path), likeExpr(value, SearchOperand.I_LIKE));
                            case NOT_LIKE -> criteriaBuilder.notLike(path, likeExpr(value, SearchOperand.NOT_LIKE));
                            case STARTS_WITH -> criteriaBuilder.like(path, likeExpr(value, SearchOperand.STARTS_WITH));
                            case ENDS_WITH -> criteriaBuilder.like(path, likeExpr(value, SearchOperand.ENDS_WITH));
                            default -> throw new RuntimeException("Invalid search operand");
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }
}
