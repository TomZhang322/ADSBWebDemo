package adsbWebCrawler.util;

import com.beamOps.utility.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Created by zhaoke on 2017/6/17.
 */
@Component
public class MQUtil {

    private final JmsTemplate jmsTemplate;

    @Autowired
    private MQUtil(JmsTemplate jmsTemplate){
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * 发送消息
     * @param queue
     * @param msg
     * @param expiredTime
     */
    public void send(String queue,final String msg,long expiredTime){
        try{
            jmsTemplate.setTimeToLive(expiredTime);
            jmsTemplate.send(queue, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage tm = session.createTextMessage(msg);
                    return tm;
                }
            });
        }catch (Exception ex){
            Log.error(MQUtil.class,"send() error",ex);
        }
    }
}
