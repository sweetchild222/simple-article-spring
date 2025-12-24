package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.util.ImageResize;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.MultipartFileToFile;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@RestController
public class FileController {

    private static final String profilePath = "file\\profile";

    @PostMapping("/file/profile")
    public ResponseEntity<?> postProfile(@RequestParam("image") MultipartFile multipartFile, @RequestParam("image-format") String format){

        if(multipartFile.getSize() > (1000 * 1000 * 10))
            return new ResponseEntity<>(HttpStatus.CONTENT_TOO_LARGE);

        String path = createFolder();
        if(path == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        String id = generateID() + "." + format;

        String filePath = path + "\\" + id;

        Log.debug(filePath);

        try {

            boolean success = cropImageToFile(ImageIO.read(multipartFile.getInputStream()), filePath, 500, 500);

            if(!success)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
        }
        catch (IOException e){

            Log.debug(e.toString());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    public static boolean cropImageToFile(BufferedImage image, String filePath, int width, int height) {

        try {
            if (image.getWidth() == width && image.getHeight() == height) {

                Log.debug("xxxxx");
                return ImageIO.write(image, "PNG", new File(filePath));
            }
            else {

                BufferedImage croppedImage = ImageResize.resizeAndCrop(image, width, height);

                return ImageIO.write(croppedImage, "PNG", new File(filePath));
            }
        } catch (IOException e) {

            Log.error(e.toString());

            return false;
        }
    }




    private static String createFolder(){

        //final String path = rootPath + "\\" + subPath;

        Path directoryPath = Paths.get(profilePath);

        try {

            if(!Files.exists(directoryPath))
                Files.createDirectories(directoryPath);

            return directoryPath.toAbsolutePath().toString();

        }catch(IOException e) {

            System.out.println(e.toString());
            return null;
        }
    }


    private static String generateID(){

        UUID uuid = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        return now.format(formatter) + "-" + uuid.toString();
    }


    @GetMapping("/file/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable String id) {

        final Path currentPath = Path.of("").toAbsolutePath();

        final String filePath = currentPath.toString() + "\\" + profilePath + "\\" + id;

        if(!(new File(filePath)).exists())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        final String urlPath = "file:" + filePath;

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        try {

            final UrlResource resource = new UrlResource(urlPath);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (MalformedURLException e) {

            Log.debug(e.toString());

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}