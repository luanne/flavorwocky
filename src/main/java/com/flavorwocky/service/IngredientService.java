package com.flavorwocky.service;

import com.flavorwocky.domain.ingredient.db.IngredientDao;

import java.util.List;

/**
 * Created by luanne on 23/08/14.
 */
public class IngredientService {

    public List<String> getAllIngredients() {
        return new IngredientDao().getAllIngredients();
    }
}
