package com.flavorwocky.service;

import java.util.Collection;
import java.util.List;

import com.flavorwocky.domain.FlavorPair;
import com.flavorwocky.domain.FlavorTree;
import com.flavorwocky.domain.LatestPairing;

public interface PairingService {

    public List<String> getTrios(String ingredient);

    public FlavorTree getFlavorTree(String ingredient);

    public void addPairing(FlavorPair flavorPair);

    public Collection<LatestPairing> getLatestPairings();
}
