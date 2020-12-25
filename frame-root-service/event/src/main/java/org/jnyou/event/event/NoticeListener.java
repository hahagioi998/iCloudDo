package org.jnyou.event.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 代码全万行，注释第一行
 * 注释不规范，同事泪两行
 * <p>
 * 消息通知监听
 *
 * @author Jnyou
 * @version 1.0.0
 */
@Component
public class NoticeListener implements ApplicationListener<NoticeEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NoticeListener.class);

    /**
     * 注解 @Async 表明改方法使用异步执行
     */
    @Async
    @Override
    public void onApplicationEvent(NoticeEvent noticeEvent) {
        logger.info("listener get event, sleep 2 second...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("event message is : {}", noticeEvent.getMessage());
    }
}
