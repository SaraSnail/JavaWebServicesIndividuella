package com.example.javawebservicesindividuella.repositories;

import com.example.javawebservicesindividuella.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
