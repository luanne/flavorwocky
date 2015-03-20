package com.flavorwocky.domain;

import com.flavorwocky.context.PersistenceContext;
import com.flavorwocky.repository.CategoryRepository;
import com.flavorwocky.repository.IngredientRepository;
import com.graphaware.test.integration.WrappingServerIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {PersistenceContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DomainTest extends WrappingServerIntegrationTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    IngredientRepository ingredientRepository;

    @Override
    protected int neoServerPort() {
        return PersistenceContext.NEO4J_PORT;
    }

    @Test
    public void shouldBeAbleToSaveACategory() {
        Category category = new Category("Dairy");
        categoryRepository.save(category);

        Category dairy = IteratorUtil.firstOrNull(categoryRepository.findByProperty("name", "Dairy"));
        assertNotNull(dairy);
        assertEquals(category.getName(), dairy.getName());
    }

    @Test
    public void shouldBeAbleToSaveAnIngredient() {
        Category category = new Category("Dairy");
        Ingredient ingredient = new Ingredient("Emmental");
        ingredient.setCategory(category);
        ingredientRepository.save(ingredient);

        Ingredient emmental = IteratorUtil.firstOrNull(ingredientRepository.findByProperty("name", "Emmental"));
        assertNotNull(emmental);
        assertEquals(ingredient.getName(), emmental.getName());
        assertEquals(category, emmental.getCategory());
    }

    @Test
    public void shouldBeAbleToAddPairing() {
        Category meat = new Category("Meat");
        Ingredient bacon = new Ingredient("Bacon");
        bacon.setCategory(meat);
        ingredientRepository.save(bacon);

        Category dairy = new Category("Dairy");
        Ingredient emmental = new Ingredient("Emmental");
        emmental.setCategory(dairy);
        ingredientRepository.save(emmental);

        Pairing pairing = new Pairing();
        pairing.setFirst(emmental);
        pairing.setSecond(bacon);
        pairing.setAffinity(Affinity.EXCELLENT);
        emmental.addPairing(pairing);
        ingredientRepository.save(emmental);

        Ingredient loadedEmmental = IteratorUtil.firstOrNull(ingredientRepository.findByProperty("name", "Emmental"));
        assertNotNull(loadedEmmental);
        assertEquals(emmental.getName(), loadedEmmental.getName());
        assertEquals(dairy, emmental.getCategory());
        assertEquals(1, loadedEmmental.getPairings().size());

    }


}
