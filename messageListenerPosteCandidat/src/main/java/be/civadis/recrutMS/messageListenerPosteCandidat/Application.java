package be.civadis.recrutMS.messageListenerPosteCandidat;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableEurekaClient
@EnableHystrix
public class Application {

    //from config server
    @Value("${exchangeName}") String exchangeName;
    @Value("${retryExchangeName}") String retryExchangeName;
    @Value("${keyCandidat}") String keyCandidat;
    @Value("${keyPoste}") String keyPoste;

    //nom de la queue créer par l'application chaque application va créer sa queue
    //si plusieurs applications pour répliquer une même service, les réplicants seront sur la même queue
    public final static String queueName = "be.civadis.recrutMS.posteCandidatListener.queue";

    //nom de la retryQueue de l'application, l'app pourra y envoyer les messages à rejouer après un délai d'attente
    public final static String retryQueueName = "be.civadis.recrutMS.posteCandidatListener.retryQueue";

    //exchange commun aux applications, la 1ere le déclare, les autres le récupère
    @Bean
    public TopicExchange exchange() {
        System.out.println("Exchange : " + exchangeName);
        return new TopicExchange(exchangeName);
    }

    //queue de l'application
    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    //exchange commun aux applications, permet de gérer les queues de retry
    @Bean
    public TopicExchange retryExchange() {
        System.out.println("RetryExchange : " + retryExchangeName);
        return new TopicExchange(retryExchangeName);
    }

    //retryQueue de l'application
    @Bean
    public Queue retryQueue() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", exchangeName);
        args.put("x-message-ttl", 10000);
        return new Queue(retryQueueName,true,false,false, args);
    }

    @Bean
    public List<Binding> bindings() {
        //on lie la queue de l'application à l'exchange et on spécifie les messages que l'on veut recevoir
        //ici l'exchange enverra sur la queue les messages dont la routing key keyCandidat ou keyPoste ou le nom de la queue
        //on lie le retryExchange à la retryQueue de l'application avec comme clé la clé de l'application (celle de sa queue principale)
        System.out.println("Bindings on : " + keyCandidat);
        System.out.println("Bindings on : " + keyPoste);
        return Arrays.asList(
                BindingBuilder.bind(queue()).to(exchange()).with(keyCandidat),
                BindingBuilder.bind(queue()).to(exchange()).with(keyPoste),
                BindingBuilder.bind(queue()).to(exchange()).with(queueName),
                BindingBuilder.bind(retryQueue()).to(retryExchange()).with(queueName));
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        //pour pouvoir envoyer des objects converti automatiquement en json
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args);
    }


}
