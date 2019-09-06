package act.db.beetlsql;

import act.db.DaoBase;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.db.ClassDesc;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.test.BaseDao;

import java.util.*;


public class BeetlSqlDao<ID_TYPE, MODEL_TYPE> extends DaoBase<ID_TYPE, MODEL_TYPE, BeetlSqlQuery<MODEL_TYPE>> {
  SQLManager sqlManager = null;
  Class targetType = null;
  String idAttr ;
  public BeetlSqlDao(SQLManager sqlManager,String idAttr,Class<MODEL_TYPE> modelType,Class<ID_TYPE> idType){
    super(idType,modelType);
    this.idAttr = idAttr;
    this.sqlManager = sqlManager;
    this.targetType = modelType;
  }


  @Override
  public MODEL_TYPE findById(ID_TYPE id) {
    return sqlManager.single(this.modelType(),id);
  }

  @Override
  public MODEL_TYPE findLatest() {
    throw new UnsupportedOperationException();
  }

  @Override
  public MODEL_TYPE findLastModified() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterable<MODEL_TYPE> findByIdList(Collection<ID_TYPE> idList) {
   //TODO,优化成 "select * from tableName where id in "
   List<MODEL_TYPE> list = new ArrayList<>(idList.size());
   for(ID_TYPE  id:idList){
     list.add(findById(id));
   }
   return list;
  }

  @Override
  public MODEL_TYPE reload(MODEL_TYPE entity) {
    return this.sqlManager.unique(this.modelType(),this.getId(entity));
  }

  @Override
  public ID_TYPE getId(MODEL_TYPE entity) {
    String tableName = this.sqlManager.getNc().getTableName(this.modelType());
    TableDesc tableDesc = this.sqlManager.getMetaDataManager().getTable(tableName);
    ClassDesc classDesc = tableDesc.getClassDesc(this.sqlManager.getNc());
    List<String> idNames = classDesc.getIdAttrs();
    if(idNames.size()>1){
      //TODO 以后支持复合主健
      throw new IllegalStateException("BeetlSQL 目前不支持在ACT中使用复合主健");
    }
    String idAttr = idNames.get(0);
    ID_TYPE value = (ID_TYPE)BeanKit.getBeanProperty(entity,idAttr);
    return value;
  }

  @Override
  public MODEL_TYPE save(MODEL_TYPE entity) {
     sqlManager.insert(entity,true);
     return entity;
  }

  @Override
  public void save(MODEL_TYPE entity, String fields, Object... values) {
    MODEL_TYPE dbEntity = this.reload(entity);
    StringTokenizer st = new StringTokenizer(fields,":,; ");
    int index = 0;
    while(st.hasMoreTokens()){
      BeanKit.setBeanProperty(dbEntity,values[index++],st.nextToken());
    }
    sqlManager.updateById(dbEntity);
  }

  @Override
  public List<MODEL_TYPE> save(Iterable<MODEL_TYPE> entities) {
    List list = new ArrayList();
    Iterator<MODEL_TYPE> it =entities.iterator();
    while(it.hasNext()){
      MODEL_TYPE obj = it.next();
      sqlManager.insert(modelType(),obj,true);
      list.add(obj);
    }

    return list;
  }

  @Override
  public void delete(MODEL_TYPE entity) {
    this.sqlManager.deleteById(this.modelType(),this.getId(entity));
  }

  @Override
  public void delete(BeetlSqlQuery<MODEL_TYPE> query) {
     query.query.delete();
  }

  @Override
  public void deleteById(ID_TYPE id) {
    sqlManager.deleteById(this.modelType(),id);
  }

  @Override
  public void deleteBy(String fields, Object... values) throws IllegalArgumentException {
    BeetlSqlQuery<MODEL_TYPE> query = createQuery(fields, values);
    Iterator<MODEL_TYPE> it = query.fetch().iterator();
    while(it.hasNext()){
      MODEL_TYPE entity = it.next();
      delete(entity);
    }

  }
  @Override
  public void deleteAll() {
    String tableName = this.sqlManager.getNc().getTableName(this.modelType());
    String sql = "delete from "+tableName;
    sqlManager.executeUpdate(new SQLReady(sql));
  }

  @Override
  public void drop() {
    throw new UnsupportedOperationException("BeetlSQL 不支持DDL 删除表");
  }

  @Override
  public BeetlSqlQuery<MODEL_TYPE> q() {
    return createQuery();
  }

  @Override
  public BeetlSqlQuery<MODEL_TYPE> createQuery() {
    return new BeetlSqlQuery<>(this,this.modelType());
  }

  @Override
  public BeetlSqlQuery<MODEL_TYPE> q(String fields, Object... values) {
    return createQuery(fields,values);
  }

  @Override
  public BeetlSqlQuery<MODEL_TYPE> createQuery(String fields, Object... values) {
    return new BeetlSqlQuery<>(this,this.modelType(),fields,values);
  }
}
