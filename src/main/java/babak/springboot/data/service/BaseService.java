package babak.springboot.data.service;

import babak.springboot.data.domain.BaseEntity;
import babak.springboot.data.mapper.MappingObjectConverter;
import babak.springboot.data.pagination.SearchResult;
import babak.springboot.data.repository.BaseRepository;
import babak.springboot.data.search.SearchFilterModel;
import babak.springboot.data.specification.BaseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
public abstract class BaseService<E extends BaseEntity, PK extends Serializable, R extends BaseRepository<E, PK>> {

    private final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    protected R getRepository() {
        return repository;
    }

    public Optional<E> find(PK id) {
        return getRepository().findById(id);
    }

    public E submit(E entity) {
        return getRepository().save(entity);
    }

    public void delete(E entity) {
        getRepository().delete(entity);
    }

    public void deleteLogically(E entity) {
        entity.setDeleted(true);
        submit(entity);
    }

    protected <F extends SearchFilterModel> Page<E> search(F filterModel) {
        BaseSpecification<E, F> specification = new BaseSpecification<>(filterModel);
        return getRepository().findAll(specification,
                PageRequest.of(filterModel.getPage(), filterModel.getPageSize(),
                        filterModel.getSort() != null ?
                                filterModel.getSort().toSort() :
                                Sort.by(Sort.Direction.DESC, "creationDate")));
    }

    public <F extends SearchFilterModel> SearchResult<Map<String, Object>> searchResult(F filterModel) {
        Page<E> page = search(filterModel);
        SearchResult<E> result = new SearchResult<>(page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getContent());
        return result.mutate(MappingObjectConverter::convert);
    }
}
