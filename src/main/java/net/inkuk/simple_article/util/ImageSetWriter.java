package net.inkuk.simple_article.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ImageSetWriter {

    private final String path;

    public ImageSetWriter(String path){

        this.path = path;
    }


    public String write(BufferedImage[] imageSet) {

        final String id = generateID();

        String path = createFolder(id);

        if(path == null)
            return null;

        for(BufferedImage image : imageSet) {

            final String fileName = image.getWidth() + "x" + image.getHeight() + ".webp";

            final String filePath = path + "/" + fileName;

            try {

                boolean success = ImageIO.write(image, "webp", new File(filePath));

                if(!success)
                    return null;
            }
            catch (IOException e){

                Log.error(filePath);
                return null;
            }
        }

        return id + ".webp";
    }


    private String createFolder(String id){

        Path directoryPath = Paths.get(this.path + "/" + id);

        try {

            if(!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
                return directoryPath.toAbsolutePath().toString();
            }

            return null;

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
