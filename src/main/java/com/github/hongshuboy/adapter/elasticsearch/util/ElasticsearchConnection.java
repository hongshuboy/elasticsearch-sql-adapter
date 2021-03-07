package com.github.hongshuboy.adapter.elasticsearch.util;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.elasticsearch.node.Node;

import java.io.IOException;

public class ElasticsearchConnection {

    public static RestHighLevelClient getConnection(String host, int port){
        return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port)).build());
    }
}
