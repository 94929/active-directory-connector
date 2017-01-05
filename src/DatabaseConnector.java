import com.sun.jdi.InvalidTypeException;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jsh3571 on 30/12/2016.
 */

public class DatabaseConnector {
    private final String url, usr, pwd;
    private String table;
    private List<String> columns;
    private Connection conn;

    public DatabaseConnector(String url, String usr, String pwd) {
        this.url = url;
        this.usr = usr;
        this.pwd = pwd;

        columns = new LinkedList<>();

        try {
            conn = connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public <V extends Object> void insertRows(List<Map<String, V>> entries) {

        // Iterate through the input, entries
        for (int i = 0; i < entries.size(); i++) {
            Map<String, V> entry = entries.get(i);
            List<String> values = new ArrayList<>();

            for (V val : entry.values())
                values.add((String) val);

            insertRow(values);
        }

        System.out.println("Row(s) inserted into '" + table + "' table...");
    }

    /* Insert values as a row of the table given */
    public void insertRow(List<String> values) {

        /* When insert a new row, number of the inserting values should be equal
         * to the number of columns of the table given
         */
        if (values.size() != columns.size())
            throw new RuntimeException();

        String insertRowSQL =
                "INSERT INTO " + table
                        + getColumns()
                        + "VALUES"
                        + getPlaceHolders();

        try (PreparedStatement pstmt = conn.prepareStatement(insertRowSQL)) {

            // Iterate through the values and set them into insertRowSQL
            for (int i = 0; i < values.size(); i++) {
                setPstmt(pstmt, getColumnType(conn, columns.get(i)),
                        values.get(i), i + 1);
            }

            // It's crucial to executeUpdate() after setting all values
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Delete row(s) from the current table by column name and values */
    public void deleteRow(String column, String value) {
        String deleteRowSQL =
                "DELETE FROM " + table + " WHERE " + column + "=?";

        try (PreparedStatement pstmt = conn.prepareStatement(deleteRowSQL)) {

            // Delete a row which contains the column value
            setPstmt(pstmt, getColumnType(conn, column), value, 1);

            // It's crucial to executeUpdate() after setting all values
            pstmt.executeUpdate();
        } catch (Exception e) {
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

    /* Set pstmt depending on the type specified, currently supports frequently
     * used types only
     */
    private void setPstmt(PreparedStatement pstmt, int type, String value,
                          int index)
            throws SQLException, InvalidTypeException {

        switch (type) {
            case 4:     // integer
                pstmt.setInt(index, Integer.parseInt(value));
                break;
            case 12:    // string
                pstmt.setString(index, value);
                break;
            case 1111:  // boolean
                pstmt.setBoolean(index, Boolean.parseBoolean(value));
                break;
            default:    // invalid type
                throw new InvalidTypeException();
        }
    }

    private String getColumns() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < columns.size(); i++) {
            sb.append(columns.get(i));

            if (i < columns.size() - 1)
                sb.append(",");
            else
                sb.append(") ");
        }
        return sb.toString();
    }

    // count required number of place holders, '?' and replace them
    private String getPlaceHolders() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < columns.size(); i++) {
            sb.append("?");
            if (i < columns.size() - 1)
                sb.append(",");
            else
                sb.append(")");
        }
        return sb.toString();
    }
}
