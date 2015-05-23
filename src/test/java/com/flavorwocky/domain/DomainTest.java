package com.flavorwocky.domain;

import static org.junit.Assert.*;

import com.flavorwocky.context.PersistenceContext;
import com.flavorwocky.repository.CategoryRepository;
import com.flavorwocky.repository.IngredientRepository;
import com.flavorwocky.repository.PairingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.helpers.collection.IteratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {PersistenceContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DomainTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    PairingRepository pairingRepository;


    @Test
    public void shouldBeAbleToSaveACategory() {
        Category category = new Category("Dairy");
        categoryRepository.save(category);

        Category dairy = categoryRepository.findByName("Dairy");
        assertNotNull(dairy);
        assertEquals(category.getName(), dairy.getName());
    }

    @Test
    public void shouldBeAbleToSaveAnIngredient() {
        Category category = new Category("Dairy");
        Ingredient ingredient = new Ingredient("Emmental");
        ingredient.setCategory(category);
        ingredientRepository.save(ingredient);

        Ingredient emmental = IteratorUtil.firstOrNull(ingredientRepository.findByName("Emmental"));
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

        Ingredient loadedEmmental = IteratorUtil.firstOrNull(ingredientRepository.findByName("Emmental"));
        assertNotNull(loadedEmmental);
        assertEquals(emmental.getName(), loadedEmmental.getName());
        assertEquals(dairy, emmental.getCategory());
        assertEquals(1, loadedEmmental.getPairings().size());

    }

    @Test
    public void shouldBeAbleToAddInterrelatedPairings() {
        Category meat = new Category("Meat");
        Category dairy = new Category("Dairy");
        Category veg = new Category("Vegetable");

        Ingredient chicken = new Ingredient("Chicken");
        chicken.setCategory(meat);
        ingredientRepository.save(chicken);

        Ingredient carrot = new Ingredient("Carrot");
        carrot.setCategory(veg);
        ingredientRepository.save(carrot);

        Ingredient butter = new Ingredient("Butter");
        butter.setCategory(dairy);
        ingredientRepository.save(butter);

        Pairing pairing = new Pairing();
        pairing.setFirst(chicken);
        pairing.setSecond(carrot);
        pairing.setAffinity(Affinity.EXCELLENT);
        carrot.addPairing(pairing);
        ingredientRepository.save(chicken);

        Pairing pairing2 = new Pairing();
        pairing2.setFirst(chicken);
        pairing2.setSecond(butter);
        pairing2.setAffinity(Affinity.EXCELLENT);
        carrot.addPairing(pairing2);
        ingredientRepository.save(chicken);


        Pairing pairing3 = new Pairing();
        pairing3.setFirst(carrot);
        pairing3.setSecond(butter);
        pairing3.setAffinity(Affinity.EXCELLENT);
        carrot.addPairing(pairing3);
        ingredientRepository.save(carrot);
    }
}
