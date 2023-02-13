package com.oz.loomtest;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import net.datafaker.Faker;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    void addAdditionalDBRows(@Observes StartupEvent ev, @ConfigProperty(name = "startup.additionalDBRows") boolean isInit,
                @ConfigProperty(name = "startup.additionalDBRowsCount") int size) {
        if (isInit) {
            initDb(size);
        }
    }

    private void initDb(int size) {
        List<Tuple> pets = new ArrayList<>(size);
        Faker faker = new Faker();
        Random random = new Random();

        var highestTypeID =  reactiveClient.query("SELECT MAX(id) FROM types LIMIT 1").
                executeAndAwait().iterator().next().getInteger("max");
        var highestOwnerID =  reactiveClient.query("SELECT MAX(id) FROM owners LIMIT 1").
                executeAndAwait().iterator().next().getInteger("max");

        for (int i = 0; i < size; i++) {
            String name = faker.name().firstName();
            LocalDateTime birthday = faker.date().birthday(0, 15).toLocalDateTime();
            int type = random.nextInt(1, highestTypeID + 1);
            int owner = random.nextInt(1, highestOwnerID + 1);
            pets.add(Tuple.of(name, birthday, type, owner));
        }

        reactiveClient.preparedQuery("INSERT INTO pets(name, birth_date, type_id, owner_id) values($1, $2, $3, $4)")
                .executeBatch(pets).await().indefinitely();
    }
}
