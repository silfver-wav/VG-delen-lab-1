package com.example.databasinterface.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Representation of a database implementation
 */
public class DbImplementation implements DbInterface {

    @Override
    public User getUser(String user) throws DbException {
        User result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            pstmt = con.prepareStatement("SELECT * FROM User WHERE userName = ? ");

            // Set the parameter
            pstmt.setString(1, user);
            // Execute the SQL query
            rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("userName"));
                result = new User(rs.getString("email"), rs.getString("userName"));
            }
        } catch (Exception e) {
            throw new DbException("",e);
        } finally {
            try {
                if (rs!=null) rs.close();
                if (pstmt!=null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("",e);
            }
        }

        return result;
    }

    private Connection con = null;

    /**
     * Connect to a database with username and password for an already created user
     * @param database the name of the database
     * @param user the username
     * @param pwd the password
     * @throws DbException
     */
    @Override
    public boolean connect(String database, String user, String pwd) throws DbException {
        String server = "jdbc:mysql://localhost:3306/" + database + "?UseClientEnc=UTF8";
        boolean connected = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(server, user, pwd);
            connected = true;
        } catch (SQLException e) {
            throw new DbException("Could not connect to Database",e);
        } catch (ClassNotFoundException e) {
            throw new DbException("Could not find class",e);
        }
        return connected;
    }

    /**
     * Disconnect from the database
     * @throws DbException
     */
    @Override
    public void disconnect() throws DbException {
        try {
            if (con != null) {
                con.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            throw new DbException("Could not disconnect from database",e);
        }
    }

    public List<Book> searchForGrade(String grade) throws DbException {
        List<Book> result = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement("SELECT gradeforbooks.* FROM gradeforbooks WHERE " + grade + "= ?");
            // Set the parameters
            pstmt.setString(1,grade);
            // Execute the SQL query for pstmt
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String ISBN = rs.getString("ISBN");
                String title = rs.getString("title");
                String publisher = rs.getString("publisher");
                String userName = rs.getString("user");
                User user = new User(userName);
                List<String> genre = getGenre(ISBN);
                List<Author> authors = getAuthors(ISBN);

                Book book = new Book(ISBN, title, publisher, authors, genre, user, Integer.parseInt(grade));
                result.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DbException("", e);
        } finally{
            try {
                if (pstmt!=null) pstmt.close();
                if (rs!=null) rs.close();
            } catch (Exception e) {
                throw new DbException("", e);
            }
        }
        return result;
    }

    /**
     * Search for books in a database
     * @param colName the column name to search within
     * @param searchStr the search string to search for
     * @return The result from the search
     * @throws DbException
     */
    @Override
    public List<Book> searchForBooks(String colName, String searchStr) throws DbException {
        // Prepared statement text
        String sql = "SELECT Book.* FROM Book WHERE " + colName + " LIKE ?;";

        List<Book> result = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Prepared statement
            pstmt = con.prepareStatement(sql);
            // Set the parameter
            pstmt.setString(1, "%" + searchStr + "%");
            // Execute the SQL query
            rs = pstmt.executeQuery();
            // Get the attribute values

            while (rs.next()) {
                String ISBN = rs.getString("ISBN");
                String title = rs.getString("title");
                String publisher = rs.getString("publisher");
                String userName = rs.getString("user");
                User user = new User(userName);
                List<String> genre = getGenre(ISBN);
                List<Author> authors = getAuthors(ISBN);
                int grade = getGrade(ISBN);

                Book book = new Book(ISBN, title, publisher, authors, genre, user,grade);
                result.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

            throw new DbException("",e);
        } finally {
            try {
                if (rs!=null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("Could not close resource",e);
            }
        }

        return result;
    }

    /**
     * Search for books in a database with a specific genre
     * @param genre the genre to search for
     * @return the result of the search
     * @throws DbException
     */
    @Override
    public List<Book> searchForGenre(String genre) throws DbException {
        // Prepared statement text
        String sql = "SELECT Book.* FROM Genre, Book WHERE (Genre.ISBN = Book.ISBN AND Genre.genre LIKE ?) ";
        List<Book> result = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Prepared statement
            pstmt = con.prepareStatement(sql);
            // Set the parameter
            pstmt.setString(1,"%" + genre + "%");
            // Execute the SQL query
            rs = pstmt.executeQuery();
            // Get the attribute values
            while (rs.next()) {
                String ISBN = rs.getString("ISBN");
                String title = rs.getString("title");
                String publisher = rs.getString("publisher");
                String user = rs.getString("user");
                int grade = getGrade(ISBN);

                result.add(new Book(ISBN, title, publisher,getAuthors(ISBN), getGenre(ISBN), getUser(user),grade));
            }
        } catch (SQLException e) {
            throw new DbException("",e);
        } finally {
            try {
                if (rs!=null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("Could not close resource",e);
            }
        }

        return result;
    }

    /**
     * Search for books in a database with a specific author
     * @param name the name of the author
     * @return the result of the search
     * @throws DbException
     */
    @Override
    public List<Book> searchForBooksByAuthor(String name) throws DbException {
        // Prepared statement text
        String sql = "SELECT Book.* FROM Book, AuthorToBook WHERE (AuthorToBook.authorName LIKE ? AND AuthorToBook.ISBN = Book.ISBN );";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Book> result = new ArrayList<>();
        try {
            // Prepared statement
            pstmt = con.prepareStatement(sql);
            // Set the parameter
            pstmt.setString(1,"%" + name + "%");

            // Execute the SQL query
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String ISBN = rs.getString("ISBN");
                String title = rs.getString("title");
                String publisher = rs.getString("publisher");
                String userName = rs.getString("user");
                User user = new User(userName);
                List<String> genre = getGenre(ISBN);
                List<Author> authors = getAuthors(ISBN);
                int grade = getGrade(ISBN);

                Book book = new Book(ISBN, title, publisher, authors, genre, user,grade);
                result.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new DbException("",e);
        } finally {
            try {
                if (rs!=null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("Could not close resource",e);
            }
        }

        return result;
    }

    /**
     * Add a book to the database
     * @param book the book to add
     * @throws DbException
     */
    @Override
    public void addBookToDatabase(Book book) throws DbException {

        List<Book> result = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con.setAutoCommit(false);

            // Add book to database
            addBook(book.getIsbn(), book.getTitle(), book.getPublisher(), book.getUser());

            // Add genre to book in database
            for (int i = 0;i<book.getGenre().size();i++) {
                addGenre(book.getIsbn(), book.getGenre().get(i));
            }

            for (int i = 0; i < book.getAuthors().size() ; i++) {
                // Checking if author already exist in the database
                pstmt = con.prepareStatement("SELECT authorId FROM AUTHOR WHERE (authorName = ? AND dob = ?)");
                pstmt.setString(1,book.getAuthors().get(i).getName());
                pstmt.setDate(2,book.getAuthors().get(i).getDob());
                rs = pstmt.executeQuery();

                if (rs.next()) { // If author exist
                    addAuthorBookRelationship(book.getIsbn(), rs.getInt("authorId"));
                } else if (!rs.next()) { // If author doesn't exist
                    int authorId = addAuthor(book.getAuthors().get(i).getName(), book.getAuthors().get(i).getDob(), book.getUser());
                    addAuthorBookRelationship(book.getIsbn(),authorId);
                }
            }

            con.commit();
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                throw new DbException("",ex);
            }
            throw new DbException("",e);

        } finally {
            try {
                if(con != null) con.setAutoCommit(true);
                if (rs!=null) rs.close();
                if (pstmt!=null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("",e);
            }
        }
    }


    @Override
    public void deleteBook(String ISBN) throws DbException {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("DELETE FROM BOOK WHERE ISBN = ?");
            pstmt.setString(1,ISBN);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DbException("",e);
        } finally {
            try {
                if (pstmt!=null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("",e);
            }
        }
    }

    @Override
    public void reviewBook(String ISBN, String user, String comment, int grade) throws DbException {

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        PreparedStatement pstmt2 = null;
        try {
            // Have user already reviewed this book

            pstmt1 = con.prepareStatement("SELECT Review.* FROM Review WHERE (user=? AND ISBN=?)");

            pstmt1.setString(1, user);
            pstmt1.setString(2,ISBN);

            rs1 = pstmt1.executeQuery();
            if (rs1.next()) {
                throw new DbException("User has already added a review");
            } else {
                //PreparedStatement pstmt = con.prepareStatement("INSERT INTO Review(ISBN, user, review_comment, grade, review_date) VALUES (?, ?, ?, ?, ?)");
                pstmt2 = con.prepareStatement("INSERT INTO Review (grade, review_date, review_comment, ISBN, user) VALUES (?, ?, ?, ?, ?)");
                // INSERT INTO `laboration_1`.`review` (`reviewId`, `grade`, `review_date`, `review_comment`, `ISBN`, `user`) VALUES ('', '5', '2021-01-01', 'Great book', '9780099555264', 'linus');
                pstmt2.setInt(1,grade);
                pstmt2.setDate(2,Date.valueOf(LocalDate.now()));
                pstmt2.setString(3,comment);
                pstmt2.setString(4,ISBN);
                pstmt2.setString(5,user);
                pstmt2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DbException("Could not review book",e);
        } finally {
            try {
                if (rs1!=null) rs1.close();
                if (pstmt1!=null) pstmt1.close();
                if (pstmt2!=null) pstmt2.close();
            } catch (Exception e) {
                throw new DbException("",e);
            }
        }
    }

    /**
     * Grade a book that exists in a database
     * @param grade the grade of the book
     * @param ISBN the isbn of the book
     * @throws DbException
     */
    @Override
    public void gradeBook(int grade, String ISBN) throws DbException {
        // Prepared statement text
        String sql = "UPDATE Book SET grade = ? WHERE (ISBN = ?)";
        PreparedStatement pstmt = null;
        try {
            // Prepared statement
            pstmt = con.prepareStatement(sql);
            // Set the parameters
            pstmt.setInt(1,grade);
            pstmt.setString(2,ISBN);
            // Execute the SQL query
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("",e);
        } finally {
            try {
                if (pstmt!=null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("",e);
            }
        }
    }

    @Override
    public void createAccount(String firstName, String lastName, String email, String username, String pwd) throws DbException {
        String sql = "INSERT INTO User(userName, firstName, lastName, email, password) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        try {
            // Prepared statement
            pstmt = con.prepareStatement(sql);
            // Set the parameters
            pstmt.setString(1,username);
            pstmt.setString(2,firstName);
            pstmt.setString(3,lastName);
            pstmt.setString(4,email);
            pstmt.setString(5,pwd);
            // Execute the SQL query
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("Could not add account",e);
        }
        finally {
            try {
                if (pstmt!=null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("",e);
            }
        }
    }

    @Override
    public boolean logIn(String username, String pwd) throws DbException {
        String sql = "SELECT User.* FROM User WHERE (userName = ? AND password = ?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean loggedIn = false;
        try {
            // Prepared statement
            pstmt = con.prepareStatement(sql);
            // Set the parameters
            pstmt.setString(1,username);
            pstmt.setString(2,pwd);
            // Execute the SQL query
            rs = pstmt.executeQuery();
            if (rs.next()) loggedIn=true;

        } catch (SQLException e) {
            throw new DbException("Could not log in",e);
        } finally {
            try {
                if (pstmt!=null) pstmt.close();
                if (rs!=null) rs.close();
            } catch (Exception e) {
                throw new DbException("Could not remove resources",e);
            }
        }
        return loggedIn;
    }

    ////////////////////// PRIVATE METHODS ///////////////////////////////////////

    /**
     * Add an author to the database using name and date of birth
     * @param name the name of the author
     * @param dob the date of birth of the author
     * @return the authorId of the author
     * @throws DbException
     */
    private int addAuthor(String name, Date dob, String user) throws SQLException {
        int authorId = 0;
        try {
            // Prepared statement
            PreparedStatement pstmt1 = con.prepareStatement("INSERT INTO Author(authorName, dob, user) VALUES (?, ?, ?)");
            // Set the parameters
            pstmt1.setString(1,name);
            pstmt1.setDate(2,dob);
            pstmt1.setString(3, user);
            // Execute the SQL query
            pstmt1.executeUpdate();

            PreparedStatement pstmt2 = con.prepareStatement("SELECT authorId FROM AUTHOR WHERE (authorName = ? AND dob = ?)");
            pstmt2.setString(1,name);
            pstmt2.setDate(2,dob);
            ResultSet rs = pstmt2.executeQuery();
            while (rs.next()) {
                authorId = rs.getInt("authorId");
            }

        } catch (SQLException e) {
            throw new SQLException();
        }

        return authorId;
    }

    private List<String> getGenre(String ISBN) throws SQLException, DbException {
        // Prepared statement text
        String sql = "SELECT * FROM Genre WHERE ISBN = ? ";
        List<String> result = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Prepared statement
            pstmt = con.prepareStatement(sql);
            // Set the parameter
            pstmt.setString(1,ISBN);
            // Execute the SQL query
            rs = pstmt.executeQuery();
            // Get the attribute values
            while (rs.next()) {
                System.out.println(rs.getString("genre"));
                result.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            throw new SQLException();
        } finally {
            try {
                if (pstmt!=null) pstmt.close();
                if (rs!=null) rs.close();
            } catch (Exception e) {
                throw new DbException("",e);
            }
        }
        return result;
    }

    private List<Author> getAuthors(String ISBN) throws SQLException, DbException {
        // Prepared statement text
        //String sql = "SELECT * FROM Genre WHERE ISBN = ? ";
        String sql = "SELECT AuthorToBook.authorName, AuthorToBook.authorId, AuthorToBook.user, AuthorToBook.dob FROM AuthorToBook WHERE (AuthorToBook.ISBN = ?);";
        List<Author> result = new ArrayList<>();

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Prepared statement
            pstmt = con.prepareStatement(sql);
            // Set the parameter
            pstmt.setString(1,ISBN);
            // Execute the SQL query
            rs = pstmt.executeQuery();
            // Get the attribute values
            while (rs.next()) {
                System.out.println("author");
                result.add(new Author(rs.getInt("authorId"), rs.getString("authorName"), getUser(rs.getString("user")), rs.getDate("dob")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new SQLException();
        } finally {
            try {
                if (pstmt!=null) pstmt.close();
                if (rs!=null) rs.close();
            } catch (Exception e) {
                throw new DbException("",e);
            }
        }
        return result;
    }


    private void addBook(String ISBN, String title, String publisher, String user) throws DbException, SQLException {
        // Prepared statement
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("INSERT INTO Book(ISBN, title, publisher, user) VALUES (?, ?, ?, ?)");
            // Set the parameter
            pstmt.setString(1,ISBN);
            pstmt.setString(2,title);
            pstmt.setString(3,publisher);
            pstmt.setString(4,user);
            // Execute the SQL query for pstmt
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("", e);
        } finally{
            try {
                if (pstmt!=null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("", e);
            }
        }
    }

    private void addGenre(String ISBN, String genre) throws SQLException, DbException {
        // Prepared statement
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("INSERT INTO Genre(genre, ISBN) VALUES (?, ?)");
            // Set the parameter for ISBN
            pstmt.setString(1,genre);
            pstmt.setString(2, ISBN);
            // Execute the SQL query for pstmt
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("", e);
        } finally{
            try {
                if (pstmt!=null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("", e);
            }
        }
    }

    private void addAuthorBookRelationship(String ISBN, int authorId) throws SQLException, DbException {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("INSERT INTO Book_Author(ISBN, authorId) VALUES (?, ?)");
            // Set the parameters
            pstmt.setString(1,ISBN);
            pstmt.setInt(2,authorId);
            // Execute the SQL query for pstmt
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("", e);
        } finally{
            try {
                if (pstmt!=null) pstmt.close();
            } catch (Exception e) {
                throw new DbException("", e);
            }
        }
    }

    private int getGrade(String ISBN) throws DbException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int grade = 0;
        try {
            pstmt = con.prepareStatement("SELECT * FROM GradeForBooks WHERE ISBN = ?");
            // Set the parameters
            pstmt.setString(1,ISBN);
            // Execute the SQL query for pstmt
            rs = pstmt.executeQuery();
            while (rs.next()) {
                grade = rs.getInt("AVG(Review.grade)");
            }
        } catch (Exception e) {
            throw new DbException("", e);
        } finally{
            try {
                if (pstmt!=null) pstmt.close();
                if (rs!=null) rs.close();
            } catch (Exception e) {
                throw new DbException("", e);
            }
        }
        return grade;
    }
}
