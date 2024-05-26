import com.example.commentservice.controller.CommentController;
import com.example.commentservice.entity.Comments;
import com.example.commentservice.request.CommentRequest;
import com.example.commentservice.request.LikeDislikeRequest;
import com.example.commentservice.request.UpdateCommentRequest;
import com.example.commentservice.service.CommentService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommentControllerTest {
    @InjectMocks
    CommentController commentController;

    @Mock
    CommentService commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testAddComment() {
        CommentRequest commentRequest = new CommentRequest("postId", "userId", "This is a comment",null);
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0 , LocalDateTime.now());
        when(commentService.addComment(any(CommentRequest.class))).thenReturn(comment);
        ResponseEntity<Comments> response = commentController.addComment(commentRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(comment, response.getBody());
    }

    @Test
    public void testLikeComment() {
        LikeDislikeRequest likeDislikeRequest = new LikeDislikeRequest("commentId", "userId");
        ResponseEntity<Void> response = commentController.likeComment(likeDislikeRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    public void testDislikeComment() {
        LikeDislikeRequest likeDislikeRequest = new LikeDislikeRequest("commentId", "userId");
        ResponseEntity<Void> response = commentController.dislikeComment(likeDislikeRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetComments() {
        String postId = "postId";
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0,LocalDateTime.now());
        List<Comments> comments = Arrays.asList(comment);
        when(commentService.getComments(postId, 0, 10)).thenReturn(comments);
        ResponseEntity<List<Comments>> response = commentController.getComments(postId, 0, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody());
    }

    @Test
    public void testGetReplies() {
        String commentId = "commentId";
        Comments reply = new Comments("id", "postId", "userId", "This is a reply", commentId, 0, 0,LocalDateTime.now());
        List<Comments> replies = Arrays.asList(reply);
        when(commentService.getReplies(commentId, 0, 10)).thenReturn(replies);
        ResponseEntity<List<Comments>> response = commentController.getReplies(commentId, 0, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(replies, response.getBody());
    }

    @Test
    public void testGetUsersWhoLikedComment() {
        String commentId = "commentId";
        List<String> users = Arrays.asList("userId1", "userId2");
        when(commentService.getUsersWhoLikedComment(commentId)).thenReturn(users);
        ResponseEntity<List<String>> response = commentController.getUsersWhoLikedComment(commentId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testGetUsersWhoDislikedComment() {
        String commentId = "commentId";
        List<String> users = Arrays.asList("userId1", "userId2");
        when(commentService.getUsersWhoDislikedComment(commentId)).thenReturn(users);
        ResponseEntity<List<String>> response = commentController.getUsersWhoDislikedComment(commentId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testUpdateComment() {
        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest("id", "userId", "Updated comment");
        Comments updatedComment = new Comments("id", "postId", "userId", "Updated comment", null, 0, 0,LocalDateTime.now());
        when(commentService.updateComment(updateCommentRequest)).thenReturn(Optional.of(updatedComment));
        ResponseEntity<Optional<Comments>> response = commentController.updateComment(updateCommentRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Optional.of(updatedComment), response.getBody());
    }

    @Test
    public void testUpdateComment_NotFound() {
        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest("id", "userId", "Updated comment");
        when(commentService.updateComment(updateCommentRequest)).thenReturn(Optional.empty());
        ResponseEntity<Optional<Comments>> response = commentController.updateComment(updateCommentRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
