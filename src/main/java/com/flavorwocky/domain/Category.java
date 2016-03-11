package com.flavorwocky.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Category {

	@JsonProperty("id")
	@GraphId
	private Long id;

	private String name;
	private String categoryColor;

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategoryColor() {
		return categoryColor;
	}
}
