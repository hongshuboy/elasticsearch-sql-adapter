package com.github.hongshuboy.adapter.elasticsearch;

import com.github.hongshuboy.adapter.elasticsearch.util.ElasticsearchConnection;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlKind;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

public class ElasticsearchQueryBuilder {
    private final ElasticsearchTableManager.TableMeta tableMeta;
    /**
     * conditions
     */
    private FilterEqualsCondition filterEqualsCondition;
    private LessThanCondition lessThanCondition;
    private GreaterThanCondition greaterThanCondition;
    /**
     * elasticsearch
     */
    private RestHighLevelClient client;
    private SearchRequest searchRequest;
    private SearchSourceBuilder searchSourceBuilder;


    public SearchResponse build() throws IOException {
        searchSourceBuilder.query(deduceQueryBuilder());
        searchRequest.source(searchSourceBuilder);
        return client.search(searchRequest);
    }

    public ElasticsearchQueryBuilder(ElasticsearchTableManager.TableMeta tableMeta, List<RexNode> filters) {
        this.tableMeta = tableMeta;
        //transform
        filters.forEach(this::filter);
        //prepare elasticsearch client
        prepareElasticsearchClient(tableMeta);
    }

    private void prepareElasticsearchClient(ElasticsearchTableManager.TableMeta tableMeta) {
        client = ElasticsearchConnection.getConnection(tableMeta.esHost, tableMeta.esPort);
        searchRequest = new SearchRequest(tableMeta.index);
        searchSourceBuilder = new SearchSourceBuilder();
    }

    private QueryBuilder deduceQueryBuilder() {
        if (Objects.nonNull(filterEqualsCondition)) {
            return QueryBuilders.matchQuery(filterEqualsCondition.columnName, filterEqualsCondition.literal);
        } else if (Objects.nonNull(lessThanCondition)) {
            return QueryBuilders.rangeQuery(lessThanCondition.columnName).lt(lessThanCondition.literal);
        } else if (Objects.nonNull(greaterThanCondition)) {
            return QueryBuilders.rangeQuery(greaterThanCondition.columnName).gt(greaterThanCondition.literal);
        } else {
            return QueryBuilders.matchAllQuery();
        }
    }

    public void filter(RexNode filter) {
        try {
            if (filter.isA(SqlKind.EQUALS)) {
                filterEqualsCondition = prepareCondition(filter, FilterEqualsCondition.class);
            } else if (filter.isA(SqlKind.LESS_THAN)) {
                lessThanCondition = prepareCondition(filter, LessThanCondition.class);
            } else if (filter.isA(SqlKind.GREATER_THAN)) {
                greaterThanCondition = prepareCondition(filter, GreaterThanCondition.class);
            }
            //...
        } catch (Exception ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseCondition> T prepareCondition(RexNode filter, Class<T> cls) throws Exception {
        final RexCall call = (RexCall) filter;
        RexNode left = call.getOperands().get(0);
        if (left.isA(SqlKind.CAST)) {
            left = ((RexCall) left).operands.get(0);
        }
        final RexNode right = call.getOperands().get(1);
        if (left instanceof RexInputRef
                && right instanceof RexLiteral) {
            final int index = ((RexInputRef) left).getIndex();
            Constructor<?> constructor = cls.getConstructor(String.class, Object.class);
            return (T) constructor.newInstance(tableMeta.fields.get(index), ((RexLiteral) right).getValue2());
        }
        return null;
    }


    /**
     * base class for query condition
     */
    abstract static class BaseCondition {
        String columnName;
        Object literal;

        public BaseCondition(String columnName, Object literal) {
            this.columnName = columnName;
            this.literal = literal;
        }
    }

    /**
     * @see SqlKind#EQUALS
     * @see ElasticsearchQueryBuilder#deduceQueryBuilder()
     */
    static class FilterEqualsCondition extends BaseCondition {
        public FilterEqualsCondition(String columnName, Object literal) {
            super(columnName, literal);
        }
    }


    /**
     * @see SqlKind#LESS_THAN
     * @see ElasticsearchQueryBuilder#deduceQueryBuilder()
     */
    static class LessThanCondition extends BaseCondition {
        public LessThanCondition(String columnName, Object literal) {
            super(columnName, literal);
        }
    }

    /**
     * @see SqlKind#GREATER_THAN
     * @see ElasticsearchQueryBuilder#deduceQueryBuilder()
     */
    static class GreaterThanCondition extends BaseCondition {
        public GreaterThanCondition(String columnName, Object literal) {
            super(columnName, literal);
        }
    }

}
