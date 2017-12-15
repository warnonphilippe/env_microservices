package be.civadis.recrutMS.gateway.rest;

import be.civadis.recrutMS.gateway.clients.MessageSenderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SenderResource {

    @Autowired
    private MessageSenderClient messageSenderClient;

    @RequestMapping(method = RequestMethod.POST, value = "/sendMessages")
    public void send() throws Exception {
        this.messageSenderClient.send();
    }

}