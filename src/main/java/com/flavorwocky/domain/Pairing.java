package com.flavorwocky.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * @author Luanne Misquitta
 */
@RelationshipEntity(type = "PAIRS_WITH")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Pairing {

	@JsonProperty("id")
	Long id;

	@StartNode
	private Ingredient first;
	@EndNode
	private Ingredient second;
	private Affinity affinity;


	public Pairing() {
	}

	public Ingredient getFirst() {
		return first;
	}

	public void setFirst(Ingredient first) {
		this.first = first;
	}

	public Ingredient getSecond() {
		return second;
	}

	public void setSecond(Ingredient second) {
		this.second = second;
	}

	public Affinity getAffinity() {
		return affinity;
	}

	public void setAffinity(Affinity affinity) {
		this.affinity = affinity;
	}
}
