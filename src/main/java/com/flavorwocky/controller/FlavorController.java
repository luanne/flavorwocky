package com.flavorwocky.controller;

import com.flavorwocky.api.FlavorPair;
import com.flavorwocky.domain.Affinity;
import com.flavorwocky.domain.FlavorTree;
import com.flavorwocky.domain.Ingredient;
import com.flavorwocky.domain.Pairing;
import com.flavorwocky.repository.IngredientRepository;
import com.flavorwocky.service.PairingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class FlavorController {

    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    PairingService pairingService;

    @RequestMapping(value = "pairing", method = RequestMethod.POST, consumes = "application/json")
    public void create(@RequestBody FlavorPair pair) {
        Ingredient ing1 = new Ingredient();
        ing1.setName(pair.getIngredient1());
        Ingredient ing2 = new Ingredient();
        ing2.setName(pair.getIngredient2());

        Pairing pairing = new Pairing();
        pairing.setFirst(ing1);
        pairing.setSecond(ing2);
        pairing.setAffinity(Affinity.valueOf(pair.getAffinity()));

        pairingService.addPairing(pairing);
    }

    @RequestMapping(value = "ingredients", method = RequestMethod.GET)
    public Iterable<Ingredient> list(final HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        return ingredientRepository.findAll(0);
    }

    @RequestMapping(value = "search/{ingredient}", method = RequestMethod.GET)
    public FlavorTree getFlavorTree(@PathVariable("ingredient") String ingredient) {
        return pairingService.getFlavorTree(ingredient);
    }

    @RequestMapping(value = "trios/{ingredient}", method = RequestMethod.GET)
    public List<String> getTrios(@PathVariable("ingredient") String ingredient) {
        return pairingService.getTrios(ingredient);
    }
}
