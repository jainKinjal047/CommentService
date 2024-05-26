package com.example.commentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;


@Data
@Document("LikeDislike")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LikeDislike {
    @Id
    private String id;
    private String commentId;
    private List<String> likes;
    private List<String> dislikes;

    public LikeDislike(String commentId, List<String> likes, List<String> dislikes) {
        this.commentId = commentId;
        this.likes = likes;
        this.dislikes = dislikes;
    }
}
