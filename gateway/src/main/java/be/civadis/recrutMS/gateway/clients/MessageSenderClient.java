package be.civadis.recrutMS.gateway.clients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("MESSAGESENDER") //DOIT ETRE EN UPPER-CASE !!!!
public interface MessageSenderClient {

    @RequestMapping(method = RequestMethod.POST, value = "/sendMessages")
    public void send() throws Exception;


}
