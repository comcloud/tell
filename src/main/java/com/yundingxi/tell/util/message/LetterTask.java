package com.yundingxi.tell.util.message;

import com.yundingxi.tell.bean.entity.Letter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 推送信件的一个任务类，这个类封装类推送一封信的逻辑（只是实现推送一封信），实现Runnable类，外部使用线程池来控制信件的发送
 * @version v1.0
 * @ClassName LetterTask
 * @Author rayss
 * @Datetime 2021/3/24 8:57 上午
 */

public class LetterTask implements Runnable{

    private final List<Letter> letters;

    public LetterTask(List<Letter> letters){
        if(letters == null){
            throw new IllegalArgumentException("传入的信件集合不可以为null");
        }
        this.letters = letters;
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
        if(letters.size() == 0){
            return;
        }


    }


}
