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
    private String url, usr, pwd;
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
        String sql = "INSERT INTO " + table + " VALUES (?);";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Build resulting string which will be replaced with '?'
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < values.size(); i++) {
                sb.append("'");
                sb.append(values.get(i));
                sb.append("'");

                if (i < values.size() - 1)
                    sb.append(",");
            }

            /* Setting PrepardedStatement, pstmt by combining given sql and
             * the resulting string which contains values of a row that will be
             * inserted into the table
             */
            pstmt.setString(1, sb.toString());
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
