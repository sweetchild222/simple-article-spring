package net.inkuk.simple_article.controller;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import net.inkuk.simple_article.util.ImageResize;
import net.inkuk.simple_article.util.ImageSetWriter;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.MultipartToFile;
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
import java.util.Map;

@RestController
public class BlobController {

    private static final int[][] profileSupportSizeList = {{256, 256}, {128, 128}, {64, 64}, {32, 32}};
    private static final String profilePath = "/home/ubuntu/simple/blob/profile";
    private final ImageSetWriter profileSetWriter = new ImageSetWriter(profilePath);


    private static final int[][] articleThumbnailSupportSizeList = {{256, 256}, {128, 128}, {64, 64}, {32, 32}};
    private static final String articleThumbnailPath = "/home/ubuntu/simple/blob/article/thumbnail";
    private final ImageSetWriter articleThumbnailSetWriter = new ImageSetWriter(articleThumbnailPath);

    private static final String articlePath = "/home/ubuntu/simple/blob/article";
    private final MultipartToFile multipartToFile = new MultipartToFile(articlePath);

    @PostMapping("/blob/article")
    public ResponseEntity<?> postArticle(@RequestParam("image") MultipartFile multipartFile) {

        if(multipartFile.getSize() > (1000 * 1000 * 10))
            return new ResponseEntity<>(HttpStatus.CONTENT_TOO_LARGE);

        final String id = multipartToFile.write(multipartFile);

        if(id == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
    }

    @PostMapping("/blob/profile")
    public ResponseEntity<?> postProfile(@RequestParam("image") MultipartFile multipartFile){

        if(multipartFile.getSize() > (1000 * 1000 * 10))
            return new ResponseEntity<>(HttpStatus.CONTENT_TOO_LARGE);

        try {

            final byte [] bytes = multipartFile.getBytes();

            final BufferedImage srcImage = ImageIO.read(new ByteArrayInputStream(bytes));

            if(srcImage == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            int orientation = getOrientation(new ByteArrayInputStream(bytes));

            final String id = writeImageSet(srcImage, orientation, profileSupportSizeList, profileSetWriter);

            if(id == null)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
        }
        catch (IOException e){

            Log.error(e.toString());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    private String writeImageSet(BufferedImage srcImage, int orientation, int [][] supportSizeList, ImageSetWriter setWriter){

        final BufferedImage [] imageSet = ImageResize.resize(srcImage, orientation, supportSizeList);

        if(imageSet == null)
            return null;

        if(imageSet.length != supportSizeList.length)
            return null;

        return setWriter.write(imageSet);
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
            return 1; //== 0 degree
        }
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


    public boolean isSupportSize(String stringSize, int [][] SizeList){

        int [] sizeInt = parseIntSize(stringSize);

        if(sizeInt == null)
            return false;

        for(int [] size: SizeList){

            if(sizeInt[0] == size[0] && sizeInt[1] == size[1])
                return true;
        }

        return false;
    }


    @PostMapping("/blob/article/thumbnail")
    public ResponseEntity<?> postArticleThumbnail(@RequestParam("image") MultipartFile multipartFile){

        if(multipartFile.getSize() > (1000 * 1000 * 10))
            return new ResponseEntity<>(HttpStatus.CONTENT_TOO_LARGE);

        try {

            final byte [] bytes = multipartFile.getBytes();

            final BufferedImage srcImage = ImageIO.read(new ByteArrayInputStream(bytes));

            if(srcImage == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            int orientation = getOrientation(new ByteArrayInputStream(bytes));

            final String id = writeImageSet(srcImage, orientation, articleThumbnailSupportSizeList, articleThumbnailSetWriter);

            if(id == null)
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(Map.of("id", id), HttpStatus.OK);
        }
        catch (IOException e){

            Log.error(e.toString());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/blob/article/thumbnail/{id}")
    public ResponseEntity<?> getArticleThumbnail(@PathVariable String id, @RequestParam(required=false) String size) {

        String [] split = spiltFileName(id);

        if(split == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!(split.length == 2 && split[1].equals("png")))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(size == null) //set default size
            size = articleThumbnailSupportSizeList[0][0] + "x" + articleThumbnailSupportSizeList[0][1];

        if(!isSupportSize(size, articleThumbnailSupportSizeList))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String fileName = split[0] + "_" + size.toLowerCase() + "." + split[1];

        final String filePath = articleThumbnailPath + "/" + fileName;

        if(!(new File(filePath)).exists())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return createResponse(filePath);
    }


    @GetMapping("/blob/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable String id, @RequestParam(required=false) String size) {

        String [] split = spiltFileName(id);

        if(split == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(!(split.length == 2 && split[1].equals("png")))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(size == null) //set default size
            size = profileSupportSizeList[0][0] + "x" + profileSupportSizeList[0][1];

        if(!isSupportSize(size, profileSupportSizeList))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final String fileName = split[0] + "_" + size.toLowerCase() + "." + split[1];

        final String filePath = profilePath + "/" + fileName;

        if(!(new File(filePath)).exists())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return createResponse(filePath);
    }


    private ResponseEntity createResponse(String filePath) {

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        try {

            final UrlResource resource = new UrlResource("file:" + filePath);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (MalformedURLException e) {

            Log.error(e.toString());

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/blob/article/{id}")
    public ResponseEntity<?> getArticle(@PathVariable String id) {

        final String filePath = articlePath + "/" + id;

        if(!(new File(filePath)).exists())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return createResponse(filePath);
    }
}