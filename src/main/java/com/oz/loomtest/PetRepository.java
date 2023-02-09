package com.oz.loomtest;

import io.agroal.api.AgroalDataSource;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PetRepository {

    @Inject
    PgPool reactiveClient;

    @Inject
    AgroalDataSource jdbcDataSource;
    
    private static final String SELECT_ALL_PETS_QUERY = "SELECT * FROM pets";

    public List<Pet> findAllAwait() {
        List<Pet> pets = new ArrayList<>();
        RowSet<Row> rowSet = reactiveClient.preparedQuery(SELECT_ALL_PETS_QUERY).executeAndAwait();
        for (Row row : rowSet) {
            pets.add(Pet.from(row));
        }
        return pets;
    }

    public List<Pet> findAllBlocking() {
        List<Pet> pets = new ArrayList<>();
        try (Connection connection = jdbcDataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_PETS_QUERY)) {
                var result = statement.executeQuery();
                while (result.next()) {
                    pets.add(Pet.from(result));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    public Uni<List<Pet>> findAllReactive() {
        return reactiveClient.preparedQuery(SELECT_ALL_PETS_QUERY).execute().map(rows -> {
            List<Pet> ret = new ArrayList<>();
            for (Row row : rows) {
                ret.add(Pet.from(row));
            }
            return ret;
        });
    }
}
