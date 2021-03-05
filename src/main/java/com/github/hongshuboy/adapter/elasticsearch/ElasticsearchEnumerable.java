package com.github.hongshuboy.adapter.elasticsearch;

import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerator;

public class ElasticsearchEnumerable extends AbstractEnumerable<Object[]> {
    @Override
    public Enumerator<Object[]> enumerator() {
        return new ElasticsearchEnumerator();
    }

    static class ElasticsearchEnumerator implements Enumerator<Object[]>{
        @Override
        public Object[] current() {
            return new Object[0];
        }

        @Override
        public boolean moveNext() {
            return false;
        }

        @Override
        public void reset() {

        }

        @Override
        public void close() {

        }

        public ElasticsearchEnumerator() {

        }
    }
}
