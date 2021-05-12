package com.yundingxi.tell.util.message;

import cn.hutool.core.bean.BeanUtil;
import com.yundingxi.tell.bean.dto.UnreadMessageDto;
import com.yundingxi.tell.bean.entity.Reply;
import com.yundingxi.tell.bean.vo.LetterWebsocketVo;
import com.yundingxi.tell.common.CenterThreadPool;
import com.yundingxi.tell.common.redis.RedisUtil;
import com.yundingxi.tell.common.websocket.WebSocketServer;
import com.yundingxi.tell.service.LetterService;
import com.yundingxi.tell.util.SpringUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version v1.0
 * @ClassName SendMailUtil
 * @Author rayss
 * @Datetime 2021/3/26 10:34 上午
 */

public class SendMailUtil {

    private static final Logger log = LoggerFactory.getLogger(SendMailUtil.class);
    /**
     * 每次的数据会暂时放入到这个队列存放，等具有来5封或者等待时间已经到来10_000ms采取弹出然后发送
     */
    @Getter
    private static final BlockingDeque<LetterWebsocketVo> WAIT_QUEUE = new LinkedBlockingDeque<>();

    /*** 信件发送的阈值*/
    private static final int LETTER_THRESHOLD = 5;

    @Getter
    private static final ReentrantLock LOCK = new ReentrantLock();

    @Getter
    private static final ThreadPoolExecutor POOL = CenterThreadPool.getWebsocketPool();

    /**
     * 何时会新建一个线程来发送邮件
     * 1.有五件信件没有发送
     * 2.等待了10_000ms没有到5封
     *
     * @param letterWebsocketVo 回复信件集合
     */
    public static void enMessageToQueue(LetterWebsocketVo letterWebsocketVo) {
        int waitQueueSize = WAIT_QUEUE.size();
        if (waitQueueSize >= LETTER_THRESHOLD) {
            LOCK.lock();
            try{
                List<LetterWebsocketVo> letterWebsocketVos = new ArrayList<>();
                for (int i = 0; i < LETTER_THRESHOLD; i++) {
                    letterWebsocketVos.add(WAIT_QUEUE.peek());
                }
                POOL.execute(new LetterTask(letterWebsocketVos));
                log.info("开启一个线程");
            }finally {
                LOCK.unlock();
            }
        }else{
            WAIT_QUEUE.push(letterWebsocketVo);
        }
    }

    /**
     * 目前作用就是用来回复信件
     * 推送信件的一个任务类，这个类封装类推送一封信的逻辑（只是实现推送一封信），实现Runnable类，外部使用线程池来控制信件的发送
     *
     * @version v1.0
     * @ClassName LetterTask
     * @Author rayss
     * @Datetime 2021/3/24 8:57 上午
     */
    public static class LetterTask implements Runnable {

        private final List<LetterWebsocketVo> letterWebsocketVoList;

        public LetterTask(LetterWebsocketVo letterWebsocketVo){
            this();
            this.letterWebsocketVoList.add(letterWebsocketVo);
        }
        LetterTask(List<LetterWebsocketVo> letterWebsocketVoList) {
            this();
            addAll(letterWebsocketVoList);
        }
        private LetterTask(){
            letterWebsocketVoList = new ArrayList<>(10);
        }
        /**
         * ------------send---------------
         * 每个用户不会把信件针对性的发送给谁，而是直接把信件放入mysql
         * 这里暂时不使用redis
         * -------------reply--------------
         * 发送信件主要逻辑就是说如何把信件发送出去，而且信件肯定还是要写入到mysql，但是还是需要存入到redis
         * 所以这里采用写入redis作为一个用户的未读消息，当然会设置一个过期时间
         * 需要提前保存数据到mysql作为一个持久化存储
         * 但是依旧需要思考一个问题，如何判断一个信件是否被读呢，现在用户读取信件有两种方式，一是从redis过来的数据，二是距离上次登陆时间
         * 太长所以redis中的数据已经过期导致需要从mysql进行读取
         * 倘若是从redis中读取，这时候需要告知一下mysql,然后进行一个状态更改，但是这样的话感觉有悖于我们性能的要求初衷
         * 所以采用凌晨统一状态更改，从redis获取信件时候，同时保存一个记号，这个记号记录着是哪个信件已读
         * 当然如果因为过期，那么会从mysql读取，则会直接修改信件状态
         */
        @Override
        public void run() {
            this.letterWebsocketVoList.forEach(this::sendMessageToSingle);
            this.letterWebsocketVoList.clear();
        }

        private void sendMessageToSingle(LetterWebsocketVo letterWebsocketVo) {
            log.info("即将发送消息来自" + letterWebsocketVo.getSender() + "，发送给" + letterWebsocketVo.getRecipient());
            WebSocketServer socketServer = letterWebsocketVo.getServer();
            if (socketServer != null) {
                log.info("recipientServer.get().sender = {}", socketServer.sender);
                try {
                    socketServer.session.getBasicRemote().sendText(letterWebsocketVo.getMessage());
                    log.info("已经发送");
                } catch (IOException e) {
                    e.printStackTrace();
                    log.info("发送失败");
                }
            } else {
                //此时将信息暂时存放到redis
                log.info(MessageFormat.format("消息接收者{0}还未建立WebSocket连接，{1}发送的消息【{2}】将被存储到Redis的【{3}】列表中", letterWebsocketVo.getRecipient(), letterWebsocketVo.getSender(), letterWebsocketVo.getMessage(), letterWebsocketVo.getRecipient()));
                //存储消息到Redis中
                UnreadMessageDto unreadMessageDto = BeanUtil.toBean(letterWebsocketVo,UnreadMessageDto.class);
                unreadMessageDto.setSenderTime(LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                final RedisUtil redisService = (RedisUtil) SpringUtil.getBean("redisUtil");
                Object o = redisService.get(letterWebsocketVo.getRecipient() + "_unread_message");
                if(o == null){
                    List<UnreadMessageDto> list = new ArrayList<>();
                    list.add(unreadMessageDto);
                    redisService.set(letterWebsocketVo.getRecipient() + "_unread_message", list);
                }else{
                    @SuppressWarnings("unchecked") List<UnreadMessageDto> messageDtoList = (List<UnreadMessageDto>) o;
                    messageDtoList.add(unreadMessageDto);
                    redisService.set(letterWebsocketVo.getRecipient() + "_unread_message", messageDtoList);
                }
            }
            LetterService letterService = (LetterService) SpringUtil.getBean("letterService");
            letterService.saveReplyFromSenderToRecipient(
                    new Reply(UUID.randomUUID().toString()
                            , letterWebsocketVo.getLetterId()
                            , new Date()
                            , letterWebsocketVo.getMessage()
                            , letterWebsocketVo.getSender(), letterWebsocketVo.getSender())
            );
        }
        private void addAll(List<LetterWebsocketVo> letterWebsocketVos){
            letterWebsocketVoList.addAll(letterWebsocketVos);
        }
    }
}
