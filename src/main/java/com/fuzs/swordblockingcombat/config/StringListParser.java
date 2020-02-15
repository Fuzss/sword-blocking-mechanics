package com.fuzs.swordblockingcombat.config;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Predicate;

public class StringListParser {

    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("C29FA54E-C2BD-4137-897D-D7B363DCA34B");
    private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("45A03570-9501-4902-ADA9-7E2EF5A2C2D8");
    private static final UUID ARMOR_MODIFIER = UUID.fromString("2202169B-0BD6-4597-B623-DA8F7161F135");
    private static final UUID ARMOR_TOUGHNESS_MODIFIER = UUID.fromString("4BAB58F7-1BB5-47EB-9A5F-8EB41ECB2289");

    private final Item itemRegistryDefault = ForgeRegistries.ITEMS.getValue(ForgeRegistries.ITEMS.getDefaultKey());

    private Optional<ResourceLocation> parseResourceLocation(String source) {

        String[] s = source.split(":");
        Optional<ResourceLocation> location = Optional.empty();
        if (s.length == 1) {

            location = Optional.of(new ResourceLocation(s[0]));
        } else if (s.length == 2) {

            location = Optional.of(new ResourceLocation(s[0], s[1]));
        } else {

            this.logStringParsingError(source, "Insufficient number of arguments");
        }

        return location;
    }

    private Optional<Item> getItemFromRegistry(ResourceLocation location) {

        Item item = ForgeRegistries.ITEMS.getValue(location);
        if (item != null && item != this.itemRegistryDefault) {

            return Optional.of(item);
        } else {

            this.logStringParsingError(location.toString(), "Item not found");
        }

        return Optional.empty();
    }

    private Optional<Item> getItemFromRegistry(String source) {

        Optional<ResourceLocation> location = this.parseResourceLocation(source);
        return location.isPresent() ? this.getItemFromRegistry(location.get()) : Optional.empty();
    }

    private void logStringParsingError(String item, String message) {
        SwordBlockingCombat.LOGGER.error("Unable to parse entry \"" + item + "\": " + message);
    }

    Set<Item> buildItemSet(List<String> locations) {
        return this.buildItemSetWithCondition(locations, flag -> true, "");
    }

    Set<Item> buildItemSetWithCondition(List<String> locations, Predicate<Item> condition, String message) {

        Set<Item> set = Sets.newHashSet();
        for (String source : locations) {

            this.parseResourceLocation(source).flatMap(this::getItemFromRegistry).ifPresent(item -> {

                if (condition.test(item)) {

                    set.add(item);
                } else {

                    this.logStringParsingError(source, message);
                }
            });
        }

        return set;
    }

    Map<Item, Double> buildItemMap(List<String> locations) {
        return this.buildItemMapWithCondition(locations, flag -> true, "");
    }

    Map<Item, Double> buildItemMapWithCondition(List<String> locations, Predicate<Double> condition, String message) {

        Map<Item, Double> map = Maps.newHashMap();
        for (String source : locations) {

            String[] s = source.split(",");
            if (s.length == 2) {

                Optional<Item> item = this.getItemFromRegistry(s[0]);
                Optional<Double> size = Optional.empty();
                try {

                    size = Optional.of(Double.parseDouble(s[1]));
                } catch (NumberFormatException e) {

                    this.logStringParsingError(source, "Invalid number format");
                }

                if (item.isPresent() && size.isPresent()) {

                    if (condition.test(size.get())) {

                        map.put(item.get(), size.get());
                    } else {

                        this.logStringParsingError(source, message);
                    }
                }
            } else {

                this.logStringParsingError(source, "Insufficient number of arguments");
            }
        }

        return map;
    }

    void buildAttributeMap(List<String> locations, AttributeModifierType type, Map<Item, Map<String, AttributeModifier>> origin) {

        this.buildItemMap(locations).forEach((key, value) -> {

            AttributeModifier modifier = new AttributeModifier(type.getModifier(), "General modifier", value, AttributeModifier.Operation.ADDITION);
            Map<String, AttributeModifier> map = origin.get(key);
            if (map != null) {

                map.put(type.getName(), modifier);
            } else {

                origin.put(key, new HashMap<String, AttributeModifier>() {{
                    this.put(type.getName(), modifier);
                }});
            }
        });
    }

    enum AttributeModifierType {

        ATTACK_DAMAGE(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), ATTACK_DAMAGE_MODIFIER),
        ATTACK_SPEED(SharedMonsterAttributes.ATTACK_SPEED.getName(), Item.ATTACK_SPEED_MODIFIER),
        ARMOR(SharedMonsterAttributes.ARMOR.getName(), ARMOR_MODIFIER),
        ARMOR_TOUGHNESS(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), ARMOR_TOUGHNESS_MODIFIER);

        private final String name;
        private final UUID modifier;

        AttributeModifierType(String name, UUID modifier) {

            this.name = name;
            this.modifier = modifier;
        }

        public String getName() {
            return this.name;
        }

        public UUID getModifier() {
            return this.modifier;
        }

    }

}
