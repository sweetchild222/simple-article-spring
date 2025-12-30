package net.inkuk.simple_article.controller;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import net.inkuk.simple_article.util.ImageResize;
import net.inkuk.simple_article.util.Log;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

@RestController
public class FileController {

    private static final int[][] supportSizeList = {{500, 500}, {400, 400}, {300, 300}, {100, 100}, {50, 50}};

    private static final String profilePath = "file/profile";

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

            final BufferedImage [] newImages = ImageResize.resize(srcImage, orientation, supportSizeList);

            if(newImages == null)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            if(newImages.length != supportSizeList.length)
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

        for(BufferedImage image : images) {

            final String fileName = id + "_" + image.getWidth() + "x" + image.getHeight() + ".png";

            final String filePath = path + "/" + fileName;

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

        return id + ".png";
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

        return now.format(formatter) + "-" + uuid;
    }


    private String [] spiltFileName(String fileName){

        int index = fileName.lastIndexOf('.');

        if (index > 0 && index < fileName.length() - 1) {

            String name = fileName.substring(0, index);
            String extension = fileName.substring(index + 1);

            return new String[]{name, extension};

        } else
            return null;
    }


    private int [] parseIntSize(String size){

        String [] split = size.split("x");

        if(split.length == 2){

            try {

                int width = Integer.parseInt(split[0]);
                int height = Integer.parseInt(split[1]);

                return new int [] {width, height};

            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }


    public boolean isSupportSize(String stringSize){

        int [] sizeInt = parseIntSize(stringSize);

        if(sizeInt == null)
            return false;

        for(int [] size: supportSizeList){

            if(sizeInt[0] == size[0] && sizeInt[1] == size[1])
                return true;
        }

        return false;
    }


    @GetMapping("/file/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable String id, @RequestParam(required=false) String size) {

        String [] split = spiltFileName(id);

        if(split == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!(split.length == 2 && split[1].equals("png")))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(size == null) //set default size
            size = supportSizeList[0][0] + "x" + supportSizeList[0][1];

        if(!isSupportSize(size))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Path currentPath = Path.of("").toAbsolutePath();

        final String fileName = split[0] + "_" + size.toLowerCase() + "." + split[1];

        final String filePath = currentPath + "/" + profilePath + "/" + fileName;

        if(!(new File(filePath)).exists())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        try {

            final UrlResource resource = new UrlResource("file:" + filePath);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (MalformedURLException e) {

            Log.debug(e.toString());

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}