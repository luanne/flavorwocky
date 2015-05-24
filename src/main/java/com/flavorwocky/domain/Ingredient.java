package com.flavorwocky.domain;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * @author Luanne Misquitta
 */
@NodeEntity
public class Ingredient {

	private Long id;
	private String name;

	@Relationship(type = "HAS_CATEGORY", direction = "OUTGOING")
	private Category category;

	@Relationship(type = "PAIRS_WITH", direction = "UNDIRECTED")
	private Set<Pairing> pairings = new HashSet<>();

	public Ingredient() {
	}

	public Ingredient(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Set<Pairing> getPairings() {
		return pairings;
	}

	public void addPairing(Pairing pairing) {
		pairing.getFirst().getPairings().add(pairing);
		pairing.getSecond().getPairings().add(pairing);
	}
}
