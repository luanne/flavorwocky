package com.flavorwocky.domain.ingredient.db;

import com.flavorwocky.db.ConnectionFactory;
import com.flavorwocky.exception.DbException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luanne on 22/08/14.
 */
public class IngredientDao {


    public List<String> getAllIngredients() throws DbException {
        List<String> ingredients = new ArrayList<>();

        final Connection conn = ConnectionFactory.getInstance().getServerConnection();
        final String query = "match (ing:Ingredient) return ing.name as name order by name";

        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(query);
            while (rs != null && rs.next()) {
                ingredients.add(rs.getString("name"));
            }
            rs.close();
            st.close();
        } catch (SQLException sqle) {
            throw new DbException("Error fetching all ingredients", sqle);
        }
        return ingredients;
    }
}
