package net.inkuk.simple_article.controller;

import net.inkuk.simple_article.authorization.JwtUtil;
import net.inkuk.simple_article.authorization.SecurityUser;
import net.inkuk.simple_article.authorization.UserDetailsServiceImpl;
import net.inkuk.simple_article.util.Log;
import net.inkuk.simple_article.util.MultipartFileToFile;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
public class ImageController {

    @PostMapping("/image")
    public ResponseEntity<?> postImage(@RequestParam("file") MultipartFile multipartFile, @RequestParam("type") String type){

        String path = MultipartFileToFile.save(type, multipartFile);

        if(path == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);


        final Map<String, Object> body = Map.of("path", path);

        return new ResponseEntity<>(body, HttpStatus.OK);
    }


    private boolean multipartFileToFile(MultipartFile multipartFile) {

        try {

            File file = new File("D:\\aaa.jpg");
            multipartFile.transferTo(file);
            return true;
        }
        catch (IOException e) {
            Log.error(e.toString());
            return false;
        }
    }
}