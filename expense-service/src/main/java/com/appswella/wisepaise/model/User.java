package com.appswella.wisepaise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String userId;
    private String userGoogleId;
    private String userName;
    private String userEmail;
    private String userImageUrl;
    private String userCreatedOn;

    @JsonProperty("isRegistered")
    private boolean isRegistered;

    private String userPin;
    private List<String> userContacts;
}