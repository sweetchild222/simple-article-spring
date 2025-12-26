package net.inkuk.simple_article.controller;

import ch.qos.logback.core.joran.sanity.Pair;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import net.inkuk.simple_article.util.ImageResize;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.MultipartFileToFile;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ch.qos.logback.core.joran.sanity.Pair;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
import java.awt.Dimension;

@RestController
public class FileController {

    private static final String profilePath = "file\\profile";

    @PostMapping("/file/profile")
    public ResponseEntity<?> postProfile(@RequestParam("image") MultipartFile multipartFile, @RequestParam("image-format") String format){

        if(multipartFile.getSize() > (1000 * 1000 * 10))
            return new ResponseEntity<>(HttpStatus.CONTENT_TOO_LARGE);

        try {

            final byte [] bytes = multipartFile.getBytes();

            final BufferedImage srcImage = ImageIO.read(new ByteArrayInputStream(bytes));

            if(srcImage == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            int orientation = getOrientation(new ByteArrayInputStream(bytes));

            final int[][] sizeList = {{500, 500}, {400, 400}, {300, 300}, {100, 100}, {50, 50}};
            //final int[][] sizeList = {{50, 50}, {100, 100}, {200, 200}, {300, 300}, {500, 500}};

            final BufferedImage [] newImages = ImageResize.resize(srcImage, orientation, sizeList);

            if(newImages == null)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            if(newImages.length != sizeList.length)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            final String id = imagesToFiles(newImages);

            if(id == null)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
        }
        catch (IOException e){

            Log.debug(e.toString());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    private String imagesToFiles(BufferedImage [] images) {

        String path = createFolder();

        if(path == null)
            return null;

        final String id = generateID();

        boolean isFirst = true;

        String representFileName = id + ".png";

        for(BufferedImage image : images) {

            final String fileName = isFirst ? representFileName : (id + "_" + image.getWidth() + "x" + image.getHeight() + ".png");

            isFirst = false;

            final String filePath = path + "\\" + fileName;

            try {

                boolean success = ImageIO.write(image, "PNG", new File(filePath));

                if(!success)
                    return null;
            }
            catch (IOException e){

                Log.debug(filePath);
                return null;
            }
        }

        return representFileName;
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