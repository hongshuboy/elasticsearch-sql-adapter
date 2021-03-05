package com.github.hongshuboy.adapter.elasticsearch;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.FilterableTable;
import org.apache.calcite.schema.impl.AbstractTable;

import java.util.List;

public class ElasticsearchTable extends AbstractTable implements FilterableTable {
    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        return null;
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root, List<RexNode> filters) {
        return new ElasticsearchEnumerable();
    }

    private boolean addFilter(RexNode filter, Object[] filterValues) {

        return false;
    }
}
