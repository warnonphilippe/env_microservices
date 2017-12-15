package be.civadis.recrutMS.messageSender;

import be.civadis.recrutMS.messageSender.manager.SenderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner{

    @Autowired
    private SenderManager senderManager;
    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        //this.senderManager.sendMessages();
        //context.close();
    }

}
