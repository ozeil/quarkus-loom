package com.oz.loomtest;

import io.vertx.mutiny.sqlclient.Row;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Pet {

    public Long id;

    public String name;

    public Pet(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Pet from(Row row) {
        return new Pet(
                row.getLong("id"),
                row.getString("name")
        );
    }

    public static Pet from(ResultSet result) throws SQLException {
        return new Pet(
                result.getLong("id"),
                result.getString("name")
        );
    }
}
