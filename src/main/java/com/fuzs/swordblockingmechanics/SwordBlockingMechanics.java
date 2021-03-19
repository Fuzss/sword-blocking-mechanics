package com.fuzs.swordblockingmechanics;

import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import com.fuzs.puzzleslib_sbm.config.ConfigManager;
import com.fuzs.puzzleslib_sbm.element.AbstractElement;
import com.fuzs.puzzleslib_sbm.element.ElementRegistry;
import com.fuzs.swordblockingmechanics.element.CombatTestElement;
import com.fuzs.swordblockingmechanics.element.SwordBlockingElement;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(SwordBlockingMechanics.MODID)
public class SwordBlockingMechanics extends PuzzlesLib {

    public static final String MODID = "swordblockingmechanics";
    public static final String NAME = "Sword Blocking Mechanics";
    public static final Logger LOGGER = LogManager.getLogger(SwordBlockingMechanics.NAME);

    public static final AbstractElement SWORD_BLOCKING = register("sword_blocking", SwordBlockingElement::new);
    public static final AbstractElement COMBAT_TEST = register("combat_test", CombatTestElement::new);

    public SwordBlockingMechanics() {

        ElementRegistry.setup(MODID);
        ConfigManager.get().load();
    }

}
