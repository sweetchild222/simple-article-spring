package net.inkuk.simple_article.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MultipartFileToFile {


    private static final String rootPath = "file";


    private static String generateFileName(){

        UUID uuid = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        return now.format(formatter) + "-" + uuid.toString();
    }


    private static String createFolder(String type){

        final String path = rootPath + "\\" + type;

        Path directoryPath = Paths.get(path);

        try {

            if(!Files.exists(directoryPath))
                Files.createDirectories(directoryPath);

            return directoryPath.toAbsolutePath().toString();

        }catch(IOException e) {

            System.out.println(e.toString());
            return null;
        }
    }


    public static String save(String type, MultipartFile multipartFile) {

        try {

            String path = createFolder(type);

            if(path == null)
                return null;

            String fileName = generateFileName();

            String fullPath = path + "\\" + fileName;

            Log.debug(fullPath);

            File file = new File(fullPath);

            Log.debug(file.getAbsolutePath());

            multipartFile.transferTo(file);

            return fileName;

        }
        catch (IOException e) {

            Log.error(e.toString());

            return null;
        }
    }
}
