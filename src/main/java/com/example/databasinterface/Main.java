package com.example.databasinterface;

import com.example.databasinterface.model.Book;
import com.example.databasinterface.model.DbException;
import com.example.databasinterface.model.DbImplementation;
import com.example.databasinterface.model.DbInterface;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.example.databasinterface.view.BooksPane;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        DbInterface dbImpl = new DbImplementation(); // model

        BooksPane root = new BooksPane(dbImpl);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                dbImpl.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}