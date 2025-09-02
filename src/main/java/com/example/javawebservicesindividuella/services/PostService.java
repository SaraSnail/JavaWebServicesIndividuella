package com.example.javawebservicesindividuella.services;

import com.example.javawebservicesindividuella.entities.Post;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;

public interface PostService {

    List<Post>getAllPosts();
    Post getPostById(Long id);
    Post newPost(Post post, Principal principal);
    Post updatePost(Post post,Principal principal);
    String deletePost(Long id,Principal principal, Authentication authentication);
    String countPosts();
}
