package com.tino.pick.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import com.tino.pick.SpawnerPickaxe;

public class ModRecipes {

    public static void registerRecipes() {
        Registry.register(
                BuiltInRegistries.RECIPE_SERIALIZER,
                Identifier.fromNamespaceAndPath(SpawnerPickaxe.MOD_ID, "spawner_pickaxe"),
                SpawnerPickaxeRecipe.SERIALIZER);
    }
}
