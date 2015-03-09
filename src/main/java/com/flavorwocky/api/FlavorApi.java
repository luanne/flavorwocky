package com.flavorwocky.api;

import com.flavorwocky.domain.ingredient.Ingredient;
import com.flavorwocky.domain.pairing.Affinity;
import com.flavorwocky.domain.pairing.FlavorTree;
import com.flavorwocky.domain.pairing.Pairing;
import com.flavorwocky.service.IngredientService;
import com.flavorwocky.service.PairingService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
        return new PairingService().getTrios(ingredient);
    }

    @Path("ingredients")
    @GET
    @Produces("application/json")
    public List<String> getIngredients() {
        return new IngredientService().getAllIngredients();
    }

    @Path("search")
    @GET
    @Produces("application/json")
    public FlavorTree getFlavorTree(@QueryParam("ingredient") String ingredient) {
       /* FlavorTree flavorTree = new FlavorTree();
        flavorTree.setName("Chicken");
        flavorTree.setAffinity("1");
        flavorTree.setCategoryColor("brown");

        FlavorTree child1 = new FlavorTree();
        child1.setName("Thyme");
        child1.setAffinity("0.5");
        child1.setCategoryColor("green");

        FlavorTree child2 = new FlavorTree();
        child2.setName("Onion");
        child2.setAffinity("0.35");
        child2.setCategoryColor("yellow");

        List<FlavorTree> children1 = new ArrayList<>();
        children1.add(child1);
        children1.add(child2);
        flavorTree.setChildren(children1);


        FlavorTree child3 = new FlavorTree();
        child3.setName("Potato");
        child3.setAffinity("0.6");
        child3.setCategoryColor("blue");


        List<FlavorTree> children2 = new ArrayList<>();
        children2.add(child3);

        child1.setChildren(children2);
        return flavorTree;
*/
        return new PairingService().getFlavorTree(ingredient);

    }

    @Path("pairing")
    @POST
    @Consumes("application/json")
    public void addPairing(FlavorPair pair) {
        Pairing pairing = new Pairing();
        Ingredient ing1 = new Ingredient();
        ing1.setName(pair.getIngredient1());
        Ingredient ing2 = new Ingredient();
        ing2.setName(pair.getIngredient2());
        pairing.setFirstIngredient(ing1);
        pairing.setSecondIngredient(ing2);
        pairing.setAffinity(Affinity.valueOf(pair.getAffinity()));
        pairing.save();
    }

}
