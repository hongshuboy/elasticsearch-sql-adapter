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

public class Test1 {
    private RestHighLevelClient client;
    @Before
    public void init() {
        client = ElasticsearchConnection.getConnection("ihongshu.top");
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
            System.out.println(hit.getSourceAsString());
        }
    }
}
