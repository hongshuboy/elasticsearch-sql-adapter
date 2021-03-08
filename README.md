# 使用指南

`ElasticSearch`SQL查询适配器，为`Elasticsearch`提供SQL查询的功能。支持跨`Elasticsearch`集群查询。通过代码简单配置，使项目根据`Elasticsearch`的`index`映射为`SQL` `Table`

## 效果展示

在`Elasticsearch`上创建一个`person`索引，有四个字段`name`,`age`,`address`,`room`

```sql
select name,age,address,room from person
```

| name         | age  | address  | room |
| ------------ | ---- | -------- | ---- |
| zhangwei     | 26   | shanghai | 3602 |
| yifei        | 25   | shanghai | 3601 |
| youyou       | 24   | sichuan  | 3602 |
| lvziqiao     | 25   | shanghai | 3602 |
| zengxiaoxian | 26   | shanghai | 3601 |
| meijia       | 24   | shanghai | 3601 |
| guangu       | 26   | dongjing | 3602 |

**Java使用方法**

在`JVM`进程内，先使用`ElasticsearchTableManager`设置要使用SQL管理的`index`，可增加不同`Elasticsearch`集群的`index`实现跨集群查询

```java
Class.forName("com.github.hongshuboy.adapter.elasticsearch.Driver");
//设置缺省状态下的Elasticsearch连接地址
ElasticsearchTableManager.setDefaultESHostAndPort("ihongshu.top", 9200);
//将index纳入SQL并设置index的schema
ElasticsearchTableManager.addIndex("person", "person", Arrays.asList("name", "age", "address", "room"), Arrays.asList(FieldType.STRING, FieldType.INT, FieldType.STRING, FieldType.INT));
//get connection
Connection connection = DriverManager.getConnection("jdbc:hongshuboy:lex=JAVA");
Statement statement = connection.createStatement();
//execute query
ResultSet resultSet = statement.executeQuery("select * from person");
printResultSet(resultSet);
```

看一下`addIndex`方法

- `index`：`elasticsearch` `index`

- `alias`：table别名，SQL查询时使用的table name
- `columns`：table columns，字段名需要与`Elasticsearch`一致
- `sqlTypes`：字段类型

可以使用带有`esHost`和`port`的重载方法指定来自另一个`Elasticsearch`集群的`index`

```java
/**
 * add elasticsearch index to connection
 *
 * @param index    elasticsearch
 * @param alias    table alias name
 * @param columns  columns in index that you want to in table
 * @param sqlTypes table column types
 */
void addIndex(String index, 
              String alias, 
              List<String> columns, 
              List<FieldType> sqlTypes) {
    //...
}
```

*因为Elasticsearch不推荐，也以实际行动移除了type，所以这里考虑后还是使用`index`对应一个`SQL`表的对应关系*

如果你添加了很多`index`，应该考虑使用`alias`区别

**关于Schema**

查询时不需要指定数据库，直接使用`table` `name`即可

```sql
select name,age,address,room from person
```

**条件查询**

```sql
select * from person where age > 25
```


| name         | age  | address  | room |
| ------------ | ---- | -------- | ---- |
| zhangwei     | 26   | shanghai | 3602 |
| zengxiaoxian | 26   | shanghai | 3601 |
| guangu       | 26   | dongjing | 3602 |

**group by**


```sql
select age,count(*) as num from person group by age
```

| age  | num  |
| ---- | ---- |
| 24   | 2    |
| 25   | 2    |
| 26   | 3    |

## 作者 Author

弘树丶

> wangpeng(hongshu)

Email:hongshuboy@gmail.com

## 版权说明 License 

本项目使用**Apache License 2.0**授权许可，详情请参阅 ***\LICENSE*** 和 ***\NOTICE***

*hongshuboy/elasticsearch-sql-adapteris licensed under the Apache License 2.0,please read LICENSE and NOTICE for more information*

Copyright ©2020 wangpeng(hongshu)

