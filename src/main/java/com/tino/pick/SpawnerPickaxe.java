package com.tino.pick;

import com.tino.pick.config.SpawnerPickaxeConfig;
import com.tino.pick.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class SpawnerPickaxe implements ModInitializer {
	public static final String MOD_ID = "spawner-pickaxe";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SpawnerPickaxeConfig.load();
		ModRecipes.registerRecipes();

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (state.is(Blocks.SPAWNER)) {
				ItemStack heldItem = player.getMainHandItem();
				if (heldItem.is(Items.GOLDEN_PICKAXE)) {
					Optional<Holder.Reference<Enchantment>> silkTouchOpt = world.registryAccess()
							.lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.SILK_TOUCH);

					if (silkTouchOpt.isPresent()) {
						int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(silkTouchOpt.get(), heldItem);
						if (silkTouchLevel >= 10) {
							if (!world.isClientSide()) {
								ItemStack spawnerItem = new ItemStack(Items.SPAWNER);

								if (blockEntity != null) {
									net.minecraft.nbt.CompoundTag tag = blockEntity
											.saveWithFullMetadata(world.registryAccess());
									tag.remove("x");
									tag.remove("y");
									tag.remove("z");
									tag.remove("id");
									spawnerItem.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA,
											net.minecraft.world.item.component.TypedEntityData.of(
													net.minecraft.world.level.block.entity.BlockEntityType.MOB_SPAWNER,
													tag));
								}

								ItemEntity drop = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5,
										pos.getZ() + 0.5, spawnerItem);
								drop.setDefaultPickUpDelay();
								world.addFreshEntity(drop);

								heldItem.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
								world.destroyBlock(pos, false);
							}
							return false;
						}
					}
				}
			}
			return true;
		});

		net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.isClientSide())
				return net.minecraft.world.InteractionResult.PASS;
			ItemStack stack = player.getItemInHand(hand);

			if (stack.is(Items.SPAWNER)) {
				net.minecraft.world.item.component.TypedEntityData<net.minecraft.world.level.block.entity.BlockEntityType<?>> component = stack
						.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);

				if (component != null && !player.canUseGameMasterBlocks()) {
					net.minecraft.world.item.context.UseOnContext useOnContext = new net.minecraft.world.item.context.UseOnContext(
							player, hand, hitResult);
					net.minecraft.world.item.context.BlockPlaceContext placeContext = new net.minecraft.world.item.context.BlockPlaceContext(
							useOnContext);

					if (!placeContext.canPlace())
						return net.minecraft.world.InteractionResult.PASS;

					net.minecraft.core.BlockPos placePos = placeContext.getClickedPos();
					net.minecraft.world.level.block.state.BlockState placementState = Blocks.SPAWNER
							.defaultBlockState();

					if (world.setBlock(placePos, placementState, 11)) {
						net.minecraft.world.level.block.entity.BlockEntity be = world.getBlockEntity(placePos);
						if (be != null) {
							component.loadInto(be, world.registryAccess());
							be.setChanged();
						}

						net.minecraft.world.level.block.SoundType soundType = placementState.getSoundType();
						world.playSound(null, placePos, soundType.getPlaceSound(),
								net.minecraft.sounds.SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F,
								soundType.getPitch() * 0.8F);
						world.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.BLOCK_PLACE, placePos);

						if (!player.isCreative()) {
							stack.shrink(1);
						}

						return net.minecraft.world.InteractionResult.SUCCESS;
					}
				}
			}
			return net.minecraft.world.InteractionResult.PASS;
		});

		LOGGER.info("Spawner Pickaxe Initialized!");
	}
}