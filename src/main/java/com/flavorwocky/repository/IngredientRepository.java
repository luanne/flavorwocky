package com.flavorwocky.repository;

import java.util.List;
import java.util.Map;

import com.flavorwocky.domain.Ingredient;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends GraphRepository<Ingredient> {

    List<Ingredient> findByName(String name);

    @Query("match p=(i:Ingredient {name:{0}})-[r:PAIRS_WITH*0..3]-(i2)-[:HAS_CATEGORY]->(cat) return p;")
    Iterable<Map<String, Object>> getFlavorPaths(String ingredientName);

    @Query("match (ing1:Ingredient {name: {0}})-[r1:PAIRS_WITH]-(ing2)-[r2:PAIRS_WITH]-(ing3)-[r3:PAIRS_WITH]-(ing1) return ing1.name as firstName, ing2.name as secondName,ing3.name as thirdName, ID(r2) as relId")
    Iterable<Map<String, Object>> getTrios(String ingredient);

}
