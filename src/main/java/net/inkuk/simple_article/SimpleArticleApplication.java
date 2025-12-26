package net.inkuk.simple_article;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import jakarta.annotation.PostConstruct;
import net.inkuk.simple_article.database.DataBaseClientPool;
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
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
        // Read image and EXIF metadata
        File inputFile = new File("D:\\abc.jpg");

        BufferedImage image = ImageIO.read(inputFile);

        //BufferedImage outImage =  rotateClockwise90(image);

        //File outputFile = new File("D:\\output90.jpg");

        //ImageIO.write(outImage, "jpg", outputFile);

        //BufferedImage outImage2 =  rotateClockwise180(image);

        BufferedImage outImage2 = rotateImage(image, 180);

        File outputFile2 = new File("D:\\output180.jpg");

        ImageIO.write(outImage2, "jpg", outputFile2);


        BufferedImage outImage3 = rotateImage(image, 270);

        File outputFile1 = new File("D:\\output270.jpg");

        ImageIO.write(outImage3, "jpg", outputFile1);


        BufferedImage outImage4 = rotateImage(image, 90);

        File outputFile4 = new File("D:\\output90.jpg");

        ImageIO.write(outImage4, "jpg", outputFile4);


        BufferedImage outImage5 = rotateImage(image, 0);

        File outputFile5 = new File("D:\\output0.jpg");

        ImageIO.write(outImage5, "jpg", outputFile5);



        //ImageIO.read(inputFile);


        //Metadata metadata = ImageMetadataReader.re(inputFile);

        //Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        //int a = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);

        //Log.debug(a);







    }


    public static BufferedImage rotateClockwise90(BufferedImage src) {

        final int width = src.getWidth();
        final int height = src.getHeight();

        BufferedImage rotated = new BufferedImage(height, width, src.getType());

        Graphics2D graphics2D = rotated.createGraphics();

        Log.debug((height - width) / 2);
        graphics2D.translate((height - width) / 2, (height - width) / 2);
        graphics2D.rotate(Math.toRadians(90), (double) height / 2, (double) width / 2);
        graphics2D.drawRenderedImage(src, null);

        return rotated;
    }


    public static BufferedImage rotateClockwise180(BufferedImage src) {

        final int width = src.getWidth();
        final int height = src.getHeight();

        BufferedImage rotated = new BufferedImage(width, height, src.getType());

        Graphics2D graphics2D = rotated.createGraphics();

        Log.debug((height - width) / 2);
        graphics2D.translate((height) / 2, -(height) / 2);
        graphics2D.rotate(Math.toRadians(180), 0, 0);
        graphics2D.drawRenderedImage(src, null);

        return rotated;
    }


    private BufferedImage rotateImage(BufferedImage srcImage, int radians) {

        BufferedImage newImage;

        if (radians == 90 || radians == 270)
            newImage = new BufferedImage(srcImage.getHeight(), srcImage.getWidth(), srcImage.getType());
        else if (radians == 180)
            newImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());
        else if(radians == 0)
            return srcImage;
        else
            return null;

        Graphics2D graphics = (Graphics2D) newImage.getGraphics();

        graphics.rotate(Math.toRadians(radians), (double)newImage.getWidth() / 2, (double)newImage.getHeight() / 2);
        graphics.translate((newImage.getWidth() - srcImage.getWidth()) / 2, (newImage.getHeight() - srcImage.getHeight()) / 2);
        graphics.drawRenderedImage(srcImage, null);

        return newImage;
    }





    @PostConstruct
    public void init() {

        //System.out.println("asdfsa");
    }


    @Scheduled(fixedRate = 1000)
    public void test() {


    }


}
