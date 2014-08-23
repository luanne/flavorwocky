package com.flavorwocky.api;

import java.util.List;

/**
 * Created by luanne on 23/08/14.
 */
public class FlavorTree {

    private String name;
    private String categoryColor;
    private String affinity;
    private List<FlavorTree> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getAffinity() {
        return affinity;
    }

    public void setAffinity(String affinity) {
        this.affinity = affinity;
    }

    public List<FlavorTree> getChildren() {
        return children;
    }

    public void setChildren(List<FlavorTree> children) {
        this.children = children;
    }
}
