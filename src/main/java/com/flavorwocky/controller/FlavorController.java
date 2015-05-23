package com.flavorwocky.controller;

import java.util.List;

import com.flavorwocky.domain.*;
import com.flavorwocky.repository.CategoryRepository;
import com.flavorwocky.repository.IngredientRepository;
import com.flavorwocky.service.PairingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class FlavorController {

    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PairingService pairingService;

    @RequestMapping(value = "pairing", method = RequestMethod.POST, consumes = "application/json")
    public void addPair(@RequestBody FlavorPair pair) {
        pairingService.addPairing(pair);
    }

    @RequestMapping(value = "ingredients", method = RequestMethod.GET)
    public Iterable<Ingredient> getIngredients() {
        return ingredientRepository.findAll();
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

    @RequestMapping(value = "categories", method = RequestMethod.GET)
    public Iterable<Category> getCategories() {
        return categoryRepository.findAll();
    }
}
