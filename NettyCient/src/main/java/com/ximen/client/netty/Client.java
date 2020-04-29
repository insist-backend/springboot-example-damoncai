package com.ximen.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * @author zhishun.cai
 * @date 2020/4/14 15:35
 * @note
 */
public class Client  {
    public static Channel channel = null;
    public static void run(String... args) throws Exception {
        //1.创建线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //2.创建客户端的启动助手，完成相关配置
            Bootstrap b = new Bootstrap();
            b.group(group) // 3. 设置线程组
                    .channel(NioSocketChannel.class) // 4.设置客户端通道的实现类
                    .handler(new ChannelInitializer<SocketChannel>() { // 5. 创建通道初始化对象
                        @Override
                        public void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();
                            //往pipeline链中添加一个解码器
                            pipeline.addLast("decoder",new StringDecoder());
                            //往pipeline链中添加一个编码器
                            pipeline.addLast("encoder",new StringEncoder());
                            pipeline.addLast(new ChatClientHandler()); //9. 往Pipeline链中添加自定义的handler类
                        }
                    });
            System.out.println("..........Netty Chat Client启动..........");
            //7.启动客户端去连接服务器端(异步非阻塞)
            ChannelFuture cf = b.connect("127.0.0.1", 10001);
            cf.addListener(new ConnectionListener());
            Channel channel = cf.channel();
            System.out.println("--------" + channel.localAddress().toString().substring(1) + "--------");
//            while (true){
//                Thread.sleep(2000);
//                channel.writeAndFlush("msg" + "\r\n");
//            }
            //8.关闭连接(异步非阻塞)
//            cf.channel().closeFuture().sync();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
//            group.shutdownGracefully();
        }
    }
}
