package com.flavorwocky.domain.pairing.dao;

import com.flavorwocky.db.ConnectionFactory;
import com.flavorwocky.domain.pairing.Pairing;
import com.flavorwocky.exception.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        } catch (SQLException sqle) {
            throw new DbException("Error saving pairing " + pairing.getFirstIngredient().getName() + ", " + pairing.getSecondIngredient().getName(), sqle);
        }
    }
}
