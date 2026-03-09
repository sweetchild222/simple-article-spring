package net.inkuk.simple_article.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import net.inkuk.simple_article.util.ImageResize;
import net.inkuk.simple_article.util.ImageSetWriter;
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
import java.nio.file.Path;
import java.util.Map;

public class MultipartToFile {

    private final String path;

    public MultipartToFile(String path){

        this.path = path;
    }


    public String write(MultipartFile multipartFile) {

        String path = createFolder();

        if(path == null)
            return null;

        final String id = generateID();

        final String fileName = id + ".png";

        final String filePath = path + "/" + fileName;

        try{

            File file = new File(filePath);
            multipartFile.transferTo(file);

        } catch (IOException e) {

            Log.error(e.toString());
            return null;
        }

        return fileName;
    }


    private String createFolder(){

        Path directoryPath = Paths.get(this.path);

        try {

            if(!Files.exists(directoryPath))
                Files.createDirectories(directoryPath);

            return directoryPath.toAbsolutePath().toString();

        }catch(IOException e) {

            Log.error(e.toString());
            return null;
        }
    }


    private String generateID(){

        UUID uuid = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        return now.format(formatter) + "-" + uuid;
    }
}
