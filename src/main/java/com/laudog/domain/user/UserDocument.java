package com.laudog.domain.user;


import com.laudog.domain.BaseDocument;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document
@SuperBuilder
public class UserDocument extends BaseDocument {

    public final static String USER_COLLECTION_NAME = "users";

    @Field
    private String username;
    @Field
    private String email;
    @Field
    private String password;
    @Field
    private String bio;
    @Field
    private String image;
    @Builder.Default()
    @Field
    private List<String> roles = new ArrayList<>();

    public UserDocument(String username, String email, String password, String bio, String image, List<String> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.image = image;
        this.roles = roles;
    }
}
