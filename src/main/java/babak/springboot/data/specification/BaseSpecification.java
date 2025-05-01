package babak.springboot.data.specification;

import babak.springboot.data.domain.BaseEntity;
import babak.springboot.data.search.SearchFilterModel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
@AllArgsConstructor
public class BaseSpecification<E extends BaseEntity, F extends SearchFilterModel> implements Specification<E> {

    private final F filterModel;

    @Override
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = filterModel.toPredicates(root, criteriaBuilder);
        Predicate notDeleted = criteriaBuilder.isFalse(root.get("deleted"));
        if (!predicates.isEmpty()) {
            predicates.add(notDeleted);
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }
        return notDeleted;
    }
}
