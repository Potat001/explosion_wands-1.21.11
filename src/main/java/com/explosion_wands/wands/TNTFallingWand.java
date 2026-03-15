package com.explosion_wands.wands;

import com.explosion_wands.customFunctions.tnt.CustomTnt;
import com.explosion_wands.entity.ModEntities;
import com.explosion_wands.sharedValues.ExplosionEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TNTFallingWand {

    public static InteractionResult use(Level level, Player player)  {
        if (level instanceof ServerLevel serverLevel && player != null) {
            int maxEntities = ExplosionEntities.maxEntities;
            int fuse = ExplosionEntities.fuse;
            int spawnedEntities = ExplosionEntities.spawnedEntities;
            float minExplosion = 0.2F;
            float maxExplosion = 2F;
            int secondFuse = 200;
            boolean explodeOnContact = true;
            float explosionPower = 10.0F;
            int minIncrement = ExplosionEntities.minIncrement;
            int maxIncrement = ExplosionEntities.maxIncrement;
            RandomSource random = RandomSource.create();
            float randomExplosion = (minExplosion + random.nextFloat() * (maxExplosion - minExplosion));
            int randomIncrement = minIncrement + random.nextInt(maxIncrement - minIncrement);
            int increment = ExplosionEntities.increment;
            double lessThanTheta = ExplosionEntities.lessThanTheta;
            double lessThanPhi = ExplosionEntities.lessThanPhi;
            double incrementTheta = ExplosionEntities.incrementTheta;
            double incrementPhi = ExplosionEntities.incrementPhi;
            double x = ExplosionEntities.x;
            double y = ExplosionEntities.y;
            double z = ExplosionEntities.z;
            double r = 1.5;
            int spawnHeight = ExplosionEntities.spawnHeight;
            int reach = ExplosionEntities.reach;
            Vec3 playerEyeStart = player.getEyePosition();
            Vec3 playerLookAngle = player.getLookAngle();
            Vec3 playerEyeEnd = playerEyeStart.add(playerLookAngle.scale(reach));
            BlockHitResult blockHitResult = level.clip(new ClipContext(
                    playerEyeStart,
                    playerEyeEnd,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
            ));
            BlockPos target = blockHitResult.getBlockPos();
            //Failsafe in-case we spawn more entities than is intended
            if(spawnedEntities <= maxEntities) {
                for (double theta = ExplosionEntities.theta; theta <= lessThanTheta; theta += incrementTheta) {
                    for (double phi = ExplosionEntities.phi; phi <= lessThanPhi; phi += incrementPhi) {
                        //Adds the entities to the world
                        CustomTnt customTnt = ModEntities.CUSTOM_TNT.create(level, EntitySpawnReason.TRIGGERED);
                        CustomTnt customTnt2 = ModEntities.CUSTOM_TNT.create(level, EntitySpawnReason.TRIGGERED);
                        //This does not make a perfect circle, but it should not be noticeable
                            if (increment <= randomExplosion && customTnt != null) {
                                customTnt.setPos(target.getX(),
                                        target.getY() + spawnHeight,
                                        target.getZ()
                                );
                                customTnt.setFuse(fuse);
                                customTnt.setExplosionPower(randomIncrement);
                                serverLevel.addFreshEntity(customTnt);
                            }
                        if (customTnt2 != null) {
                            if (x != 0 && y != 0 && z != 0) {
                                customTnt2.setPos(target.getX() + x,
                                        target.getY() + y + spawnHeight,
                                        target.getZ() + z
                                );

                                customTnt2.setFuse(secondFuse);
                                customTnt2.setExplodeOnContact(explodeOnContact);
                                customTnt2.setExplosionPower(explosionPower);
                                serverLevel.addFreshEntity(customTnt2);
                            } else {
                                customTnt2.discard();
                            }
                            x = r * Math.sin(theta) * Math.cos(phi);
                            y = r * Math.cos(theta);
                            z = r * Math.sin(theta) * Math.sin(phi);
                            increment++;
                        }
                    }
                }
                //Debugging
                /*
                System.out.println(
                        "Pre-calculated entities:   " + spawnedEntities
                                + ",   entities:   " + increment
                                + ",   random explosion:   " + randomExplosion
                                + ",   random increment:   " + 1
                );
                 */
            }
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }
}
