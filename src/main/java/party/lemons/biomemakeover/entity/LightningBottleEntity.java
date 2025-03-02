package party.lemons.biomemakeover.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import party.lemons.biomemakeover.init.*;
import party.lemons.biomemakeover.util.EntityUtil;
import party.lemons.biomemakeover.util.NetworkUtil;

import java.util.Iterator;
import java.util.List;

public class LightningBottleEntity extends ThrownItemEntity
{
	public LightningBottleEntity(EntityType<? extends LightningBottleEntity> entityType, World world) {
		super(entityType, world);
	}

	public LightningBottleEntity(World world, LivingEntity owner) {
		super(BMEntities.LIGHTNING_BOTTLE, owner, world);
	}

	public LightningBottleEntity(World world, double x, double y, double z) {
		super(BMEntities.LIGHTNING_BOTTLE, x, y, z, world);
	}

	protected Item getDefaultItem() {
		return BMItems.LIGHTNING_BOTTLE;
	}

	protected float getGravity() {
		return 0.07F;
	}

	protected void onCollision(HitResult hitResult)
	{
		super.onCollision(hitResult);
		NetworkUtil.doLightningSplash(world, true, getBlockPos());

		if (!this.world.isClient)
		{
			world.playSound(null, getBlockPos(), BMEffects.BOTTLE_THUNDER, SoundCategory.NEUTRAL, 50F, 0.8F + this.random.nextFloat() * 0.2F);

			Box box = this.getBoundingBox().expand(4.0D, 2.0D, 4.0D);
			List<LivingEntity> entities = this.world.getEntitiesByClass(LivingEntity.class, box, EntityPredicates.VALID_LIVING_ENTITY);
			if (!entities.isEmpty())
			{
				Iterator<LivingEntity> iterator = entities.iterator();

				while(iterator.hasNext())
				{
					LivingEntity e = iterator.next();
					double distance = this.squaredDistanceTo(e);
					if (distance < 16.0D)
					{
						int fireTicks = e.getFireTicks();
						boolean isInvul = e.isInvulnerable();

						LightningEntity dummyLightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
						dummyLightning.setPos(e.getX(), e.getY(), e.getZ());
						e.setInvulnerable(true);
						e.onStruckByLightning((ServerWorld) world, dummyLightning);

						e.setFireTicks(fireTicks);
						e.setInvulnerable(isInvul);
						dummyLightning.remove();
					}
				}
			}

			//Loop through again to grab transformed entities for damage & effect
			entities = this.world.getEntitiesByClass(LivingEntity.class, box, EntityPredicates.VALID_LIVING_ENTITY);
			if (!entities.isEmpty())
			{
				Iterator<LivingEntity> iterator = entities.iterator();

				while(iterator.hasNext())
				{
					LivingEntity e = iterator.next();
					double distance = this.squaredDistanceTo(e);
					if (distance < 16.0D)
					{
						NetworkUtil.doLightningEntity(world, e, 100);

						if(!e.hasStatusEffect(BMPotions.SHOCKED))
						{
							e.addStatusEffect(new StatusEffectInstance(BMPotions.SHOCKED, 1000, 0));
						}
						else
						{
							e.addStatusEffect(new StatusEffectInstance(BMPotions.SHOCKED, 1000, Math.min(3, e.getStatusEffect(BMPotions.SHOCKED).getAmplifier() + 1)));
						}
						e.damage(DamageSource.magic(this, this.getOwner()), 0);
						if(getOwner() instanceof LivingEntity)
						{
							e.setAttacker((LivingEntity) getOwner());
						}

						if(e.getHealth() > e.getMaxHealth())
							e.setHealth(e.getMaxHealth());
					}
				}
			}
			this.remove();

		}

	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return new CustomPayloadS2CPacket(BMNetwork.SPAWN_ENTITY, EntityUtil.WriteEntitySpawn(this));
	}
}
