# 使用指南

`ElasticSearch`SQL查询适配器，为`Elasticsearch`提供SQL查询的功能。支持完整的SQL查询和跨`Elasticsearch`集群查询。通过代码简单配置，使项目根据`Elasticsearch`的`index`映射为`Table`。


| License                                        | CodeBeat                                                     | Language                                                     | Build                                                        | Size                                                         | Contributors                                                 |
| ---------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![Hex.pm](https://img.shields.io/hexpm/l/plug) | [![codebeat badge](https://codebeat.co/badges/419796f3-4288-4c5c-8398-bcbff3aa844c)](https://codebeat.co/projects/github-com-hongshuboy-elasticsearch-sql-adapter-master) | ![language java](<https://img.shields.io/badge/java-v1.8-blue>) | ![GitHub release (latest by date)](https://img.shields.io/github/v/release/hongshuboy/elasticsearch-sql-adapter) | ![GitHub repo size](https://img.shields.io/github/repo-size/hongshuboy/elasticsearch-sql-adapter) | ![GitHub contributors](https://img.shields.io/github/contributors/hongshuboy/elasticsearch-sql-adapter) |

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
//注册Driver
Class.forName("com.github.hongshuboy.adapter.elasticsearch.Driver");

//设置缺省状态下的Elasticsearch连接地址
ElasticsearchTableManager.setDefaultESHostAndPort("your es host", port);

//将index纳入SQL并设置index的schema
List<String> columns = Arrays.asList("name", "age", "address", "room");
List<FieldType> types = Arrays.asList(FieldType.STRING, FieldType.INT, FieldType.STRING, FieldType.INT);
//参数：index name/table alias/columns/types
ElasticsearchTableManager.createTable("person", "person", columns, types);

//get connection
Connection connection = DriverManager.getConnection("jdbc:hongshuboy:lex=JAVA");
Statement statement = connection.createStatement();

//execute query
ResultSet resultSet = statement.executeQuery("select * from person");
printResultSet(resultSet);
```

看一下`createTable`方法

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
void createTable(String index, 
              String alias, 
              List<String> columns, 
              List<FieldType> sqlTypes) {
    //...
}
```

*因为Elasticsearch不推荐，也以实际行动移除了type，所以这里考虑后还是使用`index`对应一个`SQL`表的对应关系*

**跨Elasticsearch集群**

如果单集群添加表，为了避免麻烦，可以提前设置默认的集群连接，在不指定`Elasticsearch`连接地址的情况下，都会默认使用这里设置的连接地址

```java
ElasticsearchTableManager.setDefaultESHostAndPort("your es host", port);
```

如果跨多个集群，可以使用带有`esHost`和`port`的`createTable`方法，这样一来，在`SQL`中就可以直接进行两表的`join`等操作

```java
/**
 * @see ElasticsearchTableManager#createTable(String, String, List, List)
 */
public static void createTable(String esHost, int port, String index, String alias, List<String> columns, List<FieldType> sqlTypes) {
    assert columns.size() == sqlTypes.size();
    tableMetas.add(new TableMeta(esHost, port, index, alias, columns, sqlTypes));
}
```

**关于Schema**

查询时不需要指定数据库，直接使用`table` `name`即可

如果你添加了很多`index`，应该考虑使用`alias`区别

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

*其他如`join`查询等也都支持，可自行测试。*

## 作者 Author

弘树丶

> wangpeng(hongshu)

Email:hongshuboy@gmail.com

## 版权说明 License 

本项目使用**Apache License 2.0**授权许可，详情请参阅 ***\LICENSE*** 和 ***\NOTICE***

*hongshuboy/elasticsearch-sql-adapteris licensed under the Apache License 2.0,please read LICENSE and NOTICE for more information*

Copyright ©2020 wangpeng(hongshu)

