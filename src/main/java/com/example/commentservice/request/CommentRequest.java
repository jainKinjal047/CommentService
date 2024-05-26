package com.example.commentservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {
    @NonNull
    String postId ;
    @NonNull
    String userId;
    @NonNull
    String content;
    String parentCommentId;
}
