package babak.springboot.data.pagination;

import babak.springboot.data.exception.SearchPredicateException;
import babak.springboot.data.reflection.ReflectionUtil;
import babak.springboot.data.search.SearchField;
import babak.springboot.data.search.SearchLogic;
import babak.springboot.data.search.SearchSort;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
@Getter
@Setter
public class SearchPage {

    private Integer page;
    private Integer pageSize;
    private SearchSort sort;

    public boolean predicate(Object reference, SearchLogic logic) {
        List<Field> fields = ReflectionUtil.getFieldsByAnnotation(this.getClass(), SearchField.class);
        List<Predicate> predicates = fields.stream()
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
                        Object searchFieldValue = field.get(this);
                        Predicate predicate = switch (annotation.operand()) {
                            case EQ -> o -> searchFieldValue != null &&
                                    searchFieldValue.equals(ReflectionUtil.getFieldValue(o, annotation.column()));
                            case NOT_EQ -> o -> searchFieldValue != null &&
                                    !searchFieldValue.equals(ReflectionUtil.getFieldValue(o, annotation.column()));
                            case IN ->
                                    o -> Arrays.stream(((Object[]) ReflectionUtil.getFieldValue(o, annotation.column())))
                                            .anyMatch(i -> i.equals(searchFieldValue));
                            case NOT_IN ->
                                    o -> Arrays.stream(((Object[]) ReflectionUtil.getFieldValue(o, annotation.column())))
                                            .anyMatch(i -> !i.equals(searchFieldValue));
                            case BETWEEN -> o -> searchFieldValue.equals(o);
                            case LT -> o -> ((Comparable<Object>) ReflectionUtil.getFieldValue(o, annotation.column()))
                                    .compareTo(searchFieldValue) < 0;
                            case LE -> o -> ((Comparable<Object>) ReflectionUtil.getFieldValue(o, annotation.column()))
                                    .compareTo(searchFieldValue) <= 0;
                            case GT -> o -> ((Comparable<Object>) ReflectionUtil.getFieldValue(o, annotation.column()))
                                    .compareTo(searchFieldValue) > 0;
                            case GE -> o -> ((Comparable<Object>) ReflectionUtil.getFieldValue(o, annotation.column()))
                                    .compareTo(searchFieldValue) >= 0;
                            case LIKE -> o -> ((String) ReflectionUtil.getFieldValue(o, annotation.column()))
                                    .contains((String) searchFieldValue);
                            case I_LIKE ->
                                    o -> (((String) ReflectionUtil.getFieldValue(o, annotation.column())).toLowerCase())
                                            .contains((searchFieldValue != null ? (String) searchFieldValue : "").toLowerCase());
                            case NOT_LIKE -> o -> !((String) ReflectionUtil.getFieldValue(o, annotation.column()))
                                    .contains((String) searchFieldValue);
                            case STARTS_WITH -> o -> ReflectionUtil.getFieldValue(o, annotation.column())
                                    .toString().startsWith(searchFieldValue != null ? searchFieldValue.toString() : "");
                            case ENDS_WITH -> o -> ReflectionUtil.getFieldValue(o, annotation.column())
                                    .toString().endsWith(searchFieldValue != null ? searchFieldValue.toString() : "");
                        };
                        return predicate;
                    } catch (Exception e) {
                        throw new SearchPredicateException(e.getMessage());
                    }
                }).toList();
        return predicates.isEmpty() ||
                predicates.stream()
                        .reduce(o -> logic == SearchLogic.AND, (p1, p2) -> logic(p1, p2, logic))
                        .test(reference);
    }

    private Predicate logic(Predicate predicate1, Predicate predicate2, SearchLogic logic) {
        return switch (logic) {
            case OR -> predicate1.or(predicate2);
            case AND -> predicate1.and(predicate2);
        };
    }
}
