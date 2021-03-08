package com.test.es;

import com.github.hongshuboy.adapter.elasticsearch.ElasticsearchTableManager;
import com.github.hongshuboy.adapter.elasticsearch.util.ElasticsearchConnection;
import com.github.hongshuboy.adapter.elasticsearch.util.FieldType;
import com.google.common.collect.Maps;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Map;

public class ElasticsearchSQLTest {
    public static final String SQL = "select age,count(*) as num from person group by age";

    @Before
    public void init() {
        RestHighLevelClient client = ElasticsearchConnection.getConnection("ihongshu.top", 9200);
    }

    @After
    public void close() throws IOException {
    }

    @Test
    public void testElasticsearchSQL() throws Exception {
        Class.forName("com.github.hongshuboy.adapter.elasticsearch.Driver");
        ElasticsearchTableManager.setDefaultESHostAndPort("ihongshu.top", 9200);
        ElasticsearchTableManager.addIndex("person", "person", Arrays.asList("name", "age", "address", "room"), Arrays.asList(FieldType.STRING, FieldType.INT, FieldType.STRING, FieldType.INT));
        Connection connection = DriverManager.getConnection("jdbc:hongshuboy:lex=JAVA");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SQL);
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
