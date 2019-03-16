package io.zixingly.rpcserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.zixingly.assis.RegisterMsg;

public class TestIOproviderHandler extends ChannelInboundHandlerAdapter{


    @Override
    public void channelRegistered(ChannelHandlerContext ctx){
        System.out.println("服务线程已注册！");

//        ctx.fireChannelRegistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务激活，发送数据！");

//        ChannelFuture f = ctx.channel().writeAndFlush(new RegisterMsg().setAddress(Constant.HOST).setPort(PORT));

//        super.channelActive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("when the writable changed!");
        super.channelWritabilityChanged(ctx);
    }
}
