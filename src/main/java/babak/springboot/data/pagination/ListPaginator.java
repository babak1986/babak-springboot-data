package babak.springboot.data.pagination;

import babak.springboot.data.search.SearchLogic;
import babak.springboot.data.search.SearchSort;
import babak.springboot.data.reflection.ReflectionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
public class ListPaginator {

    private ListPaginator() {
    }

    public static <T> Page<T> page(final Pageable pageable, List<T> list) {
        int first = Math.min(Long.valueOf(pageable.getOffset()).intValue(), list.size());
        int last = Math.min(first + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(first, last), pageable, list.size());
    }

    public static <T> List<T> sort(SearchSort sort, List<T> list) {
        List<T> l = new ArrayList<>(list);
        Collections.sort(l, (o1, o2) -> {
            Class<?> fieldType = ReflectionUtil.getFieldType(o1.getClass(), sort.getField());
            if (fieldType != null) {
                Object val1 = ReflectionUtil.getFieldValue(o1, sort.getField());
                Object val2 = ReflectionUtil.getFieldValue(o2, sort.getField());
                if (ReflectionUtil.is(fieldType, Comparable.class)) {
                    int compare = val1.toString().compareTo(val2.toString());
                    if (sort.getDirection() == SearchSort.SortDirection.ASC) {
                        return compare;
                    }
                    return -compare;
                }
            }
            return 0;
        });
        return l;
    }

    public static <T> Page<T> page(SearchPage searchPage, List<T> list) {
        if (searchPage.getSort() != null) {
            list = sort(searchPage.getSort(), list.stream().filter(item -> searchPage.predicate(item, SearchLogic.AND)).toList());
        }
        return page(PageRequest.of(searchPage.getPage(), searchPage.getPageSize()),
                list.stream().filter(item -> searchPage.predicate(item, SearchLogic.AND)).toList());
    }

    public static <T> SearchResult<T> searchResult(SearchPage searchPage, List<T> list) {
        if (searchPage.getSort() != null) {
            list = sort(searchPage.getSort(), list);
        }
        if (searchPage.getPageSize() == 0) {
            return new SearchResult<>(searchPage.getPage(), searchPage.getPageSize(),
                    Long.valueOf(list.size()), list);
        }
        Page<T> page = page(searchPage, list);
        return new SearchResult<>(searchPage.getPage(), searchPage.getPageSize(),
                page.getTotalElements(), page.stream().toList());
    }
}
