package com.moxtra.moxiechat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brad on 5/8/15.
 */
public class DummyData {

    public static List<User> USERS = new ArrayList<>();
    public static List<String> EMAILS = new ArrayList<>();

    static {
        USERS.add(new User("amy@example.com", "password", "Amy", "Tate", "FA01.png"));
        USERS.add(new User("bob@example.com", "password", "Bob", "Turner", "A01.png"));
        USERS.add(new User("ted@example.com", "password", "Ted", "Packman", "A02.png"));
        USERS.add(new User("cavin@example.com", "password", "Cavin", "Page", "A03.png"));
        USERS.add(new User("emily@example.com", "password", "Emily", "Pedsy", "FB01.png"));
        USERS.add(new User("cindy@example.com", "password", "Cindy", "Pettis", "FB02.png"));
        for (User user : USERS) {
            EMAILS.add(user.email);
        }
    }

    public static User findByEmail(String email) {
        for (User user : USERS) {
            if (user.email.equals(email)) {
                return user;
            }
        }
        return null;
    }

    public static List<User> getUserListForSelect(User user) {
        List<User> userList = new ArrayList<>();
        for (User u : USERS) {
            if (u.email.equals(user.email)) {
                continue;
            }
            userList.add(u);
        }
        return userList;
    }

}
