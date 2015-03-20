package com.flavorwocky.service;

import com.flavorwocky.domain.FlavorTree;
import com.flavorwocky.domain.Pairing;

import java.util.List;

public interface PairingService {

    public List<String> getTrios(String ingredient);

    public FlavorTree getFlavorTree(String ingredient);

    public void addPairing(Pairing pairing);
}
