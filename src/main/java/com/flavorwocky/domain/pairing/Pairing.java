package com.flavorwocky.domain.pairing;

import com.flavorwocky.domain.ingredient.Ingredient;

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

   /* public void save() throws DbException{
        new PairingDao().save(this);
    }*/
}
