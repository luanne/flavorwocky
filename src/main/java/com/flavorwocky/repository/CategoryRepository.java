package com.flavorwocky.repository;

import com.flavorwocky.domain.Category;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends GraphRepository<Category> {

	Category findByName(String name);
}
