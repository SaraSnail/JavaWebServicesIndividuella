package com.example.javawebservicesindividuella.controllers;

import com.example.javawebservicesindividuella.entities.Post;
import com.example.javawebservicesindividuella.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    ///Shared endpoints
    //Hämtar alla blogginlägg
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        return new ResponseEntity<>(postService.getAllPosts(), HttpStatus.OK);
    }

    //Hämtar ett specifikt blogginlägg
    @GetMapping("/post/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
    }

    //Raderar ett blogginlägg
    @DeleteMapping("/deletepost/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id,Principal principal, Authentication authentication) {
        return new ResponseEntity<>(postService.deletePost(id,principal,authentication),HttpStatus.OK);
    }


    //TODO: använd jwt istället för princepal
    /*@GetMapping("/auth")
    public ResponseEntity<String>recive(@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(postService.getAuth(jwt.getClaim(JwtClaimNames.SUB)));
    }*/




    ///User endpoints
    //Skapar ett nytt blogginlägg
    @PostMapping("/newpost")
    public ResponseEntity<Post>newPost(@RequestBody Post post, Principal principal) {
        return new ResponseEntity<>(postService.newPost(post,principal), HttpStatus.CREATED);
    }

    //Uppdaterar ett blogginlägg
    @PutMapping("/updatepost")
    public ResponseEntity<Post> updatePost(@RequestBody Post post, Principal principal) {
        return new ResponseEntity<>(postService.updatePost(post,principal), HttpStatus.OK);
    }





    ///Admin endpoints
    //Hämtar information om antalet blogginlägg
    @GetMapping("/count")
    public ResponseEntity<String> getPostCount() {
        return new ResponseEntity<>(postService.countPosts(), HttpStatus.OK);
    }
}
