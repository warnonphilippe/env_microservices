package be.civadis.recrutMS.messageListenerCandidat;

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

    //nom de la queue créer par l'application chaque application va créer sa queue
    //si plusieurs applications pour répliquer une même service, les réplicants seront sur la même queue
    public final static String queueName = "be.civadis.recrutMS.candidatListener.queue";
    //nom de la retryQueue
    public final static String retryQueueName = "be.civadis.recrutMS.candidatListener.retryQueue";

    @Bean
    public TopicExchange exchange() {
        System.out.println("Exchange : " + exchangeName);
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange retryExchange() {
        System.out.println("RetryExchange : " + retryExchangeName);
        return new TopicExchange(retryExchangeName);
    }

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
        //ici l'exchange enverra sur la queue les messages dont la routing key est keyCandidat ou le nom de la queue
        System.out.println("Bindings on : " + keyCandidat);
        return Arrays.asList(
                BindingBuilder.bind(queue()).to(exchange()).with(keyCandidat),
                BindingBuilder.bind(queue()).to(exchange()).with(queueName),
                BindingBuilder.bind(retryQueue()).to(retryExchange()).with(queueName)
                );
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
