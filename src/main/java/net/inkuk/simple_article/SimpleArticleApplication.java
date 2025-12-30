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

    //DataBaseClient client = new DataBaseClient();

	public static void main(String[] args) {

        SpringApplication.run(SimpleArticleApplication.class, args);

        Log.info("asdfsa");

        //appContext.getBean()

        //MyService myService = appContext.getBean(MyService.class);
        //myService.doSomething();
        //appContext.close(); // Close the context when done

        //SpringApplication app = new SpringApplication(SimpleArticleApplication.class);

        //app.run(args);





        //UserService userService = new UserService();

        //List<User> a = userService.findUser("asdf");
        //sSystem.out.println(a);



	}


    @Override
    public void run(ApplicationArguments args) throws Exception {


//        for (int i = 1; i < 9; i++) {
//
//            File file = new File("D:\\image\\" + String.valueOf(i) + ".jpg");
//
//            byte[] bytes = Files.readAllBytes(file.toPath());
//
//            int orientation = getOrientation(new ByteArrayInputStream(bytes));
//
//            Log.debug(orientation);
//
//            final BufferedImage srcImage = ImageIO.read(new ByteArrayInputStream(bytes));
//
//            final int[][] sizeList = {{500, 500}, {400, 400}, {300, 300}, {100, 100}, {50, 50}};
//
//            final BufferedImage[] newImages = ImageResize.resize(srcImage, orientation, sizeList);
//
//            if (newImages != null)
//                writeImages(newImages, orientation);
//
//        }
    }

    private static String generateID(){

        UUID uuid = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        return now.format(formatter) + "-" + uuid.toString();
    }



    public String writeImages(BufferedImage [] images, int orientation) {

        String path = "D:\\image";

        if(path == null)
            return null;

        final String id = "";

        boolean isFirst = true;

        String representFileName = String.valueOf(orientation) + "_" + id + ".png";

        for(BufferedImage image : images) {

            final String fileName = isFirst ? representFileName : (String.valueOf(orientation) + "_" + id + "_" + image.getWidth() + "x" + image.getHeight() + ".png");

            isFirst = false;

            final String filePath = path + "\\" + fileName;

            if(!writeImage(image, filePath))
                return null;
        }

        return representFileName;
    }


    private boolean writeImage(BufferedImage image, String filePath){

        try {

            return ImageIO.write(image, "PNG", new File(filePath));
        }
        catch (IOException e){

            Log.debug(filePath);
            return false;
        }
    }




    public int getOrientation(ByteArrayInputStream stream) {

        try {

            Metadata metadata = ImageMetadataReader.readMetadata(stream);
            Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if(directory == null)
                return 1; //== 0 degree

            return directory.getInt(ExifIFD0Directory. TAG_ORIENTATION);

        } catch (ImageProcessingException | IOException | MetadataException e) {

            Log.error(e.toString());
            return 1; // 0 degree
        }
    }






    @PostConstruct
    public void init() {

        //System.out.println("asdfsa");
    }


    @Scheduled(fixedRate = 1000)
    public void test() {


    }


}
