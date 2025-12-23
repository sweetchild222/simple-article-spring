package net.inkuk.simple_article.interceptor;

import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.LogFile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ContextClosedEventTest {

    @EventListener
    public void event(ContextClosedEvent event) {

        DataBaseClientPool.closeAll();
        Log.close();
    }
}
