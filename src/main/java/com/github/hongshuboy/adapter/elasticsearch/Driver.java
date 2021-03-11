package com.github.hongshuboy.adapter.elasticsearch;

import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class Driver extends org.apache.calcite.jdbc.Driver {
    @SuppressWarnings("all")
    private static final String CONNECT_STRING_PREFIX = "jdbc:hongshuboy:";

    static {
        new Driver().register();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Connection connection = super.connect(url, info);
        final CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        final SchemaPlus rootSchema = calciteConnection.getRootSchema();
        rootSchema.add("es5", new ElasticsearchSchema());
        calciteConnection.setSchema("es5");
        return connection;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return super.acceptsURL(url);
    }

    @Override
    protected String getConnectStringPrefix() {
        return CONNECT_STRING_PREFIX;
    }

    @Override
    protected void register() {
        super.register();
    }
}
