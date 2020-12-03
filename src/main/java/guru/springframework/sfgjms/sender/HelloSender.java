package guru.springframework.sfgjms.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate22;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        HelloWorldMessage message22 = HelloWorldMessage.builder()
                                                     .id(UUID.randomUUID())
                                                     .message("Hello world")
                                                     .build();

        jmsTemplate22.convertAndSend(JmsConfig.MY_QUEUE, message22);
        System.out.println("Message sent");
    }

    @Scheduled(fixedRate = 2000)
    public void sendandReceiveMessage() throws JMSException {
        HelloWorldMessage message22 = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("Hello world")
                .build();

    Message receiveMsg = jmsTemplate22.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                Message helloMessage = null;
                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message22));
                    helloMessage.setStringProperty("_type", "guru.springframework.sfgjms.model.HelloWorldMessage");
                    return helloMessage;
                } catch (JsonProcessingException e) {
                 throw new JMSException("boom");
                }
            }
        });

        System.out.println(receiveMsg.getBody(String.class));
    }
}