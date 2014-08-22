package com.flavorwocky.domain.pairing.dao;

import com.flavorwocky.db.ConnectionFactory;
import com.flavorwocky.domain.pairing.Pairing;
import com.flavorwocky.exception.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luanne on 11/06/14.
 */
public class PairingDao {

    protected void save(Pairing pairing) throws DbException {

        final Connection conn = ConnectionFactory.getInstance().getServerConnection();
        final String query = "merge (i1:Ingredient {name: {1}}) " +
                "merge (i2:Ingredient {name: {2}}) " +
                "with i1,i2 " +
                "merge (i1)<-[:hasIngredient]-(p:Pairing)-[:hasIngredient]->(i2) " +
                "on create set p.affinity={3}, p.allAffinities=[{3}] " +
                "on match set p.allAffinities=coalesce(p.allAffinities,[]) + {3} " +
                "merge (i1)-[:pairsWith]-(i2)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, pairing.getFirstIngredient().getName());
            ps.setString(2, pairing.getSecondIngredient().getName());
            ps.setDouble(3, pairing.getAffinity().getWeight());
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
            rs.close();
            ps.close();
        } catch (SQLException sqle) {
            throw new DbException("Error fetching trios for " + ingredient, sqle);
        }
        return trios;
    }
}
