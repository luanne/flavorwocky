package com.flavorwocky.repository;

import com.flavorwocky.domain.Category;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Luanne Misquitta
 */
@Repository
public interface CategoryRepository extends GraphRepository<Category> {

	Category findByName(String name);
}
