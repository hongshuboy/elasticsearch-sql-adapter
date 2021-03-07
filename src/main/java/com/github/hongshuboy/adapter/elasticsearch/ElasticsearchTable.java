package com.github.hongshuboy.adapter.elasticsearch;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.FilterableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class ElasticsearchTable extends AbstractTable implements FilterableTable {
    private final ElasticsearchTableManager.TableMeta tableMeta;

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        JavaTypeFactory javaTypeFactory = (JavaTypeFactory) typeFactory;
        List<String> fields = tableMeta.fields;
        List<RelDataType> types = tableMeta.javaTypes.stream().map(x -> x.toSQLType(javaTypeFactory)).collect(Collectors.toList());
        return javaTypeFactory.createStructType(Pair.zip(fields, types));
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root, List<RexNode> filters) {
        return new ElasticsearchEnumerable(tableMeta, filters);
    }

    public ElasticsearchTable(ElasticsearchTableManager.TableMeta tableMeta) {
        this.tableMeta = tableMeta;
    }
}
