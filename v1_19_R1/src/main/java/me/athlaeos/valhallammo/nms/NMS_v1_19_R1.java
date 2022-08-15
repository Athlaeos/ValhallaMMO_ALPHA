package me.athlaeos.valhallammo.nms;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NMS_v1_19_R1 implements NMS{
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
//        System.out.println("player joined");
//        createAttackSwingListener(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
//        System.out.println("player left");
//        removeAttackSwingListener(e.getPlayer());
    }

    @Override
    public void createAttackSwingListener(Player player) {
//        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
//            @Override
//            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
//                super.channelRead(context, packet);
//                if (packet instanceof ServerboundInteractPacket) {
//                    ServerboundInteractPacket info = (ServerboundInteractPacket) packet;
//                    player.sendMessage("READ>> " + info);
//                }
//                System.out.println("GENERAL PACKET READ >> " + packet);
//            }
//
//            @Override
//            public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
//                super.write(context, packet, channelPromise);
//            }
//        };
//
//        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
//        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    @Override
    public void removeAttackSwingListener(Player player) {
//        Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
//        channel.eventLoop().submit(() -> {
//            channel.pipeline().remove(player.getName());
//            return null;
//        });
    }

    @Override
    public void clearAttackSwingListeners() {

    }
}
