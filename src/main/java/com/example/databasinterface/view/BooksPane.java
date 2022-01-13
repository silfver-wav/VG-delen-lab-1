package com.example.databasinterface.view;

import java.util.List;
import java.util.Optional;

import com.example.databasinterface.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;


/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;


    private MenuBar menuBar;


    public BooksPane(DbInterface dbImpl) {
        final Controller controller = new Controller(dbImpl, this);
        Controller.onConnect();
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg  the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus();

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)
        booksTable.setPlaceholder(new Label("No rows to display"));

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, String> publisherCol = new TableColumn<>("Publisher");
        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        TableColumn<Book, Author> authorCol = new TableColumn<>("Author/Authors");
        TableColumn<Book, Integer> gradeCol = new TableColumn<>("Grade");
        TableColumn<Book, User> userCol = new TableColumn<>("Added By");
        booksTable.getColumns().addAll(titleCol, isbnCol, publisherCol, genreCol, authorCol, gradeCol, userCol);
        // give all columns some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.3));
        isbnCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.1));
        publisherCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.1));
        genreCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.1));
        authorCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.1));
        userCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.1));

        // define how to fill data for each cell,
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("authors"));
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });

    }

    private void initMenus() {

        Menu fileMenu = new Menu("File");
        MenuItem logInItem = new MenuItem("Log in");
        MenuItem createAccountItem = new MenuItem("Create Account");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(logInItem, createAccountItem,exitItem);

        logInItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DialogView.logIn();
            }
        });

        createAccountItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DialogView.initCreateAccount();
            }
        });

        exitItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Controller.onDisconnect();
                System.exit(0);
            }
        });

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        MenuItem gradeBookItem = new MenuItem("Grade Book");
        MenuItem reviewBookItem = new MenuItem("Review Book");
        manageMenu.getItems().addAll(addItem, removeItem, updateItem, gradeBookItem, reviewBookItem);

        addItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (Controller.onLoggedIn()){ // Check if user is Logged in
                    DialogView.initAddBookDialog();
                } else {
                    if (DialogView.logIn()) { // Log in
                        DialogView.initAddBookDialog();
                    }
                }
            }
        });

        removeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (Controller.onLoggedIn()){ // Check if user is Logged in
                    DialogView.initDeleteBook();
                } else {
                    if (DialogView.logIn()) { // Log in
                        DialogView.initDeleteBook();
                    }
                }
            }
        });

        gradeBookItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DialogView.initAddGradeDialog();
            }
        });

        reviewBookItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (Controller.onLoggedIn()){
                    DialogView.initReviewBook();
                } else {
                    if (DialogView.logIn()) {
                        DialogView.initReviewBook();
                    }
                }
            }
        });

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, manageMenu);
    }
}
