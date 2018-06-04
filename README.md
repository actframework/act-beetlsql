# BeetlSQL Plugin for ActFramework

Allow ActFramework application to use [BeetlSQL](http://ibeetl.com/)

**Special note on v1.5.0**

Because Act DB updated the transaction handling framework with act-sql-common-1.4.0, it will not send events about TX entering and exit, instead it just set the global state `TxContext`, which should be pulled by each database access layer implementation when tx is really needed. For example

* act-jpa-common - The TxContext will get visited when

    1. `JPADao`'s save/delete/update methods get called
    2. `EntityManager` instance is about to get injected

* act-ebean - The TxContext will get visited when
    1. `EbeanDao`'s save/delete/upate methods get called

Unfortunately beetlsql integration doesn't provide the support for the above mechanism. Thus in order to apply transactional scope to your app with beetlsql as the database access layer, you must use `@With(BeetlSqlTransactional.class)` as demonstrated below:

```java
@SuppressWarnings("unused")
@With(BeetlSqlTransactional.class)
public class Todo {

    @Inject
    private TodoItem.Mapper mapper;

    @GetAction
    public void home() {}

    @GetAction("/list")
    public Iterable<TodoItem> list(String q) {
    		// mapper.all();
        return mapper.all();
    }

    @PostAction("/list")
    public void post(String desc) {
        TodoItem item = new TodoItem();
        item.setDesc(desc);
        mapper.insert(item);
    }

}
```

