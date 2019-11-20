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
        'biped_model_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.entity.model.BipedModel'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_212844_a_",
                    name: "setRotationAngles",
                    desc: "(Lnet/minecraft/entity/LivingEntity;FFFFFF)V",
                    patch: patchBipedModelSetRotationAngles
                }, classNode, "BipedModel");
                return classNode;
            }
        }
    };
}

function findMethod(methods, entry) {
    var length = methods.length;
    for(var i = 0; i < length; i++) {
        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {
            return method;
        }
    }
    return null;
}

function patch(entry, classNode, name) {
    var method = findMethod(classNode.methods, entry);
    log("Patching " + name + "...");
    if(method !== null) {
        var obfuscated = method.name.equals(entry.obfName);
        entry.patch(method, obfuscated);
        log("Patching " + name + " was successful");
    } else {
        log("Patching " + name + " failed");
    }
}

function patchBipedModelSetRotationAngles(method, obfuscated) {
    var swingProgress = obfuscated ? "field_217112_c" : "swingProgress";
    var rotateAngleY = obfuscated ? "field_78796_g" : "rotateAngleY";
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if (node instanceof FieldInsnNode && node.getOpcode().equals(Opcodes.GETFIELD) && node.name.equals(swingProgress)) {
            var prevNode = getNthPrevious(node, 5);
            if (prevNode instanceof FieldInsnNode && prevNode.getOpcode().equals(Opcodes.PUTFIELD) && prevNode.name.equals(rotateAngleY)) {
                foundNode = node;
                break;
            }
        }
    }
    if (foundNode != null) {
        var rightArm = obfuscated ? "field_178723_h" : "bipedRightArm";
        var leftArm = obfuscated ? "field_178724_i" : "bipedLeftArm";
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/model/BipedModel", rightArm, "Lnet/minecraft/client/renderer/entity/model/RendererModel;"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/model/BipedModel", leftArm, "Lnet/minecraft/client/renderer/entity/model/RendererModel;"));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/swordblockingcombat/handler/ThirdPersonBlockingHandler", "setArmRotationAngel", "(Lnet/minecraft/client/renderer/entity/model/RendererModel;Lnet/minecraft/client/renderer/entity/model/RendererModel;)V", false));
        method.instructions.insertBefore(getNthPrevious(node, 2), insnList);
    }
}

function getNthPrevious(node, n) {
    for (var i = 0; i < n; i++) {
        node = node.getPrevious();
    }
    return node;
}

function log(s) {
    print("[Sword Blocking Combat Transformer]: " + s);
}