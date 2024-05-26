package com.example.commentservice.repository;

import com.example.commentservice.entity.LikeDislike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LikeDislikeRepository extends MongoRepository<LikeDislike, String> {
    Optional<LikeDislike> findByCommentId(String commentId);
}
