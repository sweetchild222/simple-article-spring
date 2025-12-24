package net.inkuk.simple_article.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.MultipartFileToFile;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Map;

@RestController
public class FileController {

    private static final String profilePath = "file\\profile";

    @PostMapping("/file/profile")
    public ResponseEntity<?> postProfile(@RequestParam("image") MultipartFile multipartFile, @RequestParam("image-format") String format){

        if(multipartFile.getSize() > (1000 * 1000 * 10))
            return new ResponseEntity<>(HttpStatus.CONTENT_TOO_LARGE);

        final String id = MultipartFileToFile.save(profilePath, multipartFile, format);

        if(id == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        final Map<String, Object> body = Map.of("id", id);

        return new ResponseEntity<>(body, HttpStatus.OK);
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