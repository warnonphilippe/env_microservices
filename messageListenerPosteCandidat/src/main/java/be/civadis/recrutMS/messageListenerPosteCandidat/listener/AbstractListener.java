package be.civadis.recrutMS.messageListenerPosteCandidat.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by phw on 26/10/2017.
 */
public abstract class AbstractListener {

    /**
     * Extrait le content du message
     * @param message
     * @return
     */
    public String getContent(Object message){
        return new String(((Message) message).getBody(), StandardCharsets.UTF_8);
    }

    /**
     * Extrait le content du message et le converti en object de type T
     * @param message
     * @return
     */
    public <T> T getContent(Object message, Class<T> clazz) throws IOException {
        String content = this.getContent(message);
        return getModel(content, clazz);
    }

    /**
     * Extrait le type du message
     * @param message
     * @return
     */
    public String getType(Object message){
        return ((Message) message).getMessageProperties().getType();
    }

    /**
     * Convertit un content JSON en object
     * @param content
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T getModel(String content, Class<T> clazz) throws IOException {
        return new ObjectMapper().readValue(content, clazz);
    }

}
