package com.flavorwocky.domain.pairing;

import com.flavorwocky.domain.ingredient.Ingredient;
import com.flavorwocky.exception.DbException;

/**
 * Created by luanne on 11/06/14.
 */
public class Pairing {

    private Ingredient firstIngredient;
    private Ingredient secondIngredient;
    private Affinity affinity;

    public Ingredient getFirstIngredient() {
        return firstIngredient;
    }

    public void setFirstIngredient(Ingredient firstIngredient) {
        this.firstIngredient = firstIngredient;
    }

    public Ingredient getSecondIngredient() {
        return secondIngredient;
    }

    public void setSecondIngredient(Ingredient secondIngredient) {
        this.secondIngredient = secondIngredient;
    }

    public Affinity getAffinity() {
        return affinity;
    }

    public void setAffinity(Affinity affinity) {
        this.affinity = affinity;
    }

    public void save() throws DbException {
        firstIngredient.setName(Character.toUpperCase(firstIngredient.getName().charAt(0)) + firstIngredient.getName().substring(1));
        secondIngredient.setName(Character.toUpperCase(secondIngredient.getName().charAt(0)) + secondIngredient.getName().substring(1));
        new PairingDao().save(this);
    }
}
