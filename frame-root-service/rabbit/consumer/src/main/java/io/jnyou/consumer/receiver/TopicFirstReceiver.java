package io.jnyou.consumer.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 代码千万行，注释第一行，
 * 注释不规范，同事泪两行。
 * <p>
 * 主题消息订阅
 *
 * @author JnYou
 * @version 1.0.0
 */
@Slf4j
@Component
@RabbitListener(queues = "note.first")
public class TopicFirstReceiver {

    @RabbitHandler
    public void process(Map<String, Object> directMsg) {
        log.info("receiver 【topic 1】 message");
        log.info("【topic 1】 type:{}", directMsg.get("type"));
        log.info("【topic 1】 msgId:{}", directMsg.get("msgId"));
        log.info("【topic 1】 msgData:{}", directMsg.get("msgData"));
        log.info("【topic 1】 createTime:{}", directMsg.get("createTime"));
    }

}
