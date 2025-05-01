package babak.springboot.data.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
@Getter
@Setter
@AllArgsConstructor
public class SearchResult<T> {

    public final static SearchResult EMPTY = new SearchResult<>();

    public SearchResult() {
        this.page = 0;
        this.pageSize = 10;
        this.total = 0L;
        this.list = new ArrayList<>();
    }

    private Integer page;
    private Integer pageSize;
    private Long total;
    private List<T> list;

    public <M> SearchResult<M> mutate(Function<T, M> mutateFunction) {
        SearchResult<M> searchResult = new SearchResult<>();
        searchResult.setPage(this.page);
        searchResult.setPageSize(this.pageSize);
        searchResult.setTotal(this.total);
        searchResult.setList(this.list.stream().map(mutateFunction).toList());
        return searchResult;
    }
}
