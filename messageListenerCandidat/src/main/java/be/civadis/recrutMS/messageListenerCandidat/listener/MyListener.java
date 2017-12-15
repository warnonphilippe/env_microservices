package be.civadis.recrutMS.messageListenerCandidat.listener;

import be.civadis.recrutMS.messageListenerCandidat.Application;
import be.civadis.recrutMS.messageListenerCandidat.events.CandidatEventType;
import be.civadis.recrutMS.messageListenerCandidat.model.Candidat;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MyListener extends AbstractListener {

    //from config server
    @Value("${retryExchangeName}") String retryExchangeName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = Application.queueName)
    @HystrixCommand(fallbackMethod="fallbackMessage")
    public void processMessage(Object message) throws Exception {

        String eventType = super.getType(message);
        System.out.println("event received : " + eventType);

        //throw new AmqpRejectAndDontRequeueException("Test transmission dans dead queue letter");

        //la config du binding permet d'envoyer certains events à la queue, selon leur routing key
        //mais on peut avoir différent types de message dans la queue, même si on a écarté ceux dont on ne voulait pas
        //selon type d'event reçu, on converti et on applique le traitement
        //tous dans la même queue pour s'assurer de l'ordre d'arrivée
        //TODO appel aux managers


        if (eventType.equals(CandidatEventType.candidat_created.toString())){
            Candidat candi= super.getContent(message, Candidat.class);
            System.out.println("Candidat created: " + candi.getNom());
        } else if (eventType.equals(CandidatEventType.candidat_created.toString())) {
            Candidat candi = super.getContent(message, Candidat.class);
            System.out.println("Candidat updated: " + candi.getNom());
        }

        //throw new Exception("Test reject Message");

        //TODO voir si on peut config le listener par annotation pour ne réagir qu'à certains type
        //tout en utilisant la meme queue sinon problème d'ordre et multiplication des queues

    }

    /**
     *
     * @param message objet du modele à transmettre ou objet de type Message contenant l'objet à transmettre
     * @param type
     */
    private void retryMessage(Object message, String type){
        //System.out.println("ERROR in FALLBACK !!!");
        //throw new RuntimeException("ERROR in FALLBACK !!!");
        //throw new AmqpRejectAndDontRequeueException("ERROR in FALLBACK, but no requeueing !");
        rabbitTemplate.convertAndSend(retryExchangeName, Application.queueName, message, m -> {
            m.getMessageProperties().setType(type);
            m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return m;
        });
    }

    private void fallbackMessage(Object message, Throwable t){
        String eventType = super.getType(message);
        System.out.println("FALLBACK on event : " + eventType);
        this.retryMessage(message, eventType);
        //si exception dans fallback, elle est transmise a rabbitMQ
    }

}