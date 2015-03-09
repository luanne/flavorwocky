package com.flavorwocky.service;

import com.flavorwocky.domain.pairing.FlavorTree;
import com.flavorwocky.domain.pairing.PairingDao;

import java.util.List;

/**
 * Created by luanne on 22/08/14.
 */
public class PairingService {

    public List<String> getTrios(String ingredient) {
        PairingDao pairingDao = new PairingDao();
        List<String> trios = pairingDao.getTrios(ingredient);
        if (trios.size() == 0) {
            trios.add("No trios found");
        }
        return trios;
    }

    public FlavorTree getFlavorTree(String ingredient) {
        return new PairingDao().getFlavorTree(ingredient);
    }


}
