package com.github.hongshuboy.adapter.elasticsearch;

import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ElasticsearchSchema extends AbstractSchema {
    /**
     * index对应table
     */
    @Override
    protected Map<String, Table> getTableMap() {
        Map<String, Table> tableMap = new HashMap<>();
        Iterator<ElasticsearchTableManager.TableMeta> iterator = ElasticsearchTableManager.tableMetaIterator();
        while (iterator.hasNext()) {
            ElasticsearchTableManager.TableMeta tableMeta = iterator.next();
            tableMap.put(tableMeta.alias, new ElasticsearchTable(tableMeta));
        }
        return tableMap;
    }
}
