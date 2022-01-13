package com.example.databasinterface.view;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.util.Optional;


/**
 * Represents the different dialog panes for the view when selecting
 * options from the menu provided by the main view (BooksPane)
 */
public class DialogView {

    /**
     * Initiate the dialog window for adding a book to the database
     */
    public static void initAddBookDialog() {

        //Create custom dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Book");
        dialog.setHeaderText("Add a book to the database by filling out ISBN, Title and Publisher then press Next");
        dialog.setResizable(true);

        //Add labels
        Label isbnLabel = new Label("ISBN: ");
        Label titleLabel = new Label("Title: ");
        Label publisherLabel = new Label("Publisher: ");

        //Add text fields
        TextField isbnText = new TextField();
        TextField titleText = new TextField();
        TextField publisherText = new TextField();

        //Add labels and text fields to grid
        GridPane gridPane = new GridPane();
        gridPane.add(isbnLabel, 1, 1);
        gridPane.add(isbnText, 2, 1);
        gridPane.add(titleLabel, 1, 2);
        gridPane.add(titleText, 2, 2);
        gridPane.add(publisherLabel, 1, 3);
        gridPane.add(publisherText, 2, 3);
        dialog.getDialogPane().setContent(gridPane);

        ButtonType buttonNext = new ButtonType("Next", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonNext, buttonCancel);


        final Button buttonTypeNext = (Button) dialog.getDialogPane().lookupButton(buttonNext);
        buttonTypeNext.setDisable(true);

        isbnText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (Controller.isValidIsbn(t1)) {
                    buttonTypeNext.setDisable(false);
                }
                else {
                    buttonTypeNext.setDisable(true);
                }
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonNext) {
            Controller.onAddBookSelected(isbnText.getText(), titleText.getText(), publisherText.getText());
        }
    }


    /**
     * Initiate the dialog window to add author / authors to a book using text fields
     * @return a string array of the result from the dialog text fields
     */
    public static String[] initAddAuthorDialog() {

        //Create custom dialog
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Add Author");
        dialog.setHeaderText("Assign author (Name and Date of Birth) to the previous book");
        dialog.setResizable(true);

        Label authorLabel = new Label("Author: ");
        Label authorDobLabel = new Label("DOB: ");

        TextField authorText = new TextField();
        TextField authorDobText = new TextField();
        authorDobText.setPromptText("YYYY-MM-DD");

        GridPane gridPane = new GridPane();
        gridPane.add(authorLabel, 1, 1);
        gridPane.add(authorText, 2, 1);
        gridPane.add(authorDobLabel, 1, 2);
        gridPane.add(authorDobText, 2, 2);

        dialog.getDialogPane().setContent(gridPane);

        ButtonType buttonNext = new ButtonType("Next", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonAddAuthor = new ButtonType("Add Author", ButtonBar.ButtonData.APPLY);
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonNext, buttonAddAuthor, buttonCancel);


        //Disable button if date is not in correct format
        final Button buttonTypeAddAuthor = (Button) dialog.getDialogPane().lookupButton(buttonAddAuthor);
        buttonTypeAddAuthor.setDisable(true);

        authorDobText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(Controller.isValidDate(t1)) {
                    buttonTypeAddAuthor.setDisable(false);
                }
                else {
                    buttonTypeAddAuthor.setDisable(true);
                }
            }
        });
        //Set result to correspond with text fields on button press
        dialog.setResultConverter(new Callback<ButtonType, String[]>() {
            @Override
            public String[] call(ButtonType button) {
                if (button == buttonAddAuthor) {
                    return new String[]{authorText.getText(), authorDobText.getText()};
                } else if (button == buttonNext) {
                    return new String[]{"next"};
                }
                return new String[]{"cancel"};
            }
        });
        dialog.showAndWait();

        return dialog.getResult();

    }

    /**
     * Initiate the dialog window for adding genre / genres to a book using a text field
     * @return a string of the result within the text field
     */
    public static String initAddGenreDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Genre");
        dialog.setHeaderText("Assign Genre to Book");
        dialog.setResizable(true);

        Label genreLabel = new Label("Genre: ");

        TextField genreText = new TextField();

        GridPane gridPane = new GridPane();
        gridPane.add(genreLabel, 1, 1);
        gridPane.add(genreText, 2, 1);
        dialog.getDialogPane().setContent(gridPane);

        ButtonType buttonDone = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonAddGenre = new ButtonType("Add Genre", ButtonBar.ButtonData.APPLY);
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonDone, buttonAddGenre, buttonCancel);

        dialog.setResultConverter(new Callback<ButtonType, String>() {
            @Override
            public String call(ButtonType button) {
                if(button == buttonAddGenre) {
                    return genreText.getText();
                }
                else if(button == buttonDone){
                    return "done";
                }
                return "cancel";
            }
        });
        dialog.showAndWait();

        return dialog.getResult();
    }

    /**
     * Initiate the dialog window to grade a book
     */
    public static void initAddGradeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Grade Book");
        dialog.setHeaderText("Assign a grade from 1-5 to the specified Book");
        dialog.setResizable(true);

        Label gradeLabel = new Label("Grade: ");
        Label isbnLabel = new Label("ISBN: ");

        TextField gradeText = new TextField();
        gradeText.setPromptText("1-5");

        TextField isbnText = new TextField();
        isbnText.setPromptText("ISBN");

        GridPane gridPane = new GridPane();
        gridPane.add(gradeLabel, 1, 1);
        gridPane.add(gradeText, 2, 1);
        gridPane.add(isbnLabel, 1,2);
        gridPane.add(isbnText,2,2);

        dialog.getDialogPane().setContent(gridPane);

        ButtonType buttonDone = new ButtonType("Add Grade", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonDone, buttonCancel);

        final Button buttonTypeDone = (Button) dialog.getDialogPane().lookupButton(buttonDone);
        buttonTypeDone.setDisable(true);

        isbnText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(Controller.isValidIsbn(t1)) {
                    buttonTypeDone.setDisable(false);
                }
                else {
                    buttonTypeDone.setDisable(true);
                }
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonDone) {
            Controller.onGradeBook(isbnText.getText(), gradeText.getText());
        }
    }

    public static void initDeleteBook() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Delete Book");
        dialog.setHeaderText("Delete specified Book");
        dialog.setResizable(true);

        Label isbnLabel = new Label("ISBN: ");

        TextField isbnText = new TextField();
        isbnText.setPromptText("ISBN");

        GridPane gridPane = new GridPane();
        gridPane.add(isbnLabel, 1,2);
        gridPane.add(isbnText,2,2);
        dialog.getDialogPane().setContent(gridPane);

        ButtonType buttonDone = new ButtonType("Delete Book", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonDone);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonDone) {
            Controller.onDeleteBook(isbnText.getText());
        }
    }

    public static void initReviewBook() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Review Book");
        dialog.setHeaderText("Review specified book");
        dialog.setResizable(true);

        Label isbnLabel = new Label("ISBN: ");
        Label gradeLabel = new Label("Grade: ");
        Label commentLabel = new Label("Comment: ");

        TextField isbnText = new TextField();
        isbnText.setPromptText("ISBN");

        TextField gradeText = new TextField();
        gradeText.setPromptText("Grade");

        TextField commentText = new TextField();
        commentText.setPromptText("Comment");

        GridPane gridPane = new GridPane();
        gridPane.add(isbnLabel, 1,1);
        gridPane.add(isbnText,2,1);

        gridPane.add(gradeLabel, 1,2);
        gridPane.add(gradeText,2,2);

        gridPane.add(commentLabel, 1,3);
        gridPane.add(commentText,2,3,10,10);

        dialog.getDialogPane().setContent(gridPane);

        ButtonType buttonDone = new ButtonType("Grade Book", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonDone);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonDone) {
            Controller.onReviewBook(isbnText.getText(),commentText.getText(),gradeText.getText());
        }
    }

    public static boolean logIn() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Log In");
        dialog.setHeaderText("Log in");
        dialog.setResizable(true);

        Label userLabel = new Label("User Name: ");
        Label pwdLabel = new Label("Password: ");

        TextField userText = new TextField();
        userText.setPromptText("user name");

        TextField pwdText = new TextField();
        pwdText.setPromptText("*********");

        GridPane gridPane = new GridPane();
        gridPane.add(userLabel, 1,1);
        gridPane.add(userText,2,1);

        gridPane.add(pwdLabel, 1,2);
        gridPane.add(pwdText,2,2);

        dialog.getDialogPane().setContent(gridPane);

        ButtonType buttonDone = new ButtonType("Log In", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonDone);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonDone) {
            return Controller.onLogIn(userText.getText(), pwdText.getText());
        }
        return false;
    }

    public static void initCreateAccount() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Account");
        dialog.setHeaderText("Create Account");
        dialog.setResizable(true);

        Label firstNameLabel = new Label("firstName: ");
        Label lastNameLabel = new Label("lastName: ");
        Label emailLabel = new Label("email: ");
        Label userLabel = new Label("User Name: ");
        Label pwdLabel = new Label("Password: ");

        TextField firstNameText = new TextField();
        firstNameText.setPromptText("Firstname");
        TextField lastNameText = new TextField();
        lastNameText.setPromptText("Lastname");
        TextField emailText = new TextField();
        emailText.setPromptText("email");
        TextField userText = new TextField();
        userText.setPromptText("user name");
        TextField pwdText = new TextField();
        pwdText.setPromptText("*********");

        GridPane gridPane = new GridPane();
        gridPane.add(firstNameLabel,1,1);
        gridPane.add(firstNameText,2,1);
        gridPane.add(lastNameLabel,1,2);
        gridPane.add(lastNameText,2,2);
        gridPane.add(emailLabel,1,3);
        gridPane.add(emailText,2,3);
        gridPane.add(userLabel, 1,4);
        gridPane.add(userText,2,4);
        gridPane.add(pwdLabel, 1,5);
        gridPane.add(pwdText,2,5);

        dialog.getDialogPane().setContent(gridPane);

        ButtonType buttonDone = new ButtonType("Create Account", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonDone);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == buttonDone) {
            Controller.onCreateAccount(firstNameText.getText(),lastNameText.getText(),emailText.getText(),userText.getText(),pwdText.getText());
        }
        return;
    }
}

