package com.yundingxi.tell.common.websocket;

import com.yundingxi.tell.bean.vo.LetterVo;
import com.yundingxi.tell.util.message.SendMailUtil;
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
@Api(value = "/websocket/openid/toOpenid", tags = "websocket服务端")
@ServerEndpoint(value = "/reply/{openid}/{reply}")
public class WebSocketServer {

    private Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static volatile int onlineCount = 0;
    /**
     * 关联用户的open id与他对应的websocket对象
     */
    public static volatile Map<String, WebSocketServer> data = new ConcurrentHashMap<>();

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

    /*** 回复的消息，包含要回复的信件id，回复者的笔名 */
    private List<String> replyList = new ArrayList<>(2);

    /**
     * 连接建立成功调用的方法
     **/
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("openid") String openid,
                       @PathParam("reply") String reply) {
        if (openid == null || reply == null) {
            return;
        }
        this.session = session;
        data.put(openid, this);
        addOnlineCount();
        String[] s = openid.split(" ");
        this.sender = s[0];
        this.recipient = s[1];
        String[] replySplit = reply.split(" ");
        replyList.add(replySplit[0]);
        replyList.add(replySplit[1]);
        log.info("sender = {}", sender);
        log.info("recipient = {}", this.recipient);

    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到来自窗口" + sender + "的信息:" + message + "，发送给：" + recipient);
        log.info("当前我是：" + this.sender + ",我对应的集合存储位置是：" + data.get(this.sender));
        LetterVo letterVo = LetterVo.builder()
                .letterId(replyList.get(0))
                .penName(replyList.get(1))
                .sender(sender)
                .recipient(recipient)
                .server(this)
                .message(message)
                .build();
        SendMailUtil.enMessageToQueue(letterVo);
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
