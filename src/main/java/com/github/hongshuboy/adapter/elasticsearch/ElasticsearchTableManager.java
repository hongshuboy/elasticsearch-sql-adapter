package com.github.hongshuboy.adapter.elasticsearch;

import com.github.hongshuboy.adapter.elasticsearch.util.FieldType;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ElasticsearchTableManager {
    private static final CopyOnWriteArrayList<TableMeta> tableMetas = new CopyOnWriteArrayList<>();
    private static String defaultESHost;
    private static int defaultESPort;

    /**
     * add elasticsearch index to connection
     *
     * @param index    elasticsearch
     * @param alias    table alias name
     * @param columns  columns in index that you want to in table
     * @param sqlTypes table column types
     */
    public static void createTable(String index, String alias, List<String> columns, List<FieldType> sqlTypes) {
        Objects.requireNonNull(defaultESHost, "es host must be set first, use setDefaultESHostAndPort");
        createTable(defaultESHost, defaultESPort, index, alias, columns, sqlTypes);
    }

    /**
     * @see ElasticsearchTableManager#createTable(java.lang.String, java.lang.String, java.util.List, java.util.List)
     */
    public static void createTable(String esHost, int port, String index, String alias, List<String> columns, List<FieldType> sqlTypes) {
        assert columns.size() == sqlTypes.size();
        tableMetas.add(new TableMeta(esHost, port, index, alias, columns, sqlTypes));
    }

    /**
     * Elasticsearch host and port
     *
     * @param defaultESHost ip or host:9200
     */
    public static void setDefaultESHostAndPort(String defaultESHost, int port) {
        ElasticsearchTableManager.defaultESHost = defaultESHost;
        ElasticsearchTableManager.defaultESPort = port;
    }

    public static Iterator<TableMeta> tableMetaIterator() {
        return tableMetas.iterator();
    }


    static class TableMeta {
        String esHost;
        int esPort;
        String index;
        String alias;
        List<String> fields;
        List<FieldType> javaTypes;

        public TableMeta(String host, int port, String index, String alias, List<String> fields, List<FieldType> javaTypes) {
            this.esPort = port;
            this.esHost = host;
            this.index = index;
            this.alias = alias;
            this.fields = fields;
            this.javaTypes = javaTypes;
        }
    }
}
