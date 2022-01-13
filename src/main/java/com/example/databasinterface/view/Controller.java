package com.example.databasinterface.view;

import com.example.databasinterface.model.*;
import javafx.application.Platform;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private static BooksPane booksView; // view
    private static DbInterface ImplDb; // model


    private static User user; // Current user

    /**
     * Construct the controller from a database interface and user interface
     * @param ImplDb the database interface
     * @param booksView the user interface
     */
    public Controller(DbInterface ImplDb, BooksPane booksView) {
        this.ImplDb = ImplDb;
        this.booksView = booksView;
    }

    /**
     * Searches for a book in the database using a string in a database column specified
     * by the enumeration SearchMode
     * @param searchFor the string to search for
     * @param mode the column to search within
     */
    protected void onSearchSelected(String searchFor, SearchMode mode) {
        new Thread(()->{
            try {
                List<Book> result = null;
                if (searchFor != null && searchFor.length() > 0) {
                    switch (mode) {
                        case Title:
                            result = ImplDb.searchForBooks("title", searchFor);
                            break;
                        case ISBN:
                            result = ImplDb.searchForBooks("ISBN", searchFor);
                            break;
                        case Author:
                            result = ImplDb.searchForBooksByAuthor(searchFor);
                            break;
                        case Grade:
                            result = ImplDb.searchForGrade(searchFor);
                            break;
                        case Genre:
                            result = ImplDb.searchForGenre(searchFor);
                            break;
                    }

                    List<Book> finalResult = result;
                    Platform.runLater(() -> {
                        try {
                            if (finalResult.isEmpty()) {
                                booksView.showAlertAndWait("No results found.", INFORMATION);
                            } else {
                                booksView.displayBooks(finalResult);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        });
                } else {
                    Platform.runLater(() -> booksView.showAlertAndWait("Enter a search string!", WARNING));
                }
            } catch (Exception e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Database error.",ERROR));
            }
        }).start();
    }

    /**
     * Grade a book using ISBN and grade
     * @param ISBN the ISBN of the book to grade
     * @param grade the grade to assign to the book
     */
    protected static void onGradeBook(String ISBN, String grade) {
        new Thread(()-> {
                try {
                    ImplDb.gradeBook(Integer.parseInt(grade), ISBN);
                    Platform.runLater(() ->booksView.showAlertAndWait("The book has been graded", CONFIRMATION));
                } catch (DbException e) {
                    Platform.runLater(() -> booksView.showAlertAndWait("The book could not be graded", WARNING));
                }
        }).start();
    }

    /**
     * Add a book to the database using ISBN, title and publisher which later
     * prompts the user to specify authors and genre as well
     * @param ISBN the ISBN of the book
     * @param title the title of the book
     * @param publisher the publisher of the book
     */
    protected static void onAddBookSelected(String ISBN, String title, String publisher) {
        // Create Book object
        Book book = new Book(ISBN,title, publisher, user);

        // Add authors in Book object
        do {
            String[] rs = DialogView.initAddAuthorDialog();
            if (rs[0].equals("next")) {
                if (book.getAuthors().size()==0) {
                    booksView.showAlertAndWait("No Authors have been added", WARNING);
                } else {
                    break;
                }
            } else if(rs[0].equals("cancel")) {
                return;
            } else {
                book.addAuthor(new Author(rs[0], Date.valueOf(rs[1]), user));
            }
        } while (true);

        // Add authors in Book object
        do{
            String rs = DialogView.initAddGenreDialog();
            if (rs.equals("done")) {
                if (book.getGenre().size() == 0) {
                    booksView.showAlertAndWait("No genre have been added", WARNING);
                } else {
                    break;
                }
            } else if(rs.equals("cancel")) {
                return;
            } else {
                book.addGenre(rs);
            }
        }while(true);

        new Thread(()-> {
            try {
                ImplDb.addBookToDatabase(book);
                Platform.runLater(() ->booksView.showAlertAndWait("The book has been added", CONFIRMATION));
            } catch (DbException e) {
                Platform.runLater(() -> booksView.showAlertAndWait("The book could not be added", WARNING));
            }
        }).start();
    }

    /**
     * Disconnect from the database
     */
    protected static void onDisconnect() {
        try {
            ImplDb.disconnect();
        } catch (DbException e) {
            booksView.showAlertAndWait(
                    "Application was not able to disconnect from the database", WARNING);
        }

    }

    /**
     * Connect to the database
     */
    protected static void onConnect() {
        try {
            //ImplDb.connect("laboration_1", "anonymous_user", "1234");
            ImplDb.connect("laboration_1", "linus", "1234");

        }catch (DbException e) {
            e.printStackTrace();
            booksView.showAlertAndWait(
                    "Application was not able to connect to the database", WARNING);
        }

    }

    protected static boolean onLogIn(String user, String pwd){
        AtomicBoolean loggedIn = new AtomicBoolean(false);
        new Thread(()-> {
            try {
                if (ImplDb.logIn(user,pwd)) {
                    Controller.user = new User(user);
                    onDisconnect();
                    ImplDb.connect("laboration_1","linus", "1234");
                    loggedIn.set(true);
                }
            } catch (DbException e) {
                Platform.runLater(() -> booksView.showAlertAndWait("Wrong password or username", WARNING));
            }
        }).start();

        return loggedIn.get();
    }

    protected static boolean onLoggedIn() {
        if (user!=null) return true;
        else {
            booksView.showAlertAndWait(
                    "You need to log in first!", WARNING);
            return false;
        }
    }

    protected static void onReviewBook(String ISBN, String comment, String grade) {
        new Thread(()-> {
            try {
                ImplDb.reviewBook(ISBN,user.getUsername(),comment,Integer.parseInt(grade));
                Platform.runLater(() ->booksView.showAlertAndWait("The review has been made", CONFIRMATION));
            } catch (Exception e) {
                Platform.runLater(() ->booksView.showAlertAndWait(
                        "The review could not be made", WARNING));
            }
        }).start();

    }

    protected static void onDeleteBook(String ISBN) {
        new Thread(()-> {
                try {
                    ImplDb.deleteBook(ISBN);
                    Platform.runLater(() ->booksView.showAlertAndWait("The Book has been deleted", CONFIRMATION));
                }catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() ->booksView.showAlertAndWait(
                            "Book could not be deleted.\nAre you sure you typed in the right ISBN?", WARNING));
                }
        }).start();
    }

    /**
     * Check if a string has the correct format of a date
     * @param date the string to check
     * @return true if the string has a correct format, otherwise false
     */
    protected static boolean isValidDate(String date) {
        boolean check = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        try {
            if(date.length() == 10) {
                format.parse(date);
                check = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            check = false;
        }
        return check;
    }

    /**
     * Check if a string has the correct format of ISBN
     * @param isbn the string to check
     * @return true if the check has the correct format, otherwise false
     */
    protected static boolean isValidIsbn(String isbn) {
        String isbnPattern = "[0-9]{13}";
        isbn = isbn.replace("-","");
        isbn = isbn.replace(" ","");

        if(!isbn.matches(isbnPattern)) {
            return false;
        }
        else {
            return true;
        }
    }

    protected static void onCreateAccount(String firstName, String lastName, String email, String username, String pwd) {
        new Thread(()-> {
            try {
                ImplDb.createAccount(firstName, lastName, email, username, pwd);
                Platform.runLater(() ->booksView.showAlertAndWait("Account has been created", CONFIRMATION));
            }catch (Exception e) {
                Platform.runLater(() ->booksView.showAlertAndWait(
                        "The account could not be created", WARNING));
            }
        }).start();
    }

}
