package com.explosion_wands.wands;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FireballShotgunWand extends Item {
    public FireballShotgunWand(Properties properties) {
        super(properties);
    }

    public static InteractionResult use(Item item, Level level, Player player, InteractionHand hand) {
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hitResult.getType() != HitResult.Type.BLOCK && !level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    public static Projectile asFireballProjectile(Level level, Player player) {
        if(level instanceof ServerLevel server) {
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        //Max distance we can click on an entity

        float volume = 0.8F;
        float pitch = 1.0F;
        int reach = 360;
        double incremented = 2;
        double changePos = 0;
        double fireballAmount = 50;
        //Clicks on air/liquid
        int explosionPowerAir = 9;
        //fireball's velocity
        double velocity = 3;
        double dirX = player.getX();
        double dirY = player.getY();
        double dirZ = player.getZ();
        int degrees = 90;
        double layerStartDirForwardScale = 2.5;
        double directlyUpScale = -0.25;
        double playerStartDirRightBlockHitX = 0;
            double playerStartDirRightBlockHitY = 0.25;
            double playerStartDirRightBlockHitZ = 0;
        //Looking directly up
            double directlyUpDir = Math.toRadians(player.getXRot() + degrees);
            //Looking directly down
            double directlyDownDir = Math.toRadians(player.getXRot() - degrees);
        Vec3 playerStartDirForward = player.getLookAngle().normalize();
        Vec3 directlyUp = new Vec3(0,1,0);
        if(directlyUpDir == 0 || directlyDownDir == 0) {
            directlyUp = new Vec3(0,0,1);
        }
        Vec3 playerStartDir = player.getEyePosition();
        playerStartDirForward.add(dirX, dirY, dirZ).normalize();
            //Sets the x and z directions to these numbers as a workaround for when we look directly up or down, which makes
            //the vectors be (0,0,0) no matter what. This should be indistinguishable compared to if they are 0
            Vec3 playerStartDirRight = playerStartDirForward.cross(directlyUp).normalize();
        for(int i = 0; i < fireballAmount; i++) {
            double playerStartDirRightScale = incremented * (fireballAmount / 2) - (changePos + incremented / 2);
            LargeFireball fireballAir = new LargeFireball(level, player, playerStartDirForward, explosionPowerAir);
            //Fireball's initial spawn position
            if(blockHitResult.getType() != HitResult.Type.BLOCK) {
                    Vec3 fireballInAirPosition = playerStartDir.add(playerStartDirForward.scale(layerStartDirForwardScale)) //in front
                            //Ensures that the fireballs are evenly distributed in front of the player
                            .add(playerStartDirRight.scale((playerStartDirRightScale))) //left/right
                            .add(directlyUp.scale(directlyUpScale)); //up/down
                    //Sets the fireball's position
                    fireballAir.moveOrInterpolateTo(fireballInAirPosition);
            }
            if(blockHitResult.getType() == HitResult.Type.BLOCK) {
                    Vec3 fireballInAirPosition = blockHitResult.getLocation() //in front
                            //Ensures that the fireballs are evenly distributed in front of the player
                            .add(playerStartDirRight.scale((playerStartDirRightScale))) //left/right
                            .add(playerStartDirRightBlockHitX, playerStartDirRightBlockHitY, playerStartDirRightBlockHitZ); //up/down
                    //Sets the fireball's position
                    fireballAir.moveOrInterpolateTo(fireballInAirPosition);
            }
            //Set's the fireball's velocity
            fireballAir.setDeltaMovement(playerStartDirForward.scale(velocity));
            //Ensures the sound is not played for every single fireball that spawns
            fireballAir.addTag("fireball");
            //Spawns the fireball
            server.addFreshEntity(fireballAir);
            if(fireballAir.touchingUnloadedChunk()) {
                fireballAir.discard();
            }
            changePos += incremented;
        }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, volume, pitch);
        }
        return null;
    }
 }
