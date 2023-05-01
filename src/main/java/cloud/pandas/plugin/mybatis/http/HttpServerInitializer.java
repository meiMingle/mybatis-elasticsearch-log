package cloud.pandas.plugin.mybatis.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new ChannelHandler[]{new HttpServerCodec()});
        p.addLast(new ChannelHandler[]{new HttpObjectAggregator(65535)});
        p.addLast(new ChannelHandler[]{new HttpServerExpectContinueHandler()});
        p.addLast(new ChannelHandler[]{new HttpServerHandler()});
    }
}
