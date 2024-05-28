package com.example.commentservice.service;

import com.example.commentservice.constant.GlobalConstants;
import com.example.commentservice.entity.Comments;
import com.example.commentservice.entity.LikeDislike;
import com.example.commentservice.enums.ReactionType;
import com.example.commentservice.exception.CommentNotFoundException;
import com.example.commentservice.exception.CommentServiceException;
import com.example.commentservice.exception.ReactionNotFoundException;
import com.example.commentservice.exception.UnauthorizedException;
import com.example.commentservice.repository.CommentRepository;
import com.example.commentservice.repository.LikeDislikeRepository;
import com.example.commentservice.request.CommentRequest;
import com.example.commentservice.request.LikeDislikeRequest;
import com.example.commentservice.request.UpdateCommentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeDislikeRepository likeDislikeRepository;

    public Comments addComment(CommentRequest commentRequest) {
        try {
            Comments comment = new Comments();
            comment.setPostId(commentRequest.getPostId());
            comment.setUserId(commentRequest.getUserId());
            comment.setContent(commentRequest.getContent());
            comment.setCreatedAt(LocalDateTime.now());
            comment.setParentCommentId(commentRequest.getParentCommentId());
            return commentRepository.save(comment);
        } catch (Exception e) {
            throw new CommentServiceException("Failed to add comment", e);
        }
    }

    public void likeComment(LikeDislikeRequest likeDislikeRequest) {
        try {
            Optional<Comments> commentOptional = commentRepository.findById(likeDislikeRequest.getCommentId());
            if (commentOptional.isPresent()) {
                Comments comment = commentOptional.get();
                Optional<LikeDislike> optionalLikeDislike = likeDislikeRepository.findByCommentId(likeDislikeRequest.getCommentId());

                LikeDislike likeDislike = optionalLikeDislike.orElseGet(() -> new LikeDislike(likeDislikeRequest.getCommentId(), new ArrayList<>(), new ArrayList<>()));

                if (!likeDislike.getLikes().contains(likeDislikeRequest.getUserId())) {
                    likeDislike.getLikes().add(likeDislikeRequest.getUserId());
                    comment.setLikesCount(comment.getLikesCount() + 1);
                }
                if (likeDislike.getDislikes().contains(likeDislikeRequest.getUserId())) {
                    likeDislike.getDislikes().remove(likeDislikeRequest.getUserId());
                    comment.setDislikesCount(comment.getDislikesCount() - 1);
                }

                likeDislikeRepository.save(likeDislike);
                commentRepository.save(comment);
            } else {
                throw new CommentNotFoundException(GlobalConstants.COMMENT_NOT_FOUND);
            }
        } catch (CommentNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CommentServiceException("Failed to like comment", e);
        }
    }

    public void dislikeComment(LikeDislikeRequest likeDislikeRequest) {
        try {
            Optional<Comments> commentOptional = commentRepository.findById(likeDislikeRequest.getCommentId());
            if (commentOptional.isPresent()) {
                Comments comment = commentOptional.get();
                Optional<LikeDislike> optionalLikeDislike = likeDislikeRepository.findByCommentId(likeDislikeRequest.getCommentId());

                LikeDislike likeDislike = optionalLikeDislike.orElseGet(() -> new LikeDislike(likeDislikeRequest.getCommentId(), new ArrayList<>(), new ArrayList<>()));

                if (!likeDislike.getDislikes().contains(likeDislikeRequest.getUserId())) {
                    likeDislike.getDislikes().add(likeDislikeRequest.getUserId());
                    comment.setDislikesCount(comment.getDislikesCount() + 1);
                }
                if(likeDislike.getLikes().contains(likeDislikeRequest.getUserId())){
                    likeDislike.getLikes().remove(likeDislikeRequest.getUserId());
                    comment.setLikesCount(comment.getLikesCount()-1);
                }

                likeDislikeRepository.save(likeDislike);
                commentRepository.save(comment);
            } else {
                throw new CommentNotFoundException(GlobalConstants.COMMENT_NOT_FOUND);
            }
        }
        catch (CommentNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CommentServiceException("Failed to dislike comment", e);
        }
    }


    public List<Comments> getComments(String postId, int page , int size) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            return commentRepository.findByPostIdAndParentCommentIdIsNull(postId,pageable);
        } catch (Exception e) {
            throw new CommentServiceException("Failed to fetch comments", e);
        }
    }

    public List<Comments> getReplies(String parentCommentId , int page , int size) {
        Pageable pageable = PageRequest.of(page, size);
        try {
            return commentRepository.findByParentCommentId(parentCommentId, pageable);
        } catch (Exception e) {
            throw new CommentServiceException("Failed to fetch replies", e);
        }
    }

    public List<String> getUsersWhoLikedComment(String commentId) {
        try {
            Optional<Comments> commentOptional = commentRepository.findById(commentId);
            if (commentOptional.isPresent()) {
                return likeDislikeRepository.findByCommentId(commentId)
                        .map(LikeDislike::getLikes)
                        .orElse(new ArrayList<>());
            } else {
                throw new CommentNotFoundException(GlobalConstants.COMMENT_NOT_FOUND);
            }
        }
        catch (CommentNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new CommentServiceException("Failed to fetch users who liked the comment", e);
        }
    }

    public List<String> getUsersWhoDislikedComment(String commentId) {
        try {
            Optional<Comments> commentOptional = commentRepository.findById(commentId);
            if (commentOptional.isPresent()) {
                return likeDislikeRepository.findByCommentId(commentId)
                        .map(LikeDislike::getDislikes)
                        .orElse(new ArrayList<>());
            } else {
                throw new CommentNotFoundException(GlobalConstants.COMMENT_NOT_FOUND);
            }
        }
        catch (CommentNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new CommentServiceException("Failed to fetch users who disliked the comment", e);
        }
    }

    public Optional<Comments> updateComment(UpdateCommentRequest commentRequest) {
        try {
            Optional<Comments> commentOptional = commentRepository.findByIdAndUserId(commentRequest.getCommentId(), commentRequest.getUserId());
            if (commentOptional.isPresent()) {
                Comments comment = commentOptional.get();
                comment.setContent(commentRequest.getNewContent());
                commentRepository.save(comment);
                return Optional.of(comment);
            } else {
                throw new CommentNotFoundException(GlobalConstants.COMMENT_NOT_FOUND);
            }
        }
        catch (CommentNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw new CommentServiceException("Failed to update the comment", e);
        }
    }

    public void deleteComment(LikeDislikeRequest likeDislikeRequest) {
        Optional<Comments> commentOptional = commentRepository.findById(likeDislikeRequest.getCommentId());
        if (commentOptional.isPresent()) {
            Comments comment = commentOptional.get();
            if (comment.getUserId().equals(likeDislikeRequest.getUserId())) {
                commentRepository.delete(comment);
            } else {
                throw new UnauthorizedException("User is not authorized to delete this comment");
            }
        } else {
            throw new CommentNotFoundException("Comment not found");
        }
    }

    public void removeReaction(LikeDislikeRequest likeDislikeRequest) {
        Optional<Comments> commentOptional = commentRepository.findById(likeDislikeRequest.getCommentId());
        if(commentOptional.isPresent()) {
            Comments comment = commentOptional.get();
            Optional<LikeDislike> likeDislikeOptional = likeDislikeRepository.findByCommentId(likeDislikeRequest.getCommentId());
            if (likeDislikeOptional.isPresent()) {
                LikeDislike likeDislike = likeDislikeOptional.get();
                if (likeDislikeRequest.getReaction().equals(ReactionType.LIKE.getReaction()) && likeDislike.getLikes().contains(likeDislikeRequest.getUserId())) {
                    likeDislike.getLikes().remove(likeDislikeRequest.getUserId());
                    comment.setLikesCount(comment.getLikesCount()-1);
                } else if(likeDislikeRequest.getReaction().equals(ReactionType.DISLIKE.getReaction()) && likeDislike.getDislikes().contains(likeDislikeRequest.getUserId())){
                    likeDislike.getDislikes().remove(likeDislikeRequest.getUserId());
                    comment.setDislikesCount(comment.getDislikesCount()-1);
                }
                likeDislikeRepository.save(likeDislike);
                commentRepository.save(comment);
            }
            else {
                    throw new ReactionNotFoundException("User has not reacted on this comment");
                }
        } else {
            throw new CommentNotFoundException("Comment not found");
        }
    }
}


