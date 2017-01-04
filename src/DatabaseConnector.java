import java.sql.*;
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

    public void setTable(String table) {
        this.table = table;
    }

    public <K, V extends Object> void insertRows(List<Map<K, V>> entries) {
        // Iterate through the input, entries
        for (int i = 0; i < entries.size(); i++) {
            Map<K, V> entry = entries.get(i);
            List<String> values = new ArrayList<>();

            for (V val : entry.values()) values.add((String) val);

            insertRow(values);
        }

        System.out.println("Row(s) inserted into '" + table + "' table...");
    }

    /* Insert values as a row of the table given */
    public void insertRow(List<String> values) {
        String insertRowSQL =
                "INSERT INTO " + table
                        + "(name, loginid, loginpw) "
                        + "VALUES"
                        + "(?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertRowSQL)) {

            // Iterate through the values and set them into insertRowSQL
            for (int i = 0; i < values.size(); i++)
                pstmt.setString(i + 1, values.get(i));

            // It's crucial to executeUpdate() after setting all values
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* Delete row(s) from the current table by column name and values */
    public void deleteRow(String column, String value) {
        String deleteRowSQL =
                "DELETE FROM " + table + " WHERE " + column + "=?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteRowSQL)) {

            // Delete a row which contains the column value
            setPstmt(pstmt, getColumnType(conn, column), value);

            // It's crucial to executeUpdate() after setting all values
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Row(s) removed from '" + table + "' table...");
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

    private int getColumnType(Connection conn, String column)
            throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery("SELECT " + column + " FROM " + table);
        ResultSetMetaData rsmd = rs.getMetaData();

        return rsmd.getColumnType(1);
    }

    private String getColumnName(Connection conn, String column)
            throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs =
                stmt.executeQuery("SELECT " + column + " FROM " + table);
        ResultSetMetaData rsmd = rs.getMetaData();

        return rsmd.getColumnName(1);
    }

    /* Set pstmt depending on the type specified, currently supports frequently
     * used types only
     */
    private void setPstmt(PreparedStatement pstmt, int type, String value)
            throws SQLException {
        switch (type) {
            case 4: // integer
                pstmt.setInt(1, Integer.parseInt(value));
                break;
            case 12: // string
                pstmt.setString(1, value);
                break;
            case 1111: // boolean
                pstmt.setBoolean(1, Boolean.parseBoolean(value));
                break;
        }
    }
}
