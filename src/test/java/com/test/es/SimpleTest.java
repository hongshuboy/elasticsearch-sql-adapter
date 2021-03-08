package com.test.es;

import com.github.hongshuboy.adapter.elasticsearch.util.ElasticsearchConnection;
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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class SimpleTest {
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
        System.out.println(MessageFormat.format("总共花费时间：{0},找到结果{1}个", response.getTook(), response.getHits().getTotalHits()));
        for (SearchHit hit : response.getHits().getHits()) {
            System.out.println(hit.getSource().get("name"));
            System.out.println(hit.getSource().get("age"));
        }
    }

    @Test
    public void test4() throws Exception {
        RestHighLevelClient client = ElasticsearchConnection.getConnection("ihongshu.top", 9200);
        SearchRequest searchRequest = new SearchRequest("person");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("age", "25"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest);
        System.out.println(MessageFormat.format("总共花费时间：{0},找到结果{1}个", response.getTook(), response.getHits().getTotalHits()));
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", hit.getSource().get("name"));
            map.put("age", hit.getSource().get("age"));
            map.put("address", hit.getSource().get("address"));
            map.put("room", hit.getSource().get("room"));
            System.out.println(map);
        }
    }
}
