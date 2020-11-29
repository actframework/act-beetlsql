package actissue.beetlsql.gh20;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.List;

public class BaseServiceImpl<T, ID> implements BaseService<T, ID> {
    @Inject
    protected BaseDao<T, ID> baseDao;

    public T findById(ID id) throws EntityNotFoundException{
        T entity = (T) baseDao.findById(id);
        if (entity == null)
            throw new EntityNotFoundException("entity not found in database");
        return entity;
    }

    public T deleteById(ID id) throws EntityNotFoundException{
        T entity = this.findById(id);
        baseDao.delete(entity);
        return entity;
    }

    public T insert(T entity) {
        return entity;
    }

    public T update(T entity) {
        return entity;
    }

    public T save(T entity) {
        baseDao.save(entity);
        return entity;
    }

    public List<T> insert(List<T> entitys) {
        return entitys;
    }

    public List<T> update(List<T> entitys) {
        return entitys;
    }

    public List<T> all() {
        return baseDao.findAllAsList();
    }
}
