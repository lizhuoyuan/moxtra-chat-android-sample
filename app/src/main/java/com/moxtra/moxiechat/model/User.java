package com.moxtra.moxiechat.model;

/**
 * Created by brad on 5/8/15.
 */
public class User {

    public String email;
    public String password;
    public String firstName;
    public String lastName;
    public String avatarPath;

    public User(String email, String password, String firstName, String lastName, String avatarPath) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarPath = avatarPath;
    }

}
