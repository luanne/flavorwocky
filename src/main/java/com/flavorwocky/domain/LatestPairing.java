package com.flavorwocky.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.typeconversion.DateLong;


@NodeEntity
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class LatestPairing {

	@JsonProperty("id")
	Long id;

	@DateLong
	Date dateAdded;
	String ingredient1;
	String ingredient2;

	public LatestPairing() {
	}

	public LatestPairing(String ingredient1, String ingredient2, Date dateAdded) {
		this.ingredient1 = ingredient1;
		this.ingredient2 = ingredient2;
		this.dateAdded = dateAdded;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getIngredient1() {
		return ingredient1;
	}

	public void setIngredient1(String ingredient1) {
		this.ingredient1 = ingredient1;
	}

	public String getIngredient2() {
		return ingredient2;
	}

	public void setIngredient2(String ingredient2) {
		this.ingredient2 = ingredient2;
	}
}
