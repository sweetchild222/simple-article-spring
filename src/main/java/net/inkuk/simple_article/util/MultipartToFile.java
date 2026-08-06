package net.inkuk.simple_article.util;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.io.File;


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

        final String fileName = id + ".webp";

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
