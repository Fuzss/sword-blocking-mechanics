package fuzs.swordblockingmechanics.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(name = "simple_blocking_pose", description = "Use the much simpler third-person pose when blocking with a sword from Minecaft 1.8 instead of the default one from before that.")
    public boolean simpleBlockingPose = false;
    @Config(name = "full_interact_animations", description = "Allows block hitting to render properly (meaning attacking and then using the item directly afterwards). The hitting animation is no longer consumed as in vanilla.")
    public boolean interactAnimations = true;
}
