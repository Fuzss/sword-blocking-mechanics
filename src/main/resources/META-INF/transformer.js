var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');

function initializeCoreMod() {
    return {
        'infinity_enchantment_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.enchantment.InfinityEnchantment'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_77326_a",
                    name: "canApplyTogether",
                    desc: "(Lnet/minecraft/enchantment/Enchantment;)Z",
                    patch: patchInfinityEnchantmentCanApplyTogether
                }, classNode, "InfinityEnchantment");
                return classNode;
            }
        },
        'damage_enchantment_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.enchantment.DamageEnchantment'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_77326_a",
                    name: "canApplyTogether",
                    desc: "(Lnet/minecraft/enchantment/Enchantment;)Z",
                    patch: patchDamageEnchantmentCanApplyTogether
                }, classNode, "DamageEnchantment");
                return classNode;
            }
        },
        'tool_item_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ToolItem'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_77326_a",
                    name: "hitEntity",
                    desc: "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/LivingEntity;)Z",
                    patch: patchToolItemHitEntity
                }, classNode, "ToolItem");
                return classNode;
            }
        },
        'player_entity_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.entity.player.PlayerEntity'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_71059_n",
                    name: "attackTargetEntityWithCurrentItem",
                    desc: "(Lnet/minecraft/entity/Entity;)V",
                    patch: patchPlayerEntityAttackTargetEntityWithCurrentItem
                }, classNode, "PlayerEntity");
                return classNode;
            }
        },
        'item_stack_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_111283_C",
                    name: "getAttributeModifiers",
                    desc: "(Lnet/minecraft/inventory/EquipmentSlotType;)Lcom/google/common/collect/Multimap;",
                    patch: patchItemStackGetAttributeModifiers
                }, classNode, "ItemStack");
                return classNode;
            }
        }
    };
}

function findMethod(methods, entry) {
    var length = methods.length;
    for (var i = 0; i < length; i++) {
        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {
            return method;
        }
    }
    return null;
}

function patch(entry, classNode, name) {
    var method = findMethod(classNode.methods, entry);
    var flag;
    log("Patching " + name + "...");
    if (method !== null) {
        var obfuscated = method.name.equals(entry.obfName);
        flag = entry.patch(method, obfuscated);
    }
    if (flag) {
        log("Patching " + name + " was successful");
    } else {
        log("Patching " + name + " failed");
    }
}

function patchItemStackGetAttributeModifiers(method, obfuscated) {
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if (node instanceof MethodInsnNode && node.getOpcode().equals(Opcodes.INVOKEVIRTUAL) && node.owner.equals("net/minecraft/item/Item") && node.name.equals("getAttributeModifiers") && node.desc.equals("(Lnet/minecraft/inventory/EquipmentSlotType;Lnet/minecraft/item/ItemStack;)Lcom/google/common/collect/Multimap;")) {
            var nextNode = node.getNext();
            if (nextNode instanceof VarInsnNode && nextNode.getOpcode().equals(Opcodes.ASTORE)) {
                foundNode = node;
                break;
            }
        }
    }
    if (foundNode != null) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/swordblockingcombat/asm/Hooks", "adjustAttributeMap", "(Lcom/google/common/collect/Multimap;Lnet/minecraft/inventory/EquipmentSlotType;Lnet/minecraft/item/ItemStack;)Lcom/google/common/collect/Multimap;", false));
        method.instructions.insertBefore(getNthNode(foundNode, 1), insnList);
        return true;
    }
}

function patchPlayerEntityAttackTargetEntityWithCurrentItem(method, obfuscated) {
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if (node instanceof VarInsnNode && node.getOpcode().equals(Opcodes.ILOAD) && node.var.equals(10)) {
            var nextNode = node.getNext();
            if (nextNode instanceof JumpInsnNode && nextNode.getOpcode().equals(Opcodes.IFEQ)) {
                foundNode = node;
                break;
            }
        }
    }
    if (foundNode != null) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new VarInsnNode(Opcodes.FLOAD, 2));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/swordblockingcombat/asm/Hooks", "doSweeping", "(ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;F)Z", false));
        method.instructions.insertBefore(getNthNode(foundNode, 1), insnList);
        return true;
    }
}

function patchToolItemHitEntity(method, obfuscated) {
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if (node instanceof InsnNode && node.getOpcode().equals(Opcodes.ICONST_2)) {
            var nextNode = node.getNext();
            if (nextNode instanceof VarInsnNode && nextNode.getOpcode().equals(Opcodes.ALOAD) && nextNode.var.equals(3)) {
                foundNode = node;
                break;
            }
        }
    }
    if (foundNode != null) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/swordblockingcombat/asm/Hooks", "hitEntityAmount", "(Lnet/minecraft/item/ToolItem;)I", false));
        method.instructions.insertBefore(foundNode, insnList);
        method.instructions.remove(foundNode);
        return true;
    }
}

function patchDamageEnchantmentCanApplyTogether(method, obfuscated) {
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if (node instanceof TypeInsnNode && node.getOpcode().equals(Opcodes.INSTANCEOF) && node.desc.equals("net/minecraft/enchantment/DamageEnchantment")) {
            var nextNode = node.getNext();
            if (nextNode instanceof JumpInsnNode) {
                foundNode = node;
                break;
            }
        }
    }
    if (foundNode != null) {
        var insnList = new InsnList();
        insnList.add(new JumpInsnNode(Opcodes.IFNE, getNthNode(foundNode, 1).label));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/enchantment/ImpalingEnchantment"));
        method.instructions.insertBefore(getNthNode(foundNode, 1), insnList);
        return true;
    }
}

function patchInfinityEnchantmentCanApplyTogether(method, obfuscated) {
    var canApplyTogether = obfuscated ? "func_77326_a" : "canApplyTogether";
    var instructions = method.instructions.toArray();
    if (instructions.length > 0) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/enchantment/Enchantment", canApplyTogether, "(Lnet/minecraft/enchantment/Enchantment;)Z", false));
        insnList.add(new InsnNode(Opcodes.IRETURN));
        method.instructions.insertBefore(instructions[0], insnList);
        return true;
    }
}

function getNthNode(node, n) {
    for (var i = 0; i < Math.abs(n); i++) {
        if (n < 0) {
            node = node.getPrevious();
        } else {
            node = node.getNext();
        }
    }
    return node;
}

function log(s) {
    print("[Sword Blocking Combat Transformer]: " + s);
}