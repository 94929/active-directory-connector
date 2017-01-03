import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jsh3571 on 30/12/2016.
 */

public class DatabaseConnector {
    private final String url, usr, pwd;
    private String table;

    private Connection connection;
    private PreparedStatement pstmt;

    public DatabaseConnector(String url, String usr, String pwd) {
        this.url = url;
        this.usr = usr;
        this.pwd = pwd;

        try {
            connection = connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public <K, V> void insertRows(List<Map<K, V>> entries) {
        // Iterate through the input, entries
        for (int i = 0; i < entries.size(); i++) {
            Map<K, V> entry = entries.get(i);
            List<String> values = new ArrayList<>();

            for (V val : entry.values())
                values.add((String) val);

            insertRow(values);
        }
    }

    /* Insert values as a row of the table given */
    public void insertRow(List<String> values) {
        String insertRowSQL = "INSERT INTO " + table + " VALUES"
                + "(?,?);";

        try {
            pstmt = connection.prepareStatement(insertRowSQL);

            // Iterate through the values and set them into insertRowSQL
            for (int i = 0; i < values.size(); i++)
                pstmt.setString(i + 1, values.get(i));

            // It's crucial to executeUpdate() after setting all values
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Delete a row(or rows) from the current table by key and values */
    public void deleteRow(String key, String val) {
        String deleteRowSQL =
                "DELETE FROM " + table + " WHERE " + key + " = ?;";

        try {
            pstmt = connection.prepareStatement(deleteRowSQL);

            // Delete a row which contains the value, val
            pstmt.setString(1, val);

            // It's crucial to executeUpdate() after setting all values
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Opens a connection to the Postgresql database */
    private Connection connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(url, usr, pwd);
    }
}
