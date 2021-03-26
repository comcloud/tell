package com.yundingxi.tell.common.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * websocket的配置类
 * 使用websocket的话基本需要三个类
 * 自定义拦截器：连接websocket服务器之后要做的一些拦截处理
 * 自定义处理器：对消息的各种处理
 * 主类就是配置类，用来把所有其他的内容添加到配置之中
 *
 * @author 成都犀牛
 * @date 2020年10月20日16:06:20
 */
@Configuration
public class CustomWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    /**
     * 注册stomp端点。起到的作用就是添加一个服务端点，来接收客户端的连接，
     * registry.addEndpoint("/tmax/ws")
     * 表示添加了一个 /tmax/ws 端点，客户端可以通过这个端点来进行连接。
     * withSockJS() 的作用是开启 SockJS 访问支持，即可通过http://IP:PORT/tmax/ws 来和服务端 websocket 连接。
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        // Handshake endpoint
        registry.addEndpoint("stomp").withSockJS();
    }


    @Bean(value = "serverEndpointExporter")
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}