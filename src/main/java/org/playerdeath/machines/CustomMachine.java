package org.playerdeath.machines;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomMachine {
    private String name;
    private Material displayItem;
    private ItemStack[][] recipe;

    public CustomMachine(String name, Material displayItem, ItemStack[][] recipe) {
        this.name = name;
        this.displayItem = displayItem;
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(Material displayItem) {
        this.displayItem = displayItem;
    }

    public ItemStack[][] getRecipe() {
        return recipe;
    }

    public void setRecipe(ItemStack[][] recipe) {
        this.recipe = recipe;
    }
}
