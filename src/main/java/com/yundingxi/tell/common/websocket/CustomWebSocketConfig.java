package com.yundingxi.tell.common.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class CustomWebSocketConfig {

//    @Override
//    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//        messages
//                .nullDestMatcher().authenticated()
//                .simpSubscribeDestMatchers("/justyou/websocket/**").permitAll()
//                .simpDestMatchers("/app/**").hasRole("USER")
//                .simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole("USER")
//                .simpTypeMatchers(MESSAGE, SUBSCRIBE).denyAll();
//
//    }
//
//    /**
//     * 配置信息代理。定义消息代理，设置消息连接请求的各种规范信息。
//     * registry.enableSimpleBroker("/user","/topic") 表示客户端订阅地址的前缀信息，
//     * 也就是客户端接收服务端消息的地址的前缀信息（比较绕，看完整个例子，大概就能明白了）
//     * registry.setApplicationDestinationPrefixes("/app") 指服务端接收地址的前缀，
//     * 意思就是说客户端给服务端发消息的地址的前缀。
//     * registry.setUserDestinationPrefix("/user") 指推送用户前缀。
//     * 不难发现，setApplicationDestinationPrefixes 与 setUserDestinationPrefix 起到的效果刚好相反，
//     * 一个定义了客户端接收的地址前缀，一个定义了客户端发送地址的前缀。
//     * @param config 配置
//     */
//    @Override
//    public void configureMessageBroker(final MessageBrokerRegistry config) {
//        // These are endpoints the client can subscribes to.
//        //这个是客户端接收的端点前缀
//        config.enableSimpleBroker("/topic");
//        // Message received with one of those below destinationPrefixes will be automatically router to controllers @MessageMapping
//        //客户端向服务器端的端点前缀
//        config.setApplicationDestinationPrefixes("/app");
//    }
//
//    @Override
//    protected boolean sameOriginDisabled() {
//        return true;
//    }
//
//    /**
//     * 注册stomp端点。起到的作用就是添加一个服务端点，来接收客户端的连接，
//     * registry.addEndpoint("/tmax/ws")
//     * 表示添加了一个 /tmax/ws 端点，客户端可以通过这个端点来进行连接。
//     * withSockJS() 的作用是开启 SockJS 访问支持，即可通过http://IP:PORT/tmax/ws 来和服务端 websocket 连接。
//     * @param registry 注册器
//     */
//    @Override
//    public void registerStompEndpoints(final StompEndpointRegistry registry) {
//        // Handshake endpoint
//        registry.addEndpoint("stomp").withSockJS();
//    }


    /**
     * @return 这个@Bean注解在本地test时候可以注释掉，否则会因为冲突而报错，但是其他时候上线或者主启动类调试都要打开
     */
    @Bean(value = "serverEndpointExporter")
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}