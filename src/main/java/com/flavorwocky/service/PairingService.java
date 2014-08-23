package com.flavorwocky.service;

import com.flavorwocky.domain.pairing.dao.PairingDao;

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

  /*  public FlavorTreeVO getFlavorTree(String ingredient) {
        FlavorTreeVO flavorTreeVO = new FlavorTreeVO();
        flavorTreeVO.setName(ingredient);

    }

    private List getChildren(int depth, int nodeId, int parentNodeId) {
        def childrenList = []

        if (depth > 3) {
            return null
        }

        def res = Ingredient.cypherStatic ("""start n=node({nodeId}), original=node({original})
                match (n)-[r:pairings]-(i)-[:category]->(cat)
                where not(i=original)
        return i.name as name ,cat.catColor as catColor ,ID(i) as idi, r.wt as wt""",
                [nodeId: nodeId, original: parentNodeId]
        )
        res.iterator().each {
            def child = ["name": it.name, "catColor": it.catColor, "wt": it.wt]
            child.put("children", getChildren(depth + 1, it.idi as int, nodeId))
            childrenList.add child
        }

        return childrenList
    }*/
}
