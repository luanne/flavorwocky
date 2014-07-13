package com.flavorwocky.api;

import com.flavorwocky.domain.ingredient.Ingredient;
import com.flavorwocky.domain.pairing.Pairing;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

/**
 * REST api for flavorwocky
 */
@Path("/")
public class FlavorApi {

    @Path("latestPairings")
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

}
