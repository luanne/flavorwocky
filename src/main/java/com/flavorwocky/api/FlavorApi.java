package com.flavorwocky.api;

import com.flavorwocky.domain.ingredient.Ingredient;
import com.flavorwocky.domain.pairing.Pairing;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

/**
 * REST api for flavorwocky
 */
@Path("/")
public class FlavorApi {

    @Path("pairing/latest")
    @GET
    @Produces("application/json")
    public List<Pairing> getLatestPairings() {
        List<Pairing> pairings = new ArrayList<>();
        Pairing p1 = new Pairing();
        p1.setFirstIngredient(new Ingredient("i1"));
        p1.setSecondIngredient(new Ingredient("i2"));
        pairings.add(p1);

        Pairing p2 = new Pairing();
        p2.setFirstIngredient(new Ingredient("i3"));
        p2.setSecondIngredient(new Ingredient("i4"));
        pairings.add(p2);
        return pairings;
    }

    @Path("trios/{ingredient}")
    @GET
    @Produces("application/json")
    public List<String> getTrios(@PathParam("ingredient") String ingredient) {
        List<String> trios = new ArrayList<>();
        trios.add("Bacon, Clams, Potatoes");
        trios.add("Bacon, Honey, Chicken");
        return trios;
    }

    @Path("pairing")
    @POST
    @Consumes("application/json")
    public boolean addPairing(FlavorPair pair) {
        return true;

    }

}
