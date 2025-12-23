package net.inkuk.simple_article.util;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LogFile {

    private BufferedWriter writer = null;
    private String currentFileName = null;

    private String path = null;
    
    public LogFile(String path) {

        this.path = path;

        Path directoryPath = Paths.get(path);

        try {
                Files.createDirectories(directoryPath);

        }catch(IOException e) {

            System.out.println(e.toString());
        }
    }


    private @NotNull String generateFileName(){

        LocalDateTime now = LocalDateTime.now();

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return now.format(formatter) + ".log";
    }


    private BufferedWriter createWriter(String fileName) {

        final String filePath = this.path + "\\" + fileName;

        File file = new File(filePath);


        try {
            if (!file.exists())
                if (!file.createNewFile())
                    return null;


            FileWriter fw = new FileWriter(file, true);

            return new BufferedWriter(fw);
        }
        catch (IOException e){

            System.out.println(e.toString());
            return null;
        }
    }


    public void write(String log) {

        String fileName = generateFileName();

        if(!fileName.equals(currentFileName)) {

            if(writer != null) {

                try{
                    writer.close();
                    writer = null;

                } catch (IOException e) {

                    System.out.println(e.toString());
                    return;
                }
            }

            writer = createWriter(fileName);

            if(writer != null)
                currentFileName = fileName;
        }

        if(writer == null)
            return;

        try {
            writer.write(log + "\n");
            writer.flush();
        }
        catch (IOException e){

            System.out.println(e.toString());
        }
    }


    public void close(){

        try {

            if(writer != null)
                writer.close();

            writer = null;
        }
        catch (IOException e){

            System.out.println(e.toString());
        }
    }
}
