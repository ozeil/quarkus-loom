package com.oz.loomtest;

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
    public List<Pet> getPetsBlocking() {
        return petRepository.findAllBlocking();
    }

    @GET
    @Path("/virtualThread")
    @RunOnVirtualThread
    public List<Pet> getPets() {
        return petRepository.findAllAwait();
    }

    @GET
    @Path("/reactive")
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
    public String sleep() throws InterruptedException {
        Thread.sleep(500);
        return "Hello Pet";
    }

    @GET
    @RunOnVirtualThread
    @Path("/sleepVirtual")
    public String sleepVirtual() throws InterruptedException {
        Thread.sleep(500);
        return "Hello Pet";
    }
}
