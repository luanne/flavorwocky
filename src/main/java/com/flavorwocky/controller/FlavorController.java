package com.flavorwocky.controller;

import com.flavorwocky.domain.FlavorPair;
import com.flavorwocky.domain.FlavorTree;
import com.flavorwocky.domain.Ingredient;
import com.flavorwocky.domain.LatestPairing;
import com.flavorwocky.repository.IngredientRepository;
import com.flavorwocky.service.PairingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class FlavorController {

    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    PairingService pairingService;

    @RequestMapping(value = "pairing", method = RequestMethod.POST, consumes = "application/json")
    public void addPair(@RequestBody FlavorPair pair) {
        pairingService.addPairing(pair);
    }

    @RequestMapping(value = "ingredients", method = RequestMethod.GET)
    public Iterable<Ingredient> getIngredients() {
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

    @RequestMapping(value = "pairings/latest", method = RequestMethod.GET)
    public Iterable<LatestPairing> getLatestPairings() {
        Iterable<LatestPairing> latestPairings = pairingService.getLatestPairings();
        return latestPairings;
    }
}
