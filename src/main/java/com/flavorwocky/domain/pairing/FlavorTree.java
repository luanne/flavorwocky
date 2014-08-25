package com.flavorwocky.domain.pairing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luanne on 23/08/14.
 */
public class FlavorTree {

    private String name;
    private String categoryColor;
    private String affinity;
    private List<FlavorTree> children = new ArrayList<>();

    public FlavorTree() {
    }

    public FlavorTree(String name) {
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

    public void addChild(FlavorTree child) {
        children.add(child);
    }

    public FlavorTree getChildByName(String name) {
        for (FlavorTree child : children) {
            if (child.name.equals(name)) {
                return child;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlavorTree that = (FlavorTree) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
