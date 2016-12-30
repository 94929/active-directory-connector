import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by jsh3571 on 30/12/2016.
 */

public class DatabaseConnector {
    String url, usr, pwd;

    public DatabaseConnector(String url, String usr, String pwd) {
        this.url = url;
        this.usr = usr;
        this.pwd = pwd;
    }

    private Connection connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, usr, pwd);
    }

    /* updateDescription((1)String description, (2)int id)
     *
     */
    public void updateDescription(String description, int id) {
        String sql = "UPDATE a_text SET description = ? WHERE id = ?;";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // 'pstmt.set(#, input)' where # is place number for each input
            pstmt.setString(1, description);
            pstmt.setInt(2, id);

            // Use executeUpdate method to update set values
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
