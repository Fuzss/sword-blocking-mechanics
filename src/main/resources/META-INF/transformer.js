var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
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
                patch([{
                    obfName: "func_77326_a",
                    name: "canApplyTogether",
                    desc: "(Lnet/minecraft/enchantment/Enchantment;)Z",
                    patch: patchInfinityEnchantmentCanApplyTogether
                }], classNode, "InfinityEnchantment");
                return classNode;
            }
        },
        'damage_enchantment_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.enchantment.DamageEnchantment'
            },
            'transformer': function(classNode) {
                patch([{
                    obfName: "func_77326_a",
                    name: "canApplyTogether",
                    desc: "(Lnet/minecraft/enchantment/Enchantment;)Z",
                    patch: patchDamageEnchantmentCanApplyTogether
                }], classNode, "DamageEnchantment");
                return classNode;
            }
        },
        'tool_item_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ToolItem'
            },
            'transformer': function(classNode) {
                patch([{
                    obfName: "func_77326_a",
                    name: "hitEntity",
                    desc: "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/LivingEntity;)Z",
                    patch: patchToolItemHitEntity
                }], classNode, "ToolItem");
                return classNode;
            }
        },
        'player_entity_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.entity.player.PlayerEntity'
            },
            'transformer': function(classNode) {
                patch([{
                    obfName: "func_71059_n",
                    name: "attackTargetEntityWithCurrentItem",
                    desc: "(Lnet/minecraft/entity/Entity;)V",
                    patch: patchPlayerEntityAttackTargetEntityWithCurrentItem
                }], classNode, "PlayerEntity");
                return classNode;
            }
        },
        'item_stack_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.ItemStack'
            },
            'transformer': function(classNode) {
                patch([{
                    obfName: "func_111283_C",
                    name: "getAttributeModifiers",
                    desc: "(Lnet/minecraft/inventory/EquipmentSlotType;)Lcom/google/common/collect/Multimap;",
                    patch: patchItemStackGetAttributeModifiers
                }], classNode, "ItemStack");
                return classNode;
            }
        },
        'item_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.item.Item'
            },
            'transformer': function(classNode) {
                patch([{
                    obfName: "func_77626_a",
                    name: "getUseDuration",
                    desc: "(Lnet/minecraft/item/ItemStack;)I",
                    patch: patchItemGetUseDuration
                }], classNode, "Item");
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

function patch(entries, classNode, name) {
    log("Patching " + name + "...");
    for (var i = 0; i < entries.length; i++) {
        var entry = entries[i];
        var method = findMethod(classNode.methods, entry);
        if (method !== null) {
            var obfuscated = method.name.equals(entry.obfName);
            var flag = entry.patch(method, obfuscated);
        }
        if (flag) {
            log("Patching " + name + "#" + entry.name + " was successful");
        } else {
            log("Patching " + name + "#" + entry.name + " failed");
        }
    }
}

function patchItemGetUseDuration(method, obfuscated) {
    var isFastEating = obfuscated ? "func_221465_e" : "isFastEating";
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if (node instanceof VarInsnNode && node.getOpcode().equals(Opcodes.ALOAD) && node.var.equals(0)) {
            var nextNode = getNthNode(node, 2);
            if (nextNode instanceof MethodInsnNode && nextNode.getOpcode().equals(Opcodes.INVOKEVIRTUAL) && nextNode.owner.equals("net/minecraft/item/Food") && nextNode.name.equals(isFastEating) && nextNode.desc.equals("()Z")) {
                foundNode = node;
                break;
            }
        }
    }
    if (foundNode != null) {
        var insnList = new InsnList();
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/swordblockingcombat/asm/Hooks", "getFoodDuration", "(Lnet/minecraft/item/Item;)I", false));
        insnList.add(new InsnNode(Opcodes.IRETURN));
        method.instructions.insert(foundNode, insnList);
        return true;
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
        method.instructions.insert(foundNode, insnList);
        return true;
    }
}

function patchPlayerEntityAttackTargetEntityWithCurrentItem(method, obfuscated) {
    var getValue = obfuscated ? "func_111126_e" : "getValue";
    var foundNodeDamage;
    var foundNodeSweeping;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        var neighbor;
        if (node instanceof InsnNode && node.getOpcode().equals(Opcodes.D2F)) {
            neighbor = node.getNext();
            if (neighbor instanceof VarInsnNode && neighbor.getOpcode().equals(Opcodes.FSTORE) && neighbor.var.equals(2)) {
                neighbor = node.getPrevious();
                if (neighbor instanceof MethodInsnNode && neighbor.getOpcode().equals(Opcodes.INVOKEINTERFACE) && neighbor.owner.equals("net/minecraft/entity/ai/attributes/IAttributeInstance") && neighbor.name.equals(getValue) && neighbor.desc.equals("()D")) {
                    foundNodeDamage = node;
                }
            }
        }
        if (node instanceof VarInsnNode && node.getOpcode().equals(Opcodes.ILOAD) && node.var.equals(10)) {
            neighbor = node.getNext();
            if (neighbor instanceof JumpInsnNode && neighbor.getOpcode().equals(Opcodes.IFEQ)) {
                foundNodeSweeping = node;
            }
        }
        if (foundNodeDamage != null && foundNodeSweeping != null) {
            break;
        }
    }
    var flag1, flag2;
    if (foundNodeDamage != null) {
        var insnList1 = new InsnList();
        insnList1.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList1.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList1.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/swordblockingcombat/asm/Hooks", "addEnchantmentDamage", "(FLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)F", false));
        method.instructions.insert(foundNodeDamage, insnList1);
        flag1 = true;
    }
    if (foundNodeSweeping != null) {
        var insnList2 = new InsnList();
        insnList2.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList2.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList2.add(new VarInsnNode(Opcodes.FLOAD, 2));
        insnList2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/swordblockingcombat/asm/Hooks", "doSweeping", "(ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;F)V", false));
        insnList2.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList2.add(new VarInsnNode(Opcodes.ILOAD, 7));
        insnList2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/swordblockingcombat/asm/Hooks", "restoreSprinting", "(Lnet/minecraft/entity/player/PlayerEntity;I)V", false));
        insnList2.add(new InsnNode(Opcodes.ICONST_0));
        method.instructions.insert(foundNodeSweeping, insnList2);
        flag2 = true;
    }
    return flag1 && flag2;
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
        method.instructions.insert(foundNode, insnList);
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
        method.instructions.insert(foundNode, insnList);
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