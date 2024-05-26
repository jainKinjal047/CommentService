package com.example.commentservice.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class LikeDislikeRequest {
    @NonNull
    String commentId;
    @NonNull
    String userId;
}
