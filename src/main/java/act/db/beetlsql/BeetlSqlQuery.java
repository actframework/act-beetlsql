package act.db.beetlsql;

import act.db.Dao;
import org.beetl.sql.core.query.Query;

import java.util.List;
import java.util.StringTokenizer;

/**
 * 有状态的，操作完毕后，需要重新创建,而不是复用此类
 * @param <MODEL_TYPE>
 */
public class BeetlSqlQuery<MODEL_TYPE>  implements Dao.Query<MODEL_TYPE, BeetlSqlQuery<MODEL_TYPE>> {

  BeetlSqlDao dao = null;
  Class entityClass;
  String fields;
  Object[] values;
  Query<MODEL_TYPE> query = null;
  int pos =-1;
  int limit=-1 ;


  protected BeetlSqlQuery(BeetlSqlDao dao ,Class entityClass){
    this.dao = dao;
    this.entityClass = entityClass;
    createQuery(this.fields,this.values);
  }

  protected BeetlSqlQuery(BeetlSqlDao dao ,Class entityClass, String fields,Object[] values){
    this.dao = dao;
    this.entityClass = entityClass;
    this.fields = fields;
    this.values = values;
    createQuery(this.fields,this.values);
  }

  @Override
  public BeetlSqlQuery<MODEL_TYPE> offset(int pos) {
    this.pos = pos;
    return this;
  }

  @Override
  public BeetlSqlQuery<MODEL_TYPE> limit(int limit) {
    this.limit = limit;
    return this;
  }

  @Override
  public BeetlSqlQuery<MODEL_TYPE> orderBy(String... fieldList) {
    for(String orderBy:fieldList){
      query.orderBy(orderBy);
    }
    return this;
  }

  @Override
  public MODEL_TYPE first() {

    List<MODEL_TYPE> list = query.limit(1,1).select();
    if(list.size()==0){
      return null;
    }
    return list.get(0);

  }

  @Override
  public Iterable<MODEL_TYPE> fetch() {
    if(pos!=-1){
      return query.limit(pos,limit).select();
    }else{
      return query.select();
    }

  }

  @Override
  public long count() {
    return query.count();
  }



  protected  Query createQuery(String fields,Object[] values){
    query = dao.sqlManager.query(entityClass);
    if(fields==null){
      return query;
    }
    StringTokenizer st = new StringTokenizer(fields,":,; ");
    int index = 0;
    while(st.hasMoreTokens()){
      query.andEq(st.nextToken(),values[index++]);
    }

    return query;
  }
}
