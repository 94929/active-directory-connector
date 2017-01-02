import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by jsh3571 on 30/12/2016.
 */

public class DatabaseConnector {
    private String url, usr, pwd;
    private String sql;
    private String table;

    public DatabaseConnector(String url, String usr, String pwd) {
        this.url = url;
        this.usr = usr;
        this.pwd = pwd;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
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
    public void updateValue(String key) {
        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // initPstmt(pstmt, values);

            // Set key value
            // pstmt.setString(values.size() + 1, key);

            // Use executeUpdate method to update set values
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert values as a row of the table given
    public void insertRow(List<String> values) {
        String sql = "INSERT INTO "+table+" VALUES ?;";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Build resulting string which will be replaced with '?'
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < values.size(); i++) {
                sb.append('\'');
                sb.append(values.get(i));
                sb.append('\'');

                if (i < values.size() - 1) {
                    sb.append(',');
                } else {
                    sb.append(')');
                }
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

    private void initPstmt(PreparedStatement pstmt, List<String> values)
            throws SQLException {

        for (int i = 1; i <= values.size(); i++)
            // 'pstmt.set(#, input)' where # is place number for each input
            pstmt.setString(i, values.get(i));
    }
}
