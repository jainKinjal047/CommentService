package com.example.commentservice.enums;

import lombok.Getter;

@Getter
public enum ReactionType {
    LIKE("Like"),
    DISLIKE("Dislike");

    private String reaction;

     ReactionType(String reaction){
        this.reaction = reaction;
    }
}
