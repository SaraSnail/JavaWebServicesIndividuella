package com.example.javawebservicesindividuella.services;

import com.example.javawebservicesindividuella.entities.Post;
import com.example.javawebservicesindividuella.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAllPosts() {
        if(postRepository.findAll().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No posts found");
        }

        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        postExists(post,id);

        return post.get();
    }

    @Override
    public Post newPost(Post post, Principal principal) {
        //Lägg in felhantering för om något är null eller tomt
        validatePost(post);

        post.setAuthor(principal.getName());
        System.out.println("Author to new post: "+principal.getName());

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Post post, Principal principal) {
        Optional<Post> postOptional = postRepository.findById(post.getId());

        //Kollar om post man vill ändra ens finns
        postExists(postOptional, post.id);
        //Kollar om sökaren ens äger den existerade posten
        if(!postOptional.get().getAuthor().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You are not the author of the post and therefor not update this post");
        }

        validatePost(post);

        Post existingPost = postOptional.get();

        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());

        System.out.println("Post "+existingPost.getId()+" updated by author: "+principal.getName());

        return postRepository.save(existingPost);
    }

    @Override
    public String deletePost(Long id,Principal principal, Authentication authentication) {
        Optional<Post> postOptional = postRepository.findById(id);

        postExists(postOptional,id);



        List<String>roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth->auth.startsWith("ROLE_"))
                .collect(Collectors.toList());

        roles.replaceAll(role -> role.replaceAll("ROLE_",""));

        if(!postOptional.get().getAuthor().equals(principal.getName()) && !roles.contains("admin")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You are not an admin or the author of the post and therefor cannot delete this post");
        }

        postRepository.deleteById(id);
        return "Post with id "+id+" deleted by author: "+principal.getName();
    }

    @Override
    public String countPosts() {
        int totalPosts = postRepository.findAll().size();
        return "Total post count: "+totalPosts;
    }

    private void postExists(Optional<Post> post, Long id) {
        if(post.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No post with id "+id+" found");
        }
    }

    private void validatePost(Post post) {
        if(post.getTitle() == null || post.getTitle().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Title is required");
        }
        if(post.getContent() == null || post.getContent().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Content is required");
        }
        if(!(post.getContent().length() >= 3)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Content length should be 3 or more");
        }
        if(!(post.getTitle().length() >= 3)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Title length should be 3 or more");
        }

        //TODO: Ha någon hantering för om titlar är den samma? Kolla närmare på så du förstår
        if(postRepository.findAll().stream().anyMatch(post1 -> post1.getTitle().toLowerCase().startsWith(post.getTitle().toLowerCase()))){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Title already exists");
        }

    }

}
