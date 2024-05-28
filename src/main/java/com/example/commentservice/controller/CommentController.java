package com.example.commentservice.controller;

import com.example.commentservice.entity.Comments;
import com.example.commentservice.request.CommentRequest;
import com.example.commentservice.request.LikeDislikeRequest;
import com.example.commentservice.request.UpdateCommentRequest;
import com.example.commentservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add/comments")
    public ResponseEntity<Comments> addComment(@RequestBody CommentRequest commentRequest) {
        Comments comment = commentService.addComment(commentRequest);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PostMapping("/add/like")
    public  ResponseEntity<Void> likeComment(@RequestBody LikeDislikeRequest likeDislikeRequest) {
        commentService.likeComment(likeDislikeRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add/dislike")
    public ResponseEntity<Void> dislikeComment(@RequestBody LikeDislikeRequest likeDislikeRequest) {
        commentService.dislikeComment(likeDislikeRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comments>> getComments(@PathVariable String postId,@RequestParam int page,
                                      @RequestParam int size) {
        List<Comments> comments = commentService.getComments(postId,page, size);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<List<Comments>> getReplies(@PathVariable String commentId,@RequestParam int page,
                                     @RequestParam int size) {
        List<Comments> comments = commentService.getReplies(commentId, page, size);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments/{commentId}/likes")
    public ResponseEntity<List<String>> getUsersWhoLikedComment(@PathVariable String commentId) {
        List<String> likes = commentService.getUsersWhoLikedComment(commentId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/comments/{commentId}/dislikes")
    public ResponseEntity<List<String>> getUsersWhoDislikedComment(@PathVariable String commentId) {
       List<String> dislikes = commentService.getUsersWhoDislikedComment(commentId);
       return new ResponseEntity<>(dislikes , HttpStatus.OK);
    }

    @PutMapping("/updateComment")
    public ResponseEntity<Optional<Comments>> updateComment(@RequestBody UpdateCommentRequest updateCommentRequest){
        Optional<Comments> updatedComment = commentService.updateComment(updateCommentRequest);
        if (updatedComment.isPresent()) {
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<Void> deleteComment(@RequestBody LikeDislikeRequest likeDislikeRequest){
        commentService.deleteComment(likeDislikeRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteReaction")
    public ResponseEntity<Void> deleteReaction(@RequestBody LikeDislikeRequest likeDislikeRequest){
        commentService.removeReaction(likeDislikeRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

