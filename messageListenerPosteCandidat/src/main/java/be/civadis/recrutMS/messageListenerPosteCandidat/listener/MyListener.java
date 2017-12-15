package be.civadis.recrutMS.messageListenerPosteCandidat.listener;


import be.civadis.recrutMS.messageListenerPosteCandidat.Application;
import be.civadis.recrutMS.messageListenerPosteCandidat.events.CandidatEventType;
import be.civadis.recrutMS.messageListenerPosteCandidat.events.PosteEventType;
import be.civadis.recrutMS.messageListenerPosteCandidat.model.Candidat;
import be.civadis.recrutMS.messageListenerPosteCandidat.model.Poste;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;


@Component
public class MyListener extends AbstractListener {

    //from config server
    @Value("${retryExchangeName}") String retryExchangeName;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //TODO : voir si intérêt de Hystrix sur le listener ?

    @RabbitListener(queues = Application.queueName)
    @HystrixCommand(fallbackMethod="fallbackMessage")
    public void processMessage(Object message) throws IOException {

        String eventType = super.getType(message);
        System.out.println("event received : " + eventType);

        //la config du binding permet d'envoyer certains events à la queue, selon leur routing key
        //mais on peut avoir différent types de message dans la queue, même si on a écarté ceux dont on ne voulait pas
        //selon type d'event reçu, on converti et on applique le traitement
        //tous dans la même queue pour s'assurer de l'ordre d'arrivée
        //TODO appel aux managers

        if (eventType.equals(PosteEventType.poste_created.toString())){
            Poste poste = super.getContent(message, Poste.class);
            System.out.println("Poste created: " + poste.getNr() + "-" + poste.getTitre());

        } else if (eventType.equals(PosteEventType.poste_updated.toString())){
            Poste poste = super.getContent(message, Poste.class);
            System.out.println("Poste updated : " + poste.getNr() + "-" + poste.getTitre());

        } else if (eventType.equals(PosteEventType.poste_deleted.toString())){
            System.out.println("Poste deleted : ");

        }

        if (eventType.equals(CandidatEventType.candidat_created.toString())){
            Candidat candi= super.getContent(message, Candidat.class);
            System.out.println("Candidat created: " + candi.getNom());
        } else if (eventType.equals(CandidatEventType.candidat_updated.toString())) {
            Candidat candi = super.getContent(message, Candidat.class);
            //TEST on simule une erreur lors de l'update du candidat, transfert en RetryQueue puis en Queue après 10 sec
            Random random = new Random();
            int min = 1;
            int max = 3;
            int nbr = random.nextInt(max - min + 1) + min;
            if (nbr <= 2) {
                System.out.println("ERROR : Simulate error while updating Candidat, retry !");
                throw new RuntimeException("Exception while updating Candidat");
                //this.retryMessage(candi, eventType);
            } else {
                System.out.println("UPDATED : Candidat updated: " + candi.getNom() + ", " + candi.getPrenom());
            }
        } else if (eventType.equals(CandidatEventType.candidat_deleted.toString())) {
            //System.out.println("ERROR : Simulate error while deleting Candidat, retry !");
            //throw new RuntimeException("Exception while deleting Candidat");
        }

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