import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.channel.socket.SocketChannel;

public class Server {

    public static void main(String[] args) throws Exception{

        EventLoopGroup pGroup = new NioEventLoopGroup(); //线程组：用来处理网络事件处理（接受客户端连接）
        EventLoopGroup cGroup = new NioEventLoopGroup(); //线程组：用来进行网络通讯读写

        //Bootstrap用来配置参数
        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(pGroup, cGroup)
                    .channel(NioServerSocketChannel.class) //注册服务端channel
                    /**
                     * BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
                     * 用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，将使用默认值50。
                     * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
                     * 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                     */
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //设置日志
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel sc) throws Exception {
                            //marshaliing的编解码操作,要传输对象，必须编解码
//                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
//                        sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
//                        //5s没有交互，就会关闭channel
//                        sc.pipeline().addLast(new ReadTimeoutHandler(5));
//                            sc.pipeline().addLast(new RpcServerHandler());   //服务端业务处理类
//                            sc.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            sc.pipeline().addLast(new RpcServerHandler());
//                            sc.pipeline().addLast(new TimeServerHandler());
                        }
                    });


            ChannelFuture cf = b.bind(6666).sync();

            cf.channel().closeFuture().sync();
        }finally {

            pGroup.shutdownGracefully();
            cGroup.shutdownGracefully();

        }

        Bootstrap bc = new Bootstrap(); // (1)
        bc.group(cGroup); // (2)
        bc.channel(NioSocketChannel.class); // (3)
        bc.option(ChannelOption.SO_KEEPALIVE, true); // (4)
        bc.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(new RPCClientHandler());
                ch.pipeline().addLast(new RpcServerHandler());
            }
        });

        // Start the client register to RC.
        ChannelFuture f = bc.connect("127.0.0.1", 8888).sync(); // (5)

        // Wait until the connection is closed.
        f.channel().closeFuture().sync();

    }
}
