package com.example.javawebservicesindividuella.services;

import com.example.javawebservicesindividuella.converters.JwtAuthConverter;
import com.example.javawebservicesindividuella.entities.Post;
import com.example.javawebservicesindividuella.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final JwtAuthConverter jwtAuthConverter;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, JwtAuthConverter jwtAuthConverter) {
        this.postRepository = postRepository;
        this.jwtAuthConverter = jwtAuthConverter;
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
    public Post newPost(Post post, Jwt jwt) {
        validatePost(post);

        String userId = jwt.getClaim(JwtClaimNames.SUB);
        post.setAuthor(userId);
        System.out.println("Author to new post: "+userId);

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Post post, Jwt jwt) {
        Optional<Post> postOptional = postRepository.findById(post.getId());

        String userId = jwt.getClaim(JwtClaimNames.SUB);

        postExists(postOptional, post.id);

        if(!postOptional.get().getAuthor().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You are not the author of the post and therefor can not update this post");
        }

        validatePost(post);

        Post existingPost = postOptional.get();

        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());

        System.out.println("Post "+existingPost.getId()+" updated by author: "+userId);

        return postRepository.save(existingPost);
    }

    @Override
    public String deletePost(Long id, Jwt jwt) {
        Optional<Post> postOptional = postRepository.findById(id);

        postExists(postOptional,id);
        String user = jwt.getClaim(JwtClaimNames.SUB);

        AbstractAuthenticationToken token = jwtAuthConverter.convert(jwt);

        List<String>roles = token.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))
                .collect(Collectors.toList());

        roles.replaceAll(role -> role.replaceAll("ROLE_", ""));


        if(!postOptional.get().getAuthor().equals(user) && !roles.contains("admin")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"You are not an admin or the author of the post and therefor cannot delete this post");
        }

        postRepository.deleteById(id);
        return "Post with id "+id+" deleted by author: "+user;
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

        //TODO: Ha någon hantering för om titlar är den samma?
        if(postRepository.findAll().stream().anyMatch(post1 -> post1.getTitle().toLowerCase().startsWith(post.getTitle().toLowerCase()))){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Title already exists");
        }

    }

}
