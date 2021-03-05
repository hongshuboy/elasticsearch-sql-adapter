package com.github.hongshuboy.adapter.elasticsearch.util;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.elasticsearch.node.Node;

import java.io.IOException;

public class ElasticsearchConnection {

    /**
     * get elasticsearch connection
     * @param host eg. 127.0.0.1
     * @return es connection
     */
    public static RestHighLevelClient getConnection(String host){
        return getConnection(host, 9200);
    }

    public static RestHighLevelClient getConnection(String host, int port){
        /*Node[] nodes = {new Node(HttpHost.create(host.concat(":") + port))};
        RestClientBuilder clientBuilder = RestClient.builder(nodes);
        RestHighLevelClient client = new RestHighLevelClient(clientBuilder);
        try {
            boolean ping = client.ping(RequestOptions.DEFAULT);
            System.out.println("ES cluster connected:" + ping);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return client;*/
        return new RestHighLevelClient(RestClient.builder(new HttpHost("ihongshu.top", 9200)).build());
    }
}
