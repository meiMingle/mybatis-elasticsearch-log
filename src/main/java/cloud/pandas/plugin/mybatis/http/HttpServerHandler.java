package cloud.pandas.plugin.mybatis.http;

import cloud.pandas.plugin.mybatis.constant.Holder;
import cloud.pandas.plugin.mybatis.entity.EDsl;
import cloud.pandas.plugin.mybatis.entity.SqlWrapper;
import cloud.pandas.plugin.mybatis.listener.ElasticsearchMessageListener;
import cloud.pandas.plugin.mybatis.listener.MybatisMessageListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final byte[] SUCCESS = new byte[]{123, 34, 115, 117, 99, 99, 101, 115, 115, 34, 58, 116, 114, 117, 101, 125};
    private final Pattern mybatis = Pattern.compile("/\\w+/mybatis/log");
    private final Pattern elasticsearch = Pattern.compile("/\\w+/elasticsearch/request");

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest)msg;
            this.process(req);
            FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(SUCCESS));
            response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            boolean keepAlive = HttpUtil.isKeepAlive(req);
            if (keepAlive) {
                if (!req.protocolVersion().isKeepAliveDefault()) {
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
            } else {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            }

            ChannelFuture f = ctx.write(response);
            if (!keepAlive) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private void process(FullHttpRequest req) throws Exception {
        URI uri = new URI(req.uri());
        String path = uri.getPath();
        if (this.mybatis.matcher(path).matches()) {
            String projectName = path.split("/")[1];
            ByteBuf content = req.content();
            SqlWrapper sqlWrapper = (SqlWrapper)this.objectMapper.readValue(content.toString(StandardCharsets.UTF_8), SqlWrapper.class);
            Optional.ofNullable((Project)Holder.projectMap.get(projectName))
                .ifPresent(
                    project -> ((MybatisMessageListener)project.getMessageBus().syncPublisher((Topic)Holder.mybatisMap.get(projectName))).changed(sqlWrapper)
                );
        }

        if (this.elasticsearch.matcher(path).matches()) {
            String projectName = path.split("/")[1];
            ByteBuf content = req.content();
            EDsl dsl = (EDsl)this.objectMapper.readValue(content.toString(StandardCharsets.UTF_8), EDsl.class);
            Optional.ofNullable((Project)Holder.projectMap.get(projectName))
                .ifPresent(
                    project -> ((ElasticsearchMessageListener)project.getMessageBus().syncPublisher((Topic)Holder.elasticsearchMap.get(projectName)))
                            .changed(dsl)
                );
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
