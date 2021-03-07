package com.test.es;

import com.github.hongshuboy.adapter.elasticsearch.ElasticsearchTableManager;
import com.github.hongshuboy.adapter.elasticsearch.util.ElasticsearchConnection;
import com.github.hongshuboy.adapter.elasticsearch.util.FieldType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Test1 {
    private RestHighLevelClient client;
    @Before
    public void init() {
        client = ElasticsearchConnection.getConnection("ihongshu.top", 9200);
    }

    @After
    public void close() throws IOException {
    }

    @Test
    public void test1() throws IOException {
        SearchRequest searchRequest = new SearchRequest("person");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest);
        System.out.println(MessageFormat.format("总共花费时间：{0},找到结果{1}个",response.getTook(),response.getHits().getTotalHits()));
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSource().get("name"));
            System.out.println(hit.getSource().get("age"));
        }
    }

    @Test
    public void test3() throws Exception{
    }

    @Test
    public void test2() throws Exception{
        Class.forName("com.github.hongshuboy.adapter.elasticsearch.Driver");
        ElasticsearchTableManager.setDefaultESHostAndPort("ihongshu.top", 9200);
        ElasticsearchTableManager.addIndex("person", "person", Arrays.asList("name", "age", "address", "room"), Arrays.asList(FieldType.STRING, FieldType.INT, FieldType.STRING, FieldType.INT));
        Connection connection = DriverManager.getConnection("jdbc:hongshuboy:lex=JAVA");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from person");
        printResultSet(resultSet);
    }

    private static void printResultSet(ResultSet resultSet) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnSize = metaData.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> map = Maps.newLinkedHashMap();
            for (int i = 1; i < columnSize + 1; i++) {
                map.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            System.out.println(map);
        }
    }
}
