# 使用指南

`ElasticSearch`SQL查询适配器，为`Elasticsearch`提供SQL查询的功能。支持跨`Elasticsearch`集群查询。通过代码简单配置，使项目根据`Elasticsearch`的`index`映射为`SQL` `Table`

在`Elasticsearch`上创建一个`person`索引，有四个字段`name`,`age`,`address`,`room`

```sql
select * from person
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



```java
Class.forName("com.github.hongshuboy.adapter.elasticsearch.Driver");
ElasticsearchTableManager.setDefaultESHostAndPort("ihongshu.top", 9200);
ElasticsearchTableManager.addIndex("person", "person", Arrays.asList("name", "age", "address", "room"), Arrays.asList(FieldType.STRING, FieldType.INT, FieldType.STRING, FieldType.INT));
Connection connection = DriverManager.getConnection("jdbc:hongshuboy:lex=JAVA");
Statement statement = connection.createStatement();
ResultSet resultSet = statement.executeQuery("select * from person");
printResultSet(resultSet);
```

