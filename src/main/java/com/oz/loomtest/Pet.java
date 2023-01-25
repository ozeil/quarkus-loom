package com.oz.loomtest;

import io.vertx.mutiny.sqlclient.Row;

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

}
