package babak.springboot.data.model;

import babak.springboot.data.domain.BaseEntity;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Author: Babak Behzadi
 * Email: behzadi.babak@gmail.com
 **/
public abstract class BaseModel<PK extends Serializable, E extends BaseEntity> extends LinkedHashMap<String, Object> {

    protected BaseModel() {
    }

    protected BaseModel(E e) {
        put("id", e.getId());
        map(e);
    }

    public PK getId() {
        return (PK) get("id");
    }

    public void setId(PK id) {
        put("id", id);
    }

    public abstract void map(E e);
}
