package com.flavorwocky.domain.ingredient;

/**
 * Created by luanne on 11/06/14.
 */
public class Ingredient {

    private String name;
    private Category category;

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
}
