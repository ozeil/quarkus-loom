package com.oz.loomtest;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@Path("/pets")
public class PetResource {

    @Inject
    PetRepository petRepository;

    @GET
    @Path("/blocking")
    @Blocking
    public List<Pet> getPetsBlocking() {
        return petRepository.findAllBlocking();
    }

    @GET
    @Path("/virtualThread")
    @Blocking
    @RunOnVirtualThread
    public List<Pet> getPets() {
        return petRepository.findAllAwait();
    }

    @GET
    @Path("/reactive")
    @NonBlocking
    public Uni<List<Pet>> getPetsReactive() {
        return petRepository.findAllReactive();
    }

    @GET
    @Path("/helloPet")
    public String helloPet() {
        return "Hello Pet";
    }

    @GET
    @Path("/sleep")
    @Blocking
    public String sleep() throws InterruptedException {
        Thread.sleep(500);
        return "Hello Pet";
    }

    @GET
    @Blocking
    @RunOnVirtualThread
    @Path("/sleepVirtual")
    public String sleepVirtual() throws InterruptedException {
        Thread.sleep(500);
        return "Hello Pet";
    }
}
