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
    private List<String> input;

    public DatabaseConnector(String url, String usr, String pwd) {
        this.url = url;
        this.usr = usr;
        this.pwd = pwd;
    }

    public void updateInput(List<String> input) {
        this.input = input;
    }

    /* This method update table values in the db given
     * Update table according to the key value(i.e. key) given
     */
    public void updateTable(String key) {
        String sql = "UPDATE a_test SET cn = ? company = ? WHERE id = ?;";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            initPstmt(pstmt, input);

            // Set key value
            pstmt.setString(input.size() + 1, key);

            // Use executeUpdate method to update set values
            pstmt.executeUpdate();
        } catch (Exception e) {
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
