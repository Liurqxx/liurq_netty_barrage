package com.liurq.config;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理TextWebSocketFrame
 */
@Slf4j
public class TextWebSocketFrameHandler extends
        SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                TextWebSocketFrame msg) throws Exception { // (1)
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
			/*if (channel != incoming){
				channel.writeAndFlush(new TextWebSocketFrame(msg.text()));
			} else {
				channel.writeAndFlush(new TextWebSocketFrame("我发送的"+msg.text() ));
			}*/
            channel.writeAndFlush(new TextWebSocketFrame(msg.text()));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel incoming = ctx.channel();

        // Broadcast a message to multiple Channels
        channels.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 加入"));

        channels.add(incoming);
        log.info("client:{}已加入!!", incoming.remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel incoming = ctx.channel();
        // Broadcast a message to multiple Channels
        channels.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 离开"));
        log.info("client:{} 已离开!!", incoming.remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
        log.info("client:{} 在线!!", incoming.remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
        log.info("client:{} 掉线", incoming.remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)    // (7)
            throws Exception {
        Channel incoming = ctx.channel();
        log.error("client:{}发生异常，异常原因：{}", incoming.remoteAddress(), cause.getMessage());
        // 当出现异常就关闭连接
        ctx.close();
    }

}
