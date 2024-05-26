import com.example.commentservice.entity.Comments;
import com.example.commentservice.entity.LikeDislike;
import com.example.commentservice.exception.CommentNotFoundException;
import com.example.commentservice.exception.CommentServiceException;
import com.example.commentservice.repository.CommentRepository;
import com.example.commentservice.repository.LikeDislikeRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeDislikeRepository likeDislikeRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testAddComment() {
        CommentRequest commentRequest = new CommentRequest("postId", "userId", "This is a comment", null);
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0,LocalDateTime.now());
        when(commentRepository.save(any(Comments.class))).thenReturn(comment);
        Comments result = commentService.addComment(commentRequest);
        assertNotNull(result);
        assertEquals(comment.getPostId(), result.getPostId());
        verify(commentRepository, times(1)).save(any(Comments.class));
    }

    @Test
    public void testAddCommentException() {
        CommentRequest commentRequest = new CommentRequest("postId", "userId", "content", null);
        when(commentRepository.save(any(Comments.class))).thenThrow(new RuntimeException());

        assertThrows(CommentServiceException.class, () -> commentService.addComment(commentRequest));
    }

    @Test
    public void testLikeComment() {
        LikeDislikeRequest likeDislikeRequest = new LikeDislikeRequest("commentId", "userId");
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0, LocalDateTime.now());
        LikeDislike likeDislike = new LikeDislike("commentId", new ArrayList<>(), new ArrayList<>());
        when(commentRepository.findById(eq(likeDislikeRequest.getCommentId()))).thenReturn(Optional.of(comment));
        when(likeDislikeRepository.findByCommentId(eq(likeDislikeRequest.getCommentId()))).thenReturn(Optional.of(likeDislike));
        commentService.likeComment(likeDislikeRequest);
        assertTrue(likeDislike.getLikes().contains(likeDislikeRequest.getUserId()));
        verify(likeDislikeRepository, times(1)).save(any(LikeDislike.class));
        verify(commentRepository, times(1)).save(any(Comments.class));
    }

    @Test
   public void testLikeCommentException() {
        LikeDislikeRequest likeDislikeRequest = new LikeDislikeRequest("commentId", "userId");
        when(commentRepository.findById(likeDislikeRequest.getCommentId())).thenThrow(new RuntimeException());
        assertThrows(CommentServiceException.class, () -> commentService.likeComment(likeDislikeRequest));
    }

    @Test
    public void testLikeCommentNotFoundException(){
        LikeDislikeRequest likeDislikeRequest = new LikeDislikeRequest("commentId", "userId");
        when(commentRepository.findById(likeDislikeRequest.getCommentId())).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.likeComment(likeDislikeRequest));
    }

    @Test
    public void testDislikeComment() {
        LikeDislikeRequest likeDislikeRequest = new LikeDislikeRequest("commentId", "userId");
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0,LocalDateTime.now());
        LikeDislike likeDislike = new LikeDislike("commentId", new ArrayList<>(), new ArrayList<>());
        when(commentRepository.findById(eq(likeDislikeRequest.getCommentId()))).thenReturn(Optional.of(comment));
        when(likeDislikeRepository.findByCommentId(eq(likeDislikeRequest.getCommentId()))).thenReturn(Optional.of(likeDislike));
        commentService.dislikeComment(likeDislikeRequest);
        assertTrue(likeDislike.getDislikes().contains(likeDislikeRequest.getUserId()));
        verify(likeDislikeRepository, times(1)).save(any(LikeDislike.class));
        verify(commentRepository, times(1)).save(any(Comments.class));
    }

    @Test
    public void testDislikeCommentException() {
        LikeDislikeRequest likeDislikeRequest = new LikeDislikeRequest("commentId", "userId");
        when(commentRepository.findById(likeDislikeRequest.getCommentId())).thenThrow(new RuntimeException());
        assertThrows(CommentServiceException.class, () -> commentService.dislikeComment(likeDislikeRequest));
    }

    @Test
    public void testDislikeCommentNotFoundException(){
        LikeDislikeRequest likeDislikeRequest = new LikeDislikeRequest("commentId", "userId");
        when(commentRepository.findById(likeDislikeRequest.getCommentId())).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.dislikeComment(likeDislikeRequest));
    }

    @Test
    public void testGetComments() {
        String postId = "postId";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0, LocalDateTime.now());
        List<Comments> comments = Collections.singletonList(comment);
        when(commentRepository.findByPostIdAndParentCommentIdIsNull(eq(postId), eq(pageable))).thenReturn(comments);
        List<Comments> result = commentService.getComments(postId, page, size);
        assertEquals(comments, result);
        verify(commentRepository, times(1)).findByPostIdAndParentCommentIdIsNull(eq(postId), eq(pageable));
    }

    @Test
    public void testGetCommentsException() {
        String postId = "postId";
        Pageable pageable = PageRequest.of(0, 10);
        when(commentRepository.findByPostIdAndParentCommentIdIsNull(postId, pageable)).thenThrow(new RuntimeException());

        assertThrows(CommentServiceException.class, () -> commentService.getComments(postId, 0, 10));
    }


    @Test
    public void testGetReplies() {
        String parentCommentId = "parentCommentId";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Comments reply = new Comments("id", "postId", "userId", "This is a reply", parentCommentId, 0, 0, LocalDateTime.now());
        List<Comments> replies = Collections.singletonList(reply);
        when(commentRepository.findByParentCommentId(eq(parentCommentId), eq(pageable))).thenReturn(replies);
        List<Comments> result = commentService.getReplies(parentCommentId, page, size);
        assertEquals(replies, result);
        verify(commentRepository, times(1)).findByParentCommentId(eq(parentCommentId), eq(pageable));
    }

    @Test
   public void testGetRepliesException() {
        String parentCommentId = "parentCommentId";
        Pageable pageable = PageRequest.of(0, 10);
        when(commentRepository.findByParentCommentId(parentCommentId, pageable)).thenThrow(new RuntimeException());

        assertThrows(CommentServiceException.class, () -> commentService.getReplies(parentCommentId, 0, 10));
    }

    @Test
    public void testGetUsersWhoLikedComment() {
        String commentId = "commentId";
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0, LocalDateTime.now());
        List<String> users = Arrays.asList("userId1", "userId2");
        LikeDislike likeDislike = new LikeDislike(commentId, users, new ArrayList<>());
        when(commentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(likeDislikeRepository.findByCommentId(eq(commentId))).thenReturn(Optional.of(likeDislike));
        List<String> result = commentService.getUsersWhoLikedComment(commentId);
        assertEquals(users, result);
        verify(commentRepository, times(1)).findById(eq(commentId));
        verify(likeDislikeRepository, times(1)).findByCommentId(eq(commentId));
    }

    @Test
    public void testGetUsersWhoLikedCommentWhenThereIsNoEntryInLikeDislike() {
        String commentId = "commentId";
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0, LocalDateTime.now());
        when(commentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(likeDislikeRepository.findByCommentId(eq(commentId))).thenReturn(Optional.empty());
        List<String> result = commentService.getUsersWhoLikedComment(commentId);
        assertEquals(0, result.size());
        verify(commentRepository, times(1)).findById(eq(commentId));
        verify(likeDislikeRepository, times(1)).findByCommentId(eq(commentId));
    }

    @Test
    public void testGetUsersWhoLikedCommentException() {
        String commentId = "commentId";
        when(commentRepository.findById(commentId)).thenThrow(new RuntimeException());
        assertThrows(CommentServiceException.class, () -> commentService.getUsersWhoLikedComment(commentId));
    }

    @Test
   public void testGetUsersWhoLikedCommentNotException() {
        String commentId = "commentId";
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.getUsersWhoLikedComment(commentId));
    }

    @Test
    public void testGetUsersWhoDislikedComment() {
        String commentId = "commentId";
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0, LocalDateTime.now());
        List<String> users = Arrays.asList("userId1", "userId2");
        LikeDislike likeDislike = new LikeDislike(commentId, new ArrayList<>(), users);
        when(commentRepository.findById(eq(commentId))).thenReturn(Optional.of(comment));
        when(likeDislikeRepository.findByCommentId(eq(commentId))).thenReturn(Optional.of(likeDislike));
        List<String> result = commentService.getUsersWhoDislikedComment(commentId);
        assertEquals(users, result);
        verify(commentRepository, times(1)).findById(eq(commentId));
        verify(likeDislikeRepository, times(1)).findByCommentId(eq(commentId));
    }
    @Test
    public void testGetUsersWhoDislikedCommentException() {
        String commentId = "commentId";
        when(commentRepository.findById(commentId)).thenThrow(new RuntimeException());
        assertThrows(CommentServiceException.class, () -> commentService.getUsersWhoDislikedComment(commentId));
    }

    @Test
    public void testGetUsersWhoDislikedCommentNotFoundException() {
        String commentId = "commentId";
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.getUsersWhoDislikedComment(commentId));
    }



    @Test
    public void testUpdateComment() {
        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest("id", "userId", "Updated comment");
        Comments comment = new Comments("id", "postId", "userId", "This is a comment", null, 0, 0, LocalDateTime.now());

        when(commentRepository.findByIdAndUserId(eq(updateCommentRequest.getCommentId()), eq(updateCommentRequest.getUserId())))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comments.class))).thenReturn(comment);

        Optional<Comments> result = commentService.updateComment(updateCommentRequest);

        assertTrue(result.isPresent());
        assertEquals("Updated comment", result.get().getContent());
        verify(commentRepository, times(1)).save(any(Comments.class));
    }

    @Test
    public void testUpdateCommentNotFound() {
        UpdateCommentRequest updateCommentRequest = new UpdateCommentRequest("id", "userId", "Updated comment");

        when(commentRepository.findByIdAndUserId(eq(updateCommentRequest.getCommentId()), eq(updateCommentRequest.getUserId())))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(CommentNotFoundException.class, () -> {
            commentService.updateComment(updateCommentRequest);
        });

        assertEquals("Comment not found", exception.getMessage());
    }

}
