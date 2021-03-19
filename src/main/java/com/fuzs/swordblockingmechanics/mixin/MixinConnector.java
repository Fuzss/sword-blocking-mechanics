package com.fuzs.swordblockingmechanics.mixin;

import com.fuzs.swordblockingmechanics.SwordBlockingMechanics;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

@SuppressWarnings("unused")
public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {

        Mixins.addConfiguration("META-INF/" + SwordBlockingMechanics.MODID + ".mixins.json");
    }

}
