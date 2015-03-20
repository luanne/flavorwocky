package com.flavorwocky.service;

import com.flavorwocky.domain.Affinity;
import com.flavorwocky.domain.FlavorTree;
import com.flavorwocky.domain.Pairing;
import com.flavorwocky.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PairingServiceImpl implements PairingService {

    @Autowired
    IngredientRepository ingredientRepository;

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
    public void addPairing(Pairing pairing) {
        pairing.getFirst().getPairings().add(pairing);
        pairing.getSecond().getPairings().add(pairing);
        ingredientRepository.save(pairing.getFirst());
    }


}
