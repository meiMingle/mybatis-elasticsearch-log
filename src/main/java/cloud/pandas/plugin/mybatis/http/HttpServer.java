package cloud.pandas.plugin.mybatis.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.BindException;

public final class HttpServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void start() throws BindException {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        server.option(ChannelOption.SO_BACKLOG, 1024);
        ((ServerBootstrap)server.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)).childHandler(new HttpServerInitializer());
        server.bind(this.port).syncUninterruptibly();
    }

    public void stop() {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    public void restart(int port) throws BindException {
        this.stop();
        this.port = port;
        this.start();
    }
}
