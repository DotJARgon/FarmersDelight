package vectorwing.farmersdelight.common.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.MagmaCube;

import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModParticleTypes;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.MathUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

;

public class FrogFoodItem extends ConsumableItem
{

	private static final Map<FrogVariant, ItemLike> FROGLIGHT_VARIANT = Stream.of(new Object[][] {
			{ FrogVariant.COLD, Blocks.VERDANT_FROGLIGHT},
			{ FrogVariant.WARM, Blocks.PEARLESCENT_FROGLIGHT },
			{ FrogVariant.TEMPERATE, Blocks.OCHRE_FROGLIGHT}
	}).collect(Collectors.toMap(data -> (FrogVariant)data[0], data -> (ItemLike)data[1]));


	public static final List<MobEffectInstance> EFFECTS = Lists.newArrayList(
			new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 6000, 1),
			new MobEffectInstance(MobEffects.JUMP, 6000, 0));

	public FrogFoodItem(Properties properties) {
		super(properties);
	}

	public static void init(){
		UseEntityCallback.EVENT.register(FrogFoodItem.FrogFeedEvent::onFrogFoodApplied);
	}

	public static class FrogFeedEvent
	{

		public static InteractionResult onFrogFoodApplied(Player player, Level level, InteractionHand hand, Entity target,
														 @Nullable EntityHitResult entityHitResult) {
			if (player.isSpectator()) return InteractionResult.PASS;

			ItemStack heldStack = player.getItemInHand(hand);

			if (target instanceof LivingEntity entity && target.getType().is(ModTags.FROG_FOOD_USERS)) {
				boolean isFrog = entity instanceof Frog;

				if (entity.isAlive() && isFrog && heldStack.getItem().equals(ModItems.MAGMA_SORBET.get())) {
					ItemEntity itemEntity = entity.spawnAtLocation((ItemLike)FROGLIGHT_VARIANT.get(((Frog)entity).getVariant()), 1);

					entity.level().playSound(null, target.blockPosition(), SoundEvents.FROGLIGHT_FALL, SoundSource.PLAYERS, 0.8F, 0.8F);

					for (int i = 0; i < 5; ++i) {
						double d0 = MathUtils.RAND.nextGaussian() * 0.02D;
						double d1 = MathUtils.RAND.nextGaussian() * 0.02D;
						double d2 = MathUtils.RAND.nextGaussian() * 0.02D;
						entity.level().addParticle(ModParticleTypes.STAR.get(), entity.getRandomX(1.0D), entity.getRandomY() + 0.5D, entity.getRandomZ(1.0D), d0, d1, d2);
					}

					if (!player.isCreative()) {
						heldStack.shrink(1);
					}

					return InteractionResult.sidedSuccess(level.isClientSide);
				}
			}
			return InteractionResult.PASS;
		}
	}



	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
		if (target instanceof Frog) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
