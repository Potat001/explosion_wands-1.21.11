package com.explosion_wands.wands;

import com.explosion_wands.customFunctions.tnt.CustomTnt;
import com.explosion_wands.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TNTInstantBarrageWand {

    public static InteractionResult use(Level level, Player player)  {
        if (level instanceof ServerLevel serverLevel && player != null && !level.isClientSide()) {
            float volume = 0.4F;
            float pitch = 1.0F;
            int spawnHeight = 30;
            int reach = 360;
            int tntAmount = 80;
            //Makes the start spawn angle of the TNT be equal to the direction the player is facing (default (0): east)
            final double[] angle = {Math.toRadians(player.getYRot() + 90)};
            double angleStep = Math.PI / ((double) tntAmount / 2); //How smooth the curve looks
            double amplitude = 15; //Width of the curve
            int initialPos = 0;
            int angleValue = 0;
            int fuse = 200;
            float explosionPower = 10.0F;
            boolean explodeOnContact = true;
            double defaultGravity = 0.04;
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
            final double[] changePosition = {initialPos}; //Initial position of the starting TNT
            for (int i = 0; i < tntAmount; i++) {
                //Creates primed TNTs every iteration
                CustomTnt customTnt = ModEntities.CUSTOM_TNT.create(level, EntitySpawnReason.TRIGGERED);
                //X dir: cos, Z dir: sin, makes a circle
                if (customTnt != null) {
                    customTnt.setPos(target.getX() + (Math.cos(angle[angleValue]) * amplitude),
                            target.getY() + spawnHeight,
                            target.getZ() + (Math.sin(angle[angleValue]) * amplitude));
                    customTnt.setFuse(fuse);
                    customTnt.setExplosionPower(explosionPower);
                    customTnt.setExplodeOnContact(explodeOnContact);
                    customTnt.setDefaultGravity(defaultGravity);
                    //Adds the primed TNT to the world
                    serverLevel.addFreshEntity(customTnt);
                    if (customTnt.touchingUnloadedChunk()) {
                        customTnt.discard();
                    }
                    //Changes the initial angle by the value of angleStep every iteration so the TNTs are not static
                    angle[angleValue] += angleStep;
                    //Height of the cos curve every iteration
                    changePosition[angleValue] += Math.PI / ((double) (tntAmount / 4) / 2);
                }
            }
            //Plays a sound when a block is clicked
            level.playSound(null,
                    target.getX(),
                    target.getY() + spawnHeight,
                    target.getZ(),
                    SoundEvents.TNT_PRIMED,
                    SoundSource.PLAYERS,
                    volume,
                    pitch);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }
}
