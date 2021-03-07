package com.github.hongshuboy.adapter.elasticsearch;

import com.github.hongshuboy.adapter.elasticsearch.util.FieldType;
import org.apache.calcite.avatica.util.DateTimeUtils;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rex.RexNode;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class ElasticsearchEnumerable extends AbstractEnumerable<Object[]> {
    private final ElasticsearchTableManager.TableMeta tableMeta;
    private final RowConverter<Object[]> rowConverter;
    private final List<RexNode> filters;
    private static final FastDateFormat TIME_FORMAT_DATE;
    private static final FastDateFormat TIME_FORMAT_TIME;
    private static final FastDateFormat TIME_FORMAT_TIMESTAMP;

    static {
        final TimeZone gmt = TimeZone.getTimeZone("UTC+8");
        TIME_FORMAT_DATE = FastDateFormat.getInstance("yyyy-MM-dd", gmt);
        TIME_FORMAT_TIME = FastDateFormat.getInstance("HH:mm:ss", gmt);
        TIME_FORMAT_TIMESTAMP =
                FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", gmt);
    }

    public ElasticsearchEnumerable(ElasticsearchTableManager.TableMeta tableMeta, List<RexNode> filters) {
        this.tableMeta = tableMeta;
        rowConverter = new ArrayRowConverter();
        this.filters = filters;
    }

    @Override
    public Enumerator<Object[]> enumerator() {
        return new ElasticsearchEnumerator();
    }

    class ElasticsearchEnumerator implements Enumerator<Object[]> {
        private final SearchHit[] hits;
        private int cursor = -1;
        private final int limit;

        @Override
        public Object[] current() {
            return getCurrent();
        }

        private Object[] getCurrent() {
            Map<String, Object> source = hits[cursor].getSource();
            List<String> list = new ArrayList<>(ElasticsearchEnumerable.this.tableMeta.fields.size());
            for (String field : ElasticsearchEnumerable.this.tableMeta.fields) {
                list.add(source.get(field).toString());
            }
            return rowConverter.convertRow(list.toArray(new String[0]));
        }

        @Override
        public boolean moveNext() {
            return ++cursor <= limit - 1;
        }

        @Override
        public void reset() {

        }

        @Override
        public void close() {
        }

        public ElasticsearchEnumerator() {
            SearchResponse response;
            try {
                response = new ElasticsearchQueryBuilder(ElasticsearchEnumerable.this.tableMeta, ElasticsearchEnumerable.this.filters).build();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            hits = response.getHits().getHits();
            limit = hits.length;
        }
    }

    abstract static class RowConverter<E> {
        abstract E convertRow(String[] rows);
    }

    class ArrayRowConverter extends RowConverter<Object[]> {
        @Override
        Object[] convertRow(String[] rows) {
            return convertNormalRow(rows);
        }

        public Object[] convertNormalRow(String[] strings) {
            List<String> fields = ElasticsearchEnumerable.this.tableMeta.fields;
            final Object[] objects = new Object[fields.size()];
            for (int i = 0; i < fields.size(); i++) {
                objects[i] = convert(ElasticsearchEnumerable.this.tableMeta.javaTypes.get(i), strings[i]);
            }
            return objects;
        }

        /**
         * 单列类型转换
         *
         * @param fieldType    类型
         * @param originString 转换前的字符量
         * @return 转换后的值
         */
        protected Object convert(FieldType fieldType, String originString) {
            if (fieldType == null || originString == null) {
                return originString;
            }
            switch (fieldType) {
                case BOOLEAN:
                    if (originString.length() == 0) {
                        return null;
                    }
                    return Boolean.parseBoolean(originString);
                case SHORT:
                    if (originString.length() == 0) {
                        return null;
                    }
                    return Short.parseShort(originString);
                case INT:
                    if (originString.length() == 0) {
                        return null;
                    }
                    return Integer.parseInt(originString);
                case LONG:
                    if (originString.length() == 0) {
                        return null;
                    }
                    return Long.parseLong(originString);
                case FLOAT:
                    if (originString.length() == 0) {
                        return null;
                    }
                    return Float.parseFloat(originString);
                case DOUBLE:
                    if (originString.length() == 0) {
                        return null;
                    }
                    return Double.parseDouble(originString);
                case DATE:
                    if (originString.length() == 0) {
                        return null;
                    }
                    try {
                        Date date = TIME_FORMAT_DATE.parse(originString);
                        return (int) (date.getTime() / DateTimeUtils.MILLIS_PER_DAY);
                    } catch (ParseException e) {
                        return null;
                    }
                case TIME:
                    if (originString.length() == 0) {
                        return null;
                    }
                    try {
                        Date date = TIME_FORMAT_TIME.parse(originString);
                        return (int) date.getTime();
                    } catch (ParseException e) {
                        return null;
                    }
                case TIMESTAMP:
                    if (originString.length() == 0) {
                        return null;
                    }
                    try {
                        Date date = TIME_FORMAT_TIMESTAMP.parse(originString);
                        return date.getTime();
                    } catch (ParseException e) {
                        return null;
                    }
                case STRING:
                default:
                    return originString;
            }
        }
    }
}
