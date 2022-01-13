package com.example.databasinterface.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a book.
 *
 * @author anderslm@kth.se
 */
public class Book {

    private String isbn; // should check format
    private String title;
    private String publisher;
    private ArrayList<Author> authors = new ArrayList<>();
    private ArrayList<String> genre = new ArrayList<>();
    private int grade;
    private User user;

    public Book(String isbn, String title, String publisher, List<Author> authors, List<String> genre, User user, int grade) {
        this.isbn = isbn;
        this.title = title;
        this.publisher = publisher;
        this.authors.addAll(authors);
        this.genre.addAll(genre);
        this.user = user;
        this.grade = grade;
    }

    public Book(String isbn, String title, String publisher, User user) {

        this.isbn = isbn;
        this.title = title;
        this.publisher = publisher;
        this.user = user;
        System.out.println(this.isbn + this.title + this.publisher + this.user);
    }

    public Book(String isbn, String title, String publisher, List<Author> authors, List<String> genre, int i) {
        System.out.println("jkajdlfa");
        this.isbn = isbn;
        this.title = title;
        this.publisher = publisher;
        //this.user = null;
        this.grade=i;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getPublisher() { return publisher; }
    public int getGrade() { return grade; }
    public ArrayList<Author> getAuthors() {
        return authors;
    }
    public ArrayList<String> getGenre() {
        return genre;
    }
    public String getUser() {   return user.getUsername(); }

    public void addAuthor(Author author) {
        authors.add(author);
    }
    public void addGenre(String genre) { this.genre.add(genre); }

    @Override
    public String toString() {
        return title + ", " + isbn + ", " + publisher + ", " + grade + ", " + authors.toString() + ", " + genre.toString();
    }
}
