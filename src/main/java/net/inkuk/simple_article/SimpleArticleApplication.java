package net.inkuk.simple_article;

import jakarta.annotation.PostConstruct;
import net.inkuk.simple_article.util.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.awt.*;


@SpringBootApplication
@EnableScheduling
public class SimpleArticleApplication implements ApplicationRunner {

	public static void main(String[] args) {

        final String env = System.getenv("ENV");

        if(env != null && env.equals("DEV"))
            System.setProperty("server.servlet.context-path", "/api");

        SpringApplication.run(SimpleArticleApplication.class, args);

        Log.info("info is green");
        Log.error("error is red");
        Log.debug("debug is yellow");
	}


    @Override
    public void run(ApplicationArguments args) throws Exception {

    }


    @PostConstruct
    public void init() {

        System.out.println("PostConstruct");
    }

    @Scheduled(fixedRate = 1000000)
    public void test() {

    }
}
