package com.flavorwocky.service;

import java.util.Collection;
import java.util.List;

import com.flavorwocky.domain.FlavorPair;
import com.flavorwocky.domain.FlavorTree;
import com.flavorwocky.domain.Ingredient;
import com.flavorwocky.domain.LatestPairing;

/**
 * @author Luanne Misquitta
 */
public interface PairingService {

	Iterable<Ingredient> getIngredientNames();

	List<String> getTrios(String ingredient);

	FlavorTree getFlavorTree(String ingredient);

	void addPairing(FlavorPair flavorPair);

	Collection<LatestPairing> getLatestPairings();
}
