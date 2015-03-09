package com.flavorwocky.domain.pairing;

import com.flavorwocky.db.ConnectionFactory;
import com.flavorwocky.exception.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luanne on 11/06/14.
 */
public class PairingDao {

    protected void save(Pairing pairing) throws DbException {

        final Connection conn = ConnectionFactory.getInstance().getServerConnection();
        final String query = "merge (i1:Ingredient {name: {1}}) " +
                "merge (i2:Ingredient {name: {2}}) " +
                "merge (cat:Category {name: {4}}) " +
                "with i1,i2,cat " +
                "merge (i1)<-[:hasIngredient]-(p:PA)-[:hasIngredient]->(i2) " +
                "on create set p.affinity={3}, p.allAffinities=[{3}] " +
                "on match set p.allAffinities=coalesce(p.allAffinities,[]) + {3} " +
                "merge (i1)-[:PAIRS_WITH {affinity: {3}}]-(i2) " +
                "merge (i1)-[:category]->(cat) " +
                "merge (i2)-[:category]->(cat) ";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, pairing.getFirstIngredient().getName());
            ps.setString(2, pairing.getSecondIngredient().getName());
            ps.setDouble(3, pairing.getAffinity().getWeight());
            ps.setString(4, "Placeholder");

            ps.execute();
            ps.close();
        } catch (SQLException sqle) {
            throw new DbException("Error saving pairing " + pairing.getFirstIngredient().getName() + ", " + pairing.getSecondIngredient().getName(), sqle);
        }
    }


    public List<String> getTrios(String ingredient) throws DbException {
        List<String> trios = new ArrayList<>();
        List<Long> rels = new ArrayList<>();

        final Connection conn = ConnectionFactory.getInstance().getServerConnection();
        final String query = "match (ing1:Ingredient {name: {1}})-[r1:PAIRS_WITH]-(ing2)-[r2:PAIRS_WITH]-(ing3)-[r3:PAIRS_WITH]-(ing1) return ing1.name as firstName, ing2.name as secondName,ing3.name as thirdName, ID(r2) as relId";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ingredient);
            ResultSet rs = ps.executeQuery();
            while (rs != null && rs.next()) {
                long relId = rs.getLong("relId");
                if (!rels.contains(relId)) {
                    rels.add(relId);
                    trios.add(rs.getString("firstName") + ", " + rs.getString("secondName") + ", " + rs.getString("thirdName"));
                }
            }
            if (rs != null) {
                rs.close();
            }
            ps.close();
        } catch (SQLException sqle) {
            throw new DbException("Error fetching trios for " + ingredient, sqle);
        }
        return trios;
    }

    public FlavorTree getFlavorTree(String ingredient) {
        String query = "match p=(i:Ingredient {name:{1}})-[r:PAIRS_WITH*0..3]-(i2)-[:category]->(cat) return p;";
        final Connection conn = ConnectionFactory.getInstance().getServerConnection();
        FlavorTree root = new FlavorTree();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ingredient);
            ResultSet rs = ps.executeQuery();
            while (rs != null && rs.next()) {
                List<Map<String, Object>> path = (List<Map<String, Object>>) rs.getObject("p");
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
                    String affinity;
                    try {
                        affinity = Double.toString((Double) path.get(count).get("affinity"));
                    } catch (ClassCastException cce) {
                        affinity = (String) path.get(count).get("affinity");
                    }
                    count++;
                    String name = (String) path.get(count).get("name");
                    if (!parent.getChildren().contains(new FlavorTree(name))) {
                        FlavorTree child = new FlavorTree();
                        child.setAffinity(affinity);
                        child.setName(name);
                        parent.addChild(child);
                    }
                    parent = parent.getChildByName(name);
                    count++;
                }
                parent.setCategoryColor((String) path.get(count).get("catColor"));
            }

            if (rs != null) {
                rs.close();
            }
            ps.close();
        } catch (SQLException sqle) {
            throw new DbException("Error fetching trios for " + ingredient, sqle);
        }
        return root;
    }
}
