package com.example.javawebservicesindividuella.services;

import com.example.javawebservicesindividuella.entities.Post;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.security.Principal;
import java.util.List;

public interface PostService {

    List<Post>getAllPosts();
    Post getPostById(Long id);
    Post newPost(Post post, Jwt jwt);
    Post updatePost(Post post,Jwt jwt);
    String deletePost(Long id, Jwt jwt);
    String countPosts();
}
