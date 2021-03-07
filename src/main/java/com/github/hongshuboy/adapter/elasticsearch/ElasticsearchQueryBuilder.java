package com.github.hongshuboy.adapter.elasticsearch;

import com.github.hongshuboy.adapter.elasticsearch.util.ElasticsearchConnection;
import org.apache.calcite.rex.RexNode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;

public class ElasticsearchQueryBuilder {
    private final ElasticsearchTableManager.TableMeta tableMeta;
    private final List<RexNode> filters;

    public SearchResponse build() throws IOException {
        RestHighLevelClient client = ElasticsearchConnection.getConnection(tableMeta.esHost, tableMeta.esPort);
        SearchRequest searchRequest = new SearchRequest(tableMeta.index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        return client.search(searchRequest);
    }

    public ElasticsearchQueryBuilder(ElasticsearchTableManager.TableMeta tableMeta, List<RexNode> filters) {
        this.tableMeta = tableMeta;
        this.filters = filters;
    }
}
