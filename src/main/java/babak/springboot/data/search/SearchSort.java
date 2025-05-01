package babak.springboot.data.search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
@Getter
@Setter
public class SearchSort {

    private String field;
    private SortDirection direction;

    public Sort toSort() {
        Sort sort = Sort.by(field);
        return direction == SortDirection.ASC ? sort : sort.descending();
    }

    public enum SortDirection {
        ASC, DESC
    }
}
