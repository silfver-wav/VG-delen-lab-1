package com.example.databasinterface.model;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 *
 * NB! The methods in the implementation must catch the SQL/MongoDBExceptions thrown
 * by the underlying driver, wrap in a BooksDbException and then re-throw the latter
 * exception. This way the interface is the same for both implementations, because the
 * exception type in the method signatures is the same. More info in DbException.java.
 *
 * @author anderslm@kth.se
 */
public interface DbInterface {

    /**
     * Connect to a database using username and password
     * @param database the name of the database
     * @param user the username
     * @param pwd the password
     * @throws DbException
     */

    public boolean connect(String database, String user, String pwd) throws DbException;

    /**
     * Disconnect from a database
     * @throws DbException
     */
    public void disconnect() throws DbException;

    /**
     * Makes it possible to search for grade
     * @param grade the string to search for
     * @return the result from the search
     * @throws DbException
     */
    public List<Book> searchForGrade(String grade) throws DbException;

    /**
     * Makes it possible to search for isbn, title and publisher
     * @param colName the column to search within
     * @param searchStr the string to search for
     * @return the result from the search
     * @throws DbException
     */
    public List<Book> searchForBooks(String colName, String searchStr) throws DbException;

    /**
     * Makes it possible to search for genre
     * @param genre the genre to search for
     * @return the result of the search
     * @throws DbException
     */
    public List<Book> searchForGenre(String genre) throws DbException;

    /**
     * Makes it possible to search for an author
     * @param name the name of the author
     * @return the result of the search
     * @throws DbException
     */
    public List<Book> searchForBooksByAuthor(String name) throws DbException;

    /**
     * Makes it possible to add a book to a database
     * @param book the book to add
     * @throws DbException
     */
    public void addBookToDatabase(Book book) throws DbException;

    public void deleteBook(String ISBN) throws DbException;

    public void reviewBook(String ISBN, String user, String comment, int grade) throws DbException;

    public User getUser(String user) throws DbException;

    /**
     * Makes it possible to add an author to a database
     * @param name the name of the author
     * @param dob the date of birth of the author
     * @return the id of the author
     * @throws DbException
     */
    //public int addAuthor(String name, Date dob) throws DbException;

    /**
     * Makes it possible to grade a book in a database
     * @param grade the grade of the book
     * @param ISBN the isbn of the book
     * @throws DbException
     */
    public void gradeBook(int grade, String ISBN) throws DbException;

    public void createAccount(String firstName, String lastName, String email, String username, String pwd) throws DbException;

    public boolean logIn(String username, String pwd) throws DbException;

}