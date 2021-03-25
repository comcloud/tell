package com.yundingxi.tell.common.websocket;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.util.SpringUtil;
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
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
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
@ServerEndpoint(value = "/reply/{openid}/{toOpenid}")
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

    /**
     * 连接建立成功调用的方法
     **/
    @OnOpen
    public void onOpen(Session session,
                                    @PathParam("openid") String openid,
                                    @PathParam("toOpenid") String toOpenid) {
        if(openid == null || toOpenid == null){
            return ;
        }
        this.session = session;
        data.put(openid, this);
        addOnlineCount();
        this.sender = openid;
        this.recipient = toOpenid;
        log.info("sender = {}", sender);
        log.info("recipient = {}", this.recipient);

    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到来自窗口" + sender + "的信息:" + message + "，发送给：" + recipient);
        log.info("当前我是：" + this.sender + ",我对应的集合存储位置是：" + data.get(this.sender));
        sendMessage(message);
    }

    private void sendMessage(String message) throws IOException {
        log.info("即将发送消息来自" + this.sender + "，发送给" + this.recipient);
        WebSocketServer socketServer = data.get(this.recipient);
        if (socketServer != null) {
            log.info("recipientServer.get().sender = {}", socketServer.sender);
            assert false;
            socketServer.session.getBasicRemote().sendText(message);
            log.info("已经发送");
            try {
                Db.use().insert(
                        Entity.create("chat_record")
                                .set("chat_record_content", message)
                                .set("send_open_id", this.sender)
                                .set("receive_open_id", this.recipient)
                                .set("send_time", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            //此时将信息暂时存放到redis
            log.info(MessageFormat.format("消息接收者{0}还未建立WebSocket连接，{1}发送的消息【{2}】将被存储到Redis的【{3}】列表中", recipient, sender, message, this.recipient));
            //存储消息到Redis中
            final ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("sender", this.sender);
            node.put("message", message);
            node.put("sendTime", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            final RedisUtil redisService = (RedisUtil) SpringUtil.getBean("redisUtil");
            redisService.set(this.recipient + "_unread_message", node.toString());
        }
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketServer that = (WebSocketServer) o;
        return session.equals(that.session) && sender.equals(that.sender) && recipient.equals(that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session, sender, recipient);
    }
}
