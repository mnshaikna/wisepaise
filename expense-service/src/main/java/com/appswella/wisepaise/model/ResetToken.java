package com.appswella.wisepaise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "resetTokens")
public class ResetToken {

    @Id
    private String id;
    private String token;
    private String userId;
    private Date expiresAt;
}
