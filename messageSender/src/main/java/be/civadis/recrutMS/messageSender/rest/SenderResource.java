package be.civadis.recrutMS.messageSender.rest;

import be.civadis.recrutMS.messageSender.manager.SenderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SenderResource {

    @Autowired
    private SenderManager senderManager;

    @RequestMapping(method = RequestMethod.POST, value = "/sendMessages")
    public void send() throws Exception {
        this.senderManager.sendMessages();
    }

}
