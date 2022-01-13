package com.example.databasinterface.model;

import java.sql.Date;

/**
 * Representation of an author
 */
public class Author {

    private int authorId;
    private String name;
    private Date dob;
    private User user;


    public Author(int authorId, String name, User user, Date dob) {
        this.authorId=authorId;
        this.name = name;
        this.user = user;
        this.dob = dob;
    }

    public Author(String name, Date dob, User user) {
        this.name = name;
        this.user = user;
        this.dob = dob;
    }

    public int getAuthorId() {
        return authorId;
    }
    public Date getDob() { return dob; }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
