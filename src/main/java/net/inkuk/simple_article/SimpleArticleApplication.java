package net.inkuk.simple_article;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import jakarta.annotation.PostConstruct;
import net.inkuk.simple_article.database.DataBaseClientPool;
import net.inkuk.simple_article.util.ImageResize;
import net.inkuk.simple_article.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.imageio.ImageIO;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableScheduling
public class SimpleArticleApplication implements ApplicationRunner {

	public static void main(String[] args) {

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
