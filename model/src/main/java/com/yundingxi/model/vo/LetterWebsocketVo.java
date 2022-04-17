package com.yundingxi.model.vo;

import com.yundingxi.tell.common.websocket.WebSocketServer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version v1.0
 * @ClassName LetterVo
 * @Author rayss
 * @Datetime 2021/3/26 12:26 下午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterWebsocketVo {
    private String sender;
    private String recipient;
    private String letterId;
    private String senderPenName;
    private String recipientPenName;
    private String message;
    private WebSocketServer server;
}
