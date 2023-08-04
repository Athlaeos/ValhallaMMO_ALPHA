package me.athlaeos.valhallammo.nms;
//
//import io.netty.channel.Channel;
//import me.athlaeos.valhallammo.dom.DigInfo;
//import me.athlaeos.valhallammo.utility.Utils;
//import net.md_5.bungee.api.chat.BaseComponent;
//import net.minecraft.server.v1_16_R3.*;
//import org.bukkit.Sound;
//import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
//import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
//import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.EquipmentSlot;
//
//import java.lang.reflect.Field;
//import java.util.List;
//
//public class NMS_v1_16_R3 implements NMS{
//
//    // block nms code was partially copied from https://gitlab.com/ranull/minecraft/dualwield/-/tree/master/
//
//    @Override
//    public Channel channel(Player p) {
//        try {
//            Field c = PlayerConnection.class.getDeclaredField("networkManager");
//            c.setAccessible(true);
//            PlayerConnection connection = (PlayerConnection) c.get(((CraftPlayer) p).getHandle().playerConnection);
//            return connection.networkManager.channel;
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public DigInfo packetInfoAdapter(Player p, Object packet) {
//        if (!(packet instanceof PacketPlayInBlockDig)) return null;
//        PacketPlayInBlockDig packetDig = (PacketPlayInBlockDig) packet;
//        BlockPosition pos = packetDig.b();
//        org.bukkit.block.Block b = p.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
//        return new DigInfo(p, b, blockHardness(b), p.getInventory().getItemInMainHand(), Utils.getRandom().nextInt(2000));
//    }
//
//    @Override
//    public void swingAnimation(Player p, EquipmentSlot slot) {
//        EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
//        PlayerConnection playerConnection = entityPlayer.playerConnection;
//        PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation(entityPlayer, slot == EquipmentSlot.HAND ? 0 : 3);
//
//        playerConnection.sendPacket(packetPlayOutAnimation);
//        playerConnection.a(new PacketPlayInArmAnimation(slot == EquipmentSlot.HAND ? EnumHand.MAIN_HAND
//                : EnumHand.OFF_HAND));
//
//    }
//
//    @Override
//    public void blockBreakAnimation(Player p, org.bukkit.block.Block b, int id, int stage) {
//        EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
//        PlayerConnection playerConnection = entityPlayer.playerConnection;
//        BlockPosition blockPosition = new BlockPosition(b.getX(), b.getY(), b.getZ());
//
//        playerConnection.sendPacket(new PacketPlayOutBlockBreakAnimation(id, blockPosition, stage));
//
//    }
//
//    @Override
//    public void blockParticleAnimation(org.bukkit.block.Block b) {
//        b.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_CRACK, b.getLocation().add(0.5, 0, 0.5),
//                10, b.getBlockData());
//    }
//
//    @Override
//    public float toolPower(org.bukkit.inventory.ItemStack tool, org.bukkit.block.Block b) {
//        if (tool.getAmount() != 0) {
//            ItemStack craftItemStack = CraftItemStack.asNMSCopy(tool);
//            World nmsWorld = ((CraftWorld) b.getWorld()).getHandle();
//            Block nmsBlock = nmsWorld.getType(new BlockPosition(b.getX(), b.getY(), b.getZ())).getBlock();
//
//            return craftItemStack.a(nmsBlock.getBlockData());
//        }
//
//        return 1;
//    }
//
//    @Override
//    public Sound blockSound(org.bukkit.block.Block b) {
//        try {
//            World nmsWorld = ((CraftWorld) b.getWorld()).getHandle();
//            Block nmsBlock = nmsWorld.getType(new BlockPosition(b.getX(), b.getY(), b.getZ())).getBlock();
//            SoundEffectType soundEffectType = nmsBlock.getStepSound(nmsBlock.getBlockData());
//
//            Field soundEffectField = soundEffectType.getClass().getDeclaredField("fallSound");
//
//            soundEffectField.setAccessible(true);
//
//            SoundEffect soundEffect = (SoundEffect) soundEffectField.get(soundEffectType);
//            Field keyField = SoundEffect.class.getDeclaredField("b");
//
//            keyField.setAccessible(true);
//
//            MinecraftKey minecraftKey = (MinecraftKey) keyField.get(soundEffect);
//
//            return Sound.valueOf(minecraftKey.getKey().toUpperCase()
//                    .replace(".", "_")
//                    .replace("_FALL", "_HIT"));
//        } catch (NoSuchFieldException | IllegalAccessException ignored) {
//        }
//
//        return Sound.BLOCK_STONE_HIT;
//    }
//
//    @Override
//    public float blockHardness(org.bukkit.block.Block b) {
//        World nmsWorld = ((CraftWorld) b.getWorld()).getHandle();
//        Block nmsBlock = nmsWorld.getType(new BlockPosition(b.getX(), b.getY(), b.getZ())).getBlock();
//
//        return nmsBlock.getBlockData().h(null, null);
//    }
//
//    @Override
//    public boolean breakBlock(Player p, org.bukkit.block.Block b) {
//        EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
//        BlockPosition blockPosition = new BlockPosition(b.getX(), b.getY(), b.getZ());
//
//        return entityPlayer.playerInteractManager.breakBlock(blockPosition);
//    }
//
//    @Override
//    public void setBookContents(org.bukkit.inventory.ItemStack book, List<BaseComponent[]> pages) {
//
//    }
//}
