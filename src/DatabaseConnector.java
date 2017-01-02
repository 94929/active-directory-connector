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

    public DatabaseConnector(String url, String usr, String pwd) {
        this.url = url;
        this.usr = usr;
        this.pwd = pwd;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    /* This method updates value of the table in the db given
     * Update table according to the key value(i.e. key) given
     */
    public void updateRow(String key, List<String> values) {
        String sql = "UPDATE " + table + " SET cn = ? company = ? WHERE id = ?;";
        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Set cn and company
            for (int i = 0; i < values.size(); i++)
                pstmt.setString(i + 1, values.get(i));

            // Set key value
            pstmt.setString(values.size() + 1, key);

            // Use executeUpdate method to update set values
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        String insertRowSQL = "INSERT INTO " + table + "(cn, company) VALUES"
                + "(?,?);";

        try (Connection connection = connect();
             PreparedStatement pstmt =
                     connection.prepareStatement(insertRowSQL)) {

            for (int i = 0; i < values.size(); i++)
                pstmt.setString(i+1, values.get(i));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(url, usr, pwd);
    }
}
