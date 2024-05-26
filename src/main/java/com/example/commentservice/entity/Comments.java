package com.example.commentservice.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
@Data
@Document(collection = "Comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comments {
    @Id
    private String id;
    private String postId;
    private String userId;
    private String content;
    private String parentCommentId;
    private int likesCount;
    private int dislikesCount;
    private LocalDateTime createdAt;
}
