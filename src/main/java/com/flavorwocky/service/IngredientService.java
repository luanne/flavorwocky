package com.flavorwocky.service;

import com.flavorwocky.domain.ingredient.IngredientDao;

import java.util.List;

public class IngredientService {

    public List<String> getAllIngredients() {
        return new IngredientDao().getAllIngredients();
    }
}
