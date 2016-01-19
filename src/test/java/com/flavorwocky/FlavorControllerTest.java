package com.flavorwocky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flavorwocky.context.PersistenceContext;
import com.flavorwocky.controller.FlavorController;
import com.flavorwocky.domain.Affinity;
import com.flavorwocky.domain.Category;
import com.flavorwocky.domain.FlavorPair;
import com.flavorwocky.domain.Ingredient;
import com.flavorwocky.domain.LatestPairing;
import com.flavorwocky.domain.Pairing;
import com.flavorwocky.repository.CategoryRepository;
import com.flavorwocky.repository.IngredientRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {PersistenceContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FlavorControllerTest {

	@Autowired
	FlavorController flavorController;
	@Autowired
	IngredientRepository ingredientRepository;
	@Autowired
	CategoryRepository categoryRepository;

	@Autowired Session session;

	@Before
	public void setup() {
		//Create some ingredients and categories
		Category meat = new Category("Meat");
		Category dairy = new Category("Dairy");
		Category veg = new Category("Vegetable");
		categoryRepository.save(Arrays.asList(meat, dairy, veg));

		Ingredient chicken = new Ingredient("Chicken");
		chicken.setCategory(meat);
		Ingredient carrot = new Ingredient("Carrot");
		carrot.setCategory(veg);
		Ingredient butter = new Ingredient("Butter");
		butter.setCategory(dairy);
		Ingredient coriander = new Ingredient("Coriander");
		coriander.setCategory(veg);
		Ingredient yoghurt = new Ingredient("Yoghurt");
		yoghurt.setCategory(dairy);
		ingredientRepository.save(Arrays.asList(chicken, carrot, butter, yoghurt, coriander));
	}

	@After
	public void tearDown() {
		session.purgeDatabase();
	}

	@Test
	public void shouldFetchAllIngredients() {
		Iterable<Ingredient> ingredients = flavorController.getIngredients();
		assertNotNull(ingredients);
		Map<String, Ingredient> ing = new HashMap<>();
		for (Ingredient ingredient : ingredients) {
			ing.put(ingredient.getName(), ingredient);
		}
		assertEquals(5, ing.size());
		assertEquals(ingredientRepository.findByName("Chicken").iterator().next(), ing.get("Chicken"));
		assertEquals(ingredientRepository.findByName("Carrot").iterator().next(), ing.get("Carrot"));
		assertEquals(ingredientRepository.findByName("Butter").iterator().next(), ing.get("Butter"));
		assertEquals(ingredientRepository.findByName("Coriander").iterator().next(), ing.get("Coriander"));
		assertEquals(ingredientRepository.findByName("Yoghurt").iterator().next(), ing.get("Yoghurt"));
	}

	@Test
	public void shouldBeAbleToAddPairing() {
		FlavorPair flavorPair = new FlavorPair();
		flavorPair.setIngredient1("Chicken");
		flavorPair.setIngredient2("Carrot");
		flavorPair.setCategory1("Meat");
		flavorPair.setCategory2("Vegetable");
		flavorPair.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair);

		Ingredient chicken = ingredientRepository.findByName("Chicken").iterator().next();
		assertEquals(1, chicken.getPairings().size());
		Pairing pairing = chicken.getPairings().iterator().next();
		assertEquals(Affinity.EXCELLENT, pairing.getAffinity());
		assertEquals("Chicken", pairing.getFirst().getName());
		assertEquals("Carrot", pairing.getSecond().getName());

		Ingredient carrot = ingredientRepository.findByName("Carrot").iterator().next();
		assertEquals(1, carrot.getPairings().size());
		assertEquals(Affinity.EXCELLENT, pairing.getAffinity());
		assertEquals("Chicken", pairing.getFirst().getName());
		assertEquals("Carrot", pairing.getSecond().getName());
	}

	@Test
	public void shouldNotUpdatePairingIfOneExists() {
		FlavorPair flavorPair = new FlavorPair();
		flavorPair.setIngredient1("Chicken");
		flavorPair.setIngredient2("Carrot");
		flavorPair.setCategory1("Meat");
		flavorPair.setCategory2("Vegetable");
		flavorPair.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair);

		FlavorPair flavorPair2 = new FlavorPair();
		flavorPair2.setIngredient1("Chicken");
		flavorPair2.setIngredient2("Carrot");
		flavorPair2.setCategory1("Meat");
		flavorPair2.setCategory2("Vegetable");
		flavorPair2.setAffinity(Affinity.GOOD.name());
		flavorController.addPair(flavorPair2);

		Ingredient chicken = ingredientRepository.findByName("Chicken").iterator().next();
		assertEquals(1, chicken.getPairings().size());
		Pairing pairing = chicken.getPairings().iterator().next();
		assertEquals(Affinity.EXCELLENT, pairing.getAffinity());
		assertEquals("Chicken", pairing.getFirst().getName());
		assertEquals("Carrot", pairing.getSecond().getName());

		Ingredient carrot = ingredientRepository.findByName("Carrot").iterator().next();
		assertEquals(1, carrot.getPairings().size());
		assertEquals(Affinity.EXCELLENT, pairing.getAffinity());
		assertEquals("Chicken", pairing.getFirst().getName());
		assertEquals("Carrot", pairing.getSecond().getName());
	}

	@Test
	public void shouldReturnLatestPairings() {
		FlavorPair flavorPair = new FlavorPair();
		flavorPair.setIngredient1("Chicken");
		flavorPair.setIngredient2("Carrot");
		flavorPair.setCategory1("Meat");
		flavorPair.setCategory2("Vegetable");
		flavorPair.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair);

		FlavorPair flavorPair2 = new FlavorPair();
		flavorPair2.setIngredient1("Chicken");
		flavorPair2.setIngredient2("Butter");
		flavorPair2.setCategory1("Meat");
		flavorPair2.setCategory2("Dairy");
		flavorPair2.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair2);

		FlavorPair flavorPair3 = new FlavorPair();
		flavorPair3.setIngredient1("Carrot");
		flavorPair3.setIngredient2("Butter");
		flavorPair3.setCategory1("Vegetable");
		flavorPair3.setCategory2("Dairy");
		flavorPair3.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair3);

		Iterable<LatestPairing> latest = flavorController.getLatestPairings();
		List<LatestPairing> pairings = new ArrayList<>();
		for (LatestPairing latestPairing : latest) {
			pairings.add(latestPairing);
		}
		assertEquals(3, pairings.size());
		assertEquals("Carrot", pairings.get(0).getIngredient1());
		assertEquals("Butter", pairings.get(0).getIngredient2());
		assertEquals("Chicken", pairings.get(1).getIngredient1());
		assertEquals("Butter", pairings.get(1).getIngredient2());
		assertEquals("Chicken", pairings.get(2).getIngredient1());
		assertEquals("Carrot", pairings.get(2).getIngredient2());
	}

	@Test
	public void shouldDropEarliestPairingWhenLatestPairingsExceeds5() {
		FlavorPair flavorPair = new FlavorPair();
		flavorPair.setIngredient1("Chicken");
		flavorPair.setIngredient2("Carrot");
		flavorPair.setCategory1("Meat");
		flavorPair.setCategory2("Vegetable");
		flavorPair.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair);

		FlavorPair flavorPair2 = new FlavorPair();
		flavorPair2.setIngredient1("Chicken");
		flavorPair2.setIngredient2("Butter");
		flavorPair2.setCategory1("Meat");
		flavorPair2.setCategory2("Dairy");
		flavorPair2.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair2);

		FlavorPair flavorPair3 = new FlavorPair();
		flavorPair3.setIngredient1("Carrot");
		flavorPair3.setIngredient2("Butter");
		flavorPair3.setCategory1("Vegetable");
		flavorPair3.setCategory2("Dairy");
		flavorPair3.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair3);

		FlavorPair flavorPair4 = new FlavorPair();
		flavorPair4.setIngredient1("Coriander");
		flavorPair4.setIngredient2("Yoghurt");
		flavorPair4.setCategory1("Vegetable");
		flavorPair4.setCategory2("Dairy");
		flavorPair4.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair4);

		FlavorPair flavorPair5 = new FlavorPair();
		flavorPair5.setIngredient1("Coriander");
		flavorPair5.setIngredient2("Chicken");
		flavorPair5.setCategory1("Vegetable");
		flavorPair5.setCategory2("Meat");
		flavorPair5.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair5);

		FlavorPair flavorPair6 = new FlavorPair();
		flavorPair6.setIngredient1("Yoghurt");
		flavorPair6.setIngredient2("Chicken");
		flavorPair6.setCategory1("Dairy");
		flavorPair6.setCategory2("Meat");
		flavorPair6.setAffinity(Affinity.EXCELLENT.name());
		flavorController.addPair(flavorPair6);

		Iterable<LatestPairing> latest = flavorController.getLatestPairings();
		List<LatestPairing> pairings = new ArrayList<>();
		for (LatestPairing latestPairing : latest) {
			pairings.add(latestPairing);
		}
		assertEquals(5, pairings.size());
		assertEquals("Yoghurt", pairings.get(0).getIngredient1());
		assertEquals("Chicken", pairings.get(0).getIngredient2());
		assertEquals("Coriander", pairings.get(1).getIngredient1());
		assertEquals("Chicken", pairings.get(1).getIngredient2());
		assertEquals("Coriander", pairings.get(2).getIngredient1());
		assertEquals("Yoghurt", pairings.get(2).getIngredient2());
		assertEquals("Carrot", pairings.get(3).getIngredient1());
		assertEquals("Butter", pairings.get(3).getIngredient2());
		assertEquals("Chicken", pairings.get(4).getIngredient1());
		assertEquals("Butter", pairings.get(4).getIngredient2());
	}
}
