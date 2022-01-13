module com.example.databasinterface {
    requires javafx.controls;
    requires javafx.base;
    requires java.sql;

// Might need to fix this in case of errors; according to readme from
// https://gits-15.sys.kth.se/anderslm/Databases-BooksDb-Mock-implementation/blob/master/README.md
    opens com.example.databasinterface to javafx.base;
    opens com.example.databasinterface.model to javafx.base;
    exports com.example.databasinterface;
}