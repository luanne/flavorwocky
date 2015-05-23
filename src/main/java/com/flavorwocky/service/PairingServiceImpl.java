package com.flavorwocky.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.flavorwocky.domain.*;
import com.flavorwocky.repository.IngredientRepository;
import com.flavorwocky.repository.LatestPairingRepository;
import com.flavorwocky.repository.PairingRepository;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.cypher.query.SortOrder;
import org.neo4j.ogm.session.Neo4jSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PairingServiceImpl implements PairingService {

    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    LatestPairingRepository latestPairingRepository;

    @Autowired
    PairingRepository pairingRepository;

    @Autowired Neo4jSession neo4jSession;

    public List<String> getTrios(String ingredient) {
        List<String> trios = new ArrayList<>();
        List<Number> rels = new ArrayList<>();
        Iterable<Map<String, Object>> results = ingredientRepository.getTrios(ingredient);
        for (Map<String, Object> row : results) {
            Number relId = (Number) row.get("relId");
            if (!rels.contains(relId)) {
                rels.add(relId);
                trios.add(row.get("firstName") + ", " + row.get("secondName") + ", " + row.get("thirdName"));
            }
        }
        if (trios.size() == 0) {
            trios.add("No trios found");
        }
        return trios;
    }

    public FlavorTree getFlavorTree(String ingredient) {
        Iterable<Map<String, Object>> results = ingredientRepository.getFlavorPaths(ingredient);
        FlavorTree root = new FlavorTree();
        for (Map<String, Object> row : results) {
            List<Map<String, Object>> path = (List<Map<String, Object>>) row.get("p");
            System.out.println("path = " + path);
            root.setName((String) path.get(0).get("name"));
            root.setAffinity("1");

            int count = 1;
            FlavorTree parent = root;
            while (count < path.size() - 1) { //the last element on the path is the category of the final ingredient
                if (path.get(count).size() == 0) {  //Category relation, no attributes
                    count++;
                    break;
                }
                String affinity = (String) path.get(count).get("affinity");
                count++;
                String name = (String) path.get(count).get("name");
                if (!parent.getChildren().contains(new FlavorTree(name))) {
                    FlavorTree child = new FlavorTree();
                    child.setAffinity(Double.toString(Affinity.valueOf(affinity).getWeight()));
                    child.setName(name);
                    parent.addChild(child);
                }
                parent = parent.getChildByName(name);
                count++;
            }
            parent.setCategoryColor((String) path.get(count).get("catColor"));
        }

        return root;
    }

    @Override
    public void addPairing(FlavorPair flavorPair) {
        //TODO index on name
        Ingredient ingredient1 = IteratorUtil.firstOrNull(ingredientRepository.findByName(flavorPair.getIngredient1()));
        Ingredient ingredient2 = IteratorUtil.firstOrNull(ingredientRepository.findByName(flavorPair.getIngredient2()));
        Pairing pairing = null;
        if (ingredient1 != null && ingredient2 != null) {
            for (Pairing p : ingredient1.getPairings()) {
                if (p.getFirst().getName().equals(ingredient2.getName()) || p.getSecond().getName().equals(ingredient2.getName())) {
                    //The pairing exists, bail
                    pairing = p;
                    break;
                }
            }
            if (pairing != null) {
                return;
            }
        }
        if (ingredient1 == null) {
            ingredient1 = new Ingredient();
            ingredient1.setName(flavorPair.getIngredient1());
            ingredient1.setCategory(new Category(flavorPair.getCategory1()));
            ingredientRepository.save(ingredient1);
        }
        if (ingredient2 == null) {
            ingredient2 = new Ingredient();
            ingredient2.setName(flavorPair.getIngredient2());
            ingredient2.setCategory(new Category(flavorPair.getCategory2()));
            ingredientRepository.save(ingredient2);
        }
        pairing = new Pairing();
        pairing.setFirst(ingredient1);
        pairing.setSecond(ingredient2);
        pairing.setAffinity(Affinity.valueOf(flavorPair.getAffinity()));
        ingredient1.addPairing(pairing);
        //ingredient1.getPairings().add(pairing);
        //ingredient2.getPairings().add(pairing);
        pairingRepository.save(pairing);


        //Add the pairing as a latest pairing
        Iterable<LatestPairing> latestPairings = neo4jSession.loadAll(LatestPairing.class, new SortOrder().add(SortOrder.Direction.DESC, "dateAdded"), new Pagination(1, 5));
        for (LatestPairing latestPairing : latestPairings) {
            latestPairingRepository.delete(latestPairing);
        }

        LatestPairing latestPairing = new LatestPairing(ingredient1.getName(), ingredient2.getName(), new Date());
        latestPairingRepository.save(latestPairing);
    }

    @Override
    public Iterable<LatestPairing> getLatestPairings() {
        Iterable<LatestPairing> pairings = neo4jSession.loadAll(LatestPairing.class, new SortOrder().add(SortOrder.Direction.DESC, "dateAdded"), new Pagination(0, 5));
        return pairings;
    }

}
