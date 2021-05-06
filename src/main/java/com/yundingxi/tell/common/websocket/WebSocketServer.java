package com.yundingxi.tell.common.websocket;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每个websocket服务都是一个单独的聊天通道
 * 存放着自己的open id以及对方的open id
 * 每次收到消息时候都是获取对方的Open id对应的session然后发送message
 *
 * @author HP
 */
@SuppressWarnings("all")
@Slf4j
@Component
@EnableWebSocket
@EnableWebSocketMessageBroker
@Api(value = "/reply/openid", tags = "websocket服务端")
@ServerEndpoint(value = "/reply/{openid}")
public class WebSocketServer {

    private Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static volatile int onlineCount = 0;
    /**
     * 关联用户的open id与他对应的websocket对象
     */
    private static volatile Map<String, WebSocketServer> data = new ConcurrentHashMap<>();

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
//    public static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
     public Session session;

    /*** 接收openid */
    public String sender = "";

    /***接收者 */
    private String recipient = "";

    /**
     * 连接建立成功调用的方法
     **/
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("openid") String openid) {
        if (openid == null) {
            return;
        }
        this.session = session;
        data.put(openid, this);
        addOnlineCount();
        this.sender = openid;
        log.info("sender = {}", sender);

    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到来自窗口" + sender + "的信息:" + message + "，发送给：" + recipient);
        log.info("当前我是：" + this.sender + ",我对应的集合存储位置是：" + data.get(this.sender));
//        LetterVo letterVo = LetterVo.builder()
//                .letterId(replyList.get(0))
//                .penName(replyList.get(1))
//                .sender(sender)
//                .recipient(recipient)
//                .server(this)
//                .message(message)
//                .build();
//        SendMailUtil.enMessageToQueue(letterVo);
    }


    @OnClose
    public void onClose() {
        //从set中删除当前对象
        data.remove(this.sender);
        //在线数减1
        subOnlineCount();
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }


    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static WebSocketServer getServerByOpenId(String openId){
        return data.get(openId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketServer that = (WebSocketServer) o;
        return session.equals(that.session) && sender.equals(that.sender) && recipient.equals(that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, sender, recipient);
    }
}
