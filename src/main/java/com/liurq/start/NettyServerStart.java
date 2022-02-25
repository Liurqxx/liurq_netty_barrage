package com.liurq.start;

import com.liurq.config.BarrageServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author liurq
 * @desc
 * @date 2022-02-24  22:20
 */
@Component
@Slf4j
public class NettyServerStart implements ApplicationRunner {


    private int port = 8080;

    public void startBarrageServer() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(2); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new BarrageServerInitializer())  //(4)
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            log.info("server start ....");
            // 绑定端口，开始接收进来的连接
            ChannelFuture f = b.bind(port).sync(); // (7)

            // 等待服务器  socket 关闭 。
            // 会阻塞，但你可以优雅地关闭你的服务器。
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("server error: {}", e.getMessage());
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

            log.info("server stop ....");
        }

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        this.startBarrageServer();
    }
}
