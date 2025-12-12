package net.inkuk.simple_article.database;


import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service

public class ContextClosedEventTest {
    @EventListener
    public void event(ContextClosedEvent event) {

        DataBaseClientPool.closeAll();
    }
}
