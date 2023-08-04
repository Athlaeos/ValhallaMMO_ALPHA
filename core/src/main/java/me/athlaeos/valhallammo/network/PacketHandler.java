package me.athlaeos.valhallammo.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.nms.NMS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PacketHandler implements Listener {
    private final NMS nms;
    private final IPacketHandler handler;

    public PacketHandler(IPacketHandler handler){
        this.handler = handler;
        this.nms = ValhallaMMO.getNMS();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        close(event.getPlayer());
    }

    public void inject(Player player) {
        ChannelDuplexHandler duplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                if (!handler.readBefore(player, packet)) return;
                super.channelRead(ctx, packet);
                handler.readAfter(player, packet);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
                if (!handler.writeBefore(player, packet)) return;
                super.write(ctx, packet, promise);
                handler.writeAfter(player, packet);
            }
        };

        nms.channel(player).pipeline().addBefore("packet_handler", "valmmo_blockbreak_" + player.getUniqueId(), duplexHandler);
    }

    public void close(Player player) {
        Channel channel = nms.channel(player);
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("valmmo_blockbreak_" + player.getUniqueId());
            return null;
        });
    }

    public void send(Player player, Object packet) {
        nms.channel(player).pipeline().writeAndFlush(packet);
    }
}
