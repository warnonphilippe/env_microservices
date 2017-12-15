package be.civadis.recrutMS.messageSender.manager;

import be.civadis.recrutMS.messageSender.events.CandidatEventType;
import be.civadis.recrutMS.messageSender.events.PosteEventType;
import be.civadis.recrutMS.messageSender.model.Candidat;
import be.civadis.recrutMS.messageSender.model.Poste;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SenderManager {

    //from config server
    @Value("${exchangeName}") String exchangeName;
    @Value("${retryExchangeName}") String retryExchangeName;
    @Value("${keyCandidat}") String keyCandidat;
    @Value("${keyPoste}") String keyPoste;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessages() throws Exception {
        System.out.println("Sending message...");

        Poste poste = new Poste("1", "Poste 1");
        rabbitTemplate.convertAndSend(exchangeName, keyPoste, poste, m -> {
            m.getMessageProperties().setType(PosteEventType.poste_created.toString());
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return m;
        });


        Candidat candi = new Candidat("Toto", "gateway");
        rabbitTemplate.convertAndSend(exchangeName, keyCandidat, candi, m -> {
            m.getMessageProperties().setType(CandidatEventType.candidat_created.toString());
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return m;
        });

        Candidat candi2 = new Candidat("Test", "test");
        rabbitTemplate.convertAndSend(exchangeName, keyCandidat, candi2, m -> {
            m.getMessageProperties().setType(CandidatEventType.candidat_updated.toString());
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return m;
        });
/*
        for (int i =0; i++ < 1000;){
            Candidat c = new Candidat("Test", "toDelete");
            rabbitTemplate.convertAndSend(exchangeName, keyCandidat, c, m -> {
                m.getMessageProperties().setType(CandidatEventType.candidat_deleted.toString());
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        }
*/
        //rabbitTemplate.convertAndSend(Application.queueCandidat, "Hello Queue 2 !");
    }

}
