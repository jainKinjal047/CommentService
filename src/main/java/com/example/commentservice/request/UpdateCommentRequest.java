package com.example.commentservice.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateCommentRequest {
    @NonNull
    String commentId;
    @NonNull
    String userId;
    @NonNull
    String newContent;
}
