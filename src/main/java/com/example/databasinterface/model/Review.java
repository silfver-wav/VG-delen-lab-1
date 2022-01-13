package com.example.databasinterface.model;

import java.sql.Date;

/**
 * Representation of a review
 */
public class Review {

    private User user;
    private Book book;

    private int reviewId;
    private Date date;
    private String comment;
    private int grade;

    public Review(User user, Book book, int reviewId, Date date, String comment, int grade) {
        this.user = user;
        this.book = book;
        this.reviewId = reviewId;
        this.date = date;
        this.comment = comment;
        this.grade=grade;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getISBN() {
        return book.getIsbn();
    }

    public Date getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return user + ", " + reviewId + ", " + book + ", " + date + ", " + comment;
    }
}
