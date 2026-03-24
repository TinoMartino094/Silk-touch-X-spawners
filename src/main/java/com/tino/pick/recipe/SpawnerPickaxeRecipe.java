package com.tino.pick.recipe;

import com.mojang.serialization.MapCodec;
import com.tino.pick.config.SpawnerPickaxeConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;

import java.util.Optional;

public class SpawnerPickaxeRecipe extends CustomRecipe {
    public static final MapCodec<SpawnerPickaxeRecipe> MAP_CODEC = MapCodec.unit(SpawnerPickaxeRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, SpawnerPickaxeRecipe> STREAM_CODEC = StreamCodec
            .unit(new SpawnerPickaxeRecipe());
    public static final RecipeSerializer<SpawnerPickaxeRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC,
            STREAM_CODEC);

    private HolderLookup.Provider cachedProvider = null;

    public SpawnerPickaxeRecipe() {
        super();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        this.cachedProvider = level.registryAccess();
        boolean foundPickaxe = false;
        boolean foundIngredient = false;
        int filledSlots = 0;

        String configIngredientId = SpawnerPickaxeConfig.get().recipeIngredient;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                filledSlots++;
                if (stack.is(Items.GOLDEN_PICKAXE)) {
                    if (foundPickaxe)
                        return false;
                    foundPickaxe = true;
                } else if (Identifier.parse(configIngredientId)
                        .equals(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
                    if (foundIngredient)
                        return false;
                    foundIngredient = true;
                } else {
                    return false;
                }
            }
        }

        return foundPickaxe && foundIngredient && filledSlots == 2;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack result = new ItemStack(Items.GOLDEN_PICKAXE);

        result.set(DataComponents.CUSTOM_NAME, Component.literal("Spawner Pickaxe")
                .setStyle(Style.EMPTY.withColor(net.minecraft.ChatFormatting.GOLD).withItalic(false)));

        if (this.cachedProvider != null) {
            Optional<Holder.Reference<Enchantment>> silkTouchOpt = this.cachedProvider
                    .lookupOrThrow(Registries.ENCHANTMENT)
                    .get(Enchantments.SILK_TOUCH);
            if (silkTouchOpt.isPresent()) {
                ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                enchantments.set(silkTouchOpt.get(), 10);
                result.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
            }
        }

        result.set(DataComponents.REPAIR_COST, 9999999);

        int damage = SpawnerPickaxeConfig.get().pickaxeDamage;
        if (damage > 0) {
            result.set(DataComponents.DAMAGE, damage);
        }

        return result;
    }

    @Override
    public RecipeSerializer<SpawnerPickaxeRecipe> getSerializer() {
        return SERIALIZER;
    }
}
