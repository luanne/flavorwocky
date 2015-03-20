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
}
