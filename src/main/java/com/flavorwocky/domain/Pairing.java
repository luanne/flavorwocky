package com.flavorwocky.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "PAIRS_WITH")
public class Pairing {

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

    /*public void save() throws DbException {
        firstIngredient.setName(Character.toUpperCase(firstIngredient.getName().charAt(0)) + firstIngredient.getName().substring(1));
        secondIngredient.setName(Character.toUpperCase(secondIngredient.getName().charAt(0)) + secondIngredient.getName().substring(1));
        new PairingDao().save(this);
    }*/
}
