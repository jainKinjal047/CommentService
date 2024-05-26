package com.example.commentservice.repository;


import com.example.commentservice.entity.Comments;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CommentRepository extends MongoRepository<Comments, String>, PagingAndSortingRepository<Comments,String> {
    List<Comments> findByPostIdAndParentCommentIdIsNull(String postId, Pageable pageable);
    List<Comments> findByParentCommentId(String parentCommentId,  Pageable pageable);
    Optional<Comments> findByIdAndUserId(String id, String userId);
}
