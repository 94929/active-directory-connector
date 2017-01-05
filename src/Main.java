import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by jsh3571 on 27/12/2016.
 */

public class Main {

    /* TODO::
     * 1. Sort result list by key of each map
     * 2. Use internal iterator in getUser method to encapsulate information
     * 3. Maybe refactor the way to pass the parameters to dao constructor
     */
    public static void main(String[] args) throws Exception {
        String domain = "cn=users,dc=comtrue,dc=com";

        // Creating a dao for ad
        ActiveDirectoryConnector dao =
                new ActiveDirectoryConnector(
                        args[0], args[1], args[2], args[3], domain);

        // Use getUser method according to its 'absolute key' value
        List<Map<String, String>> output =
                dao.getUser("st=", "서울특별시");

        // Printing out the result of ADC
        System.out.println(output);

        // Closing context, ctx after use
        dao.close();

        // Obtaining url, usr and pwd from program arguments section
        String url = args[4];
        String usr = args[5];
        String pwd = args[6];

        DatabaseConnector dbc = new DatabaseConnector(url, usr, pwd);

        // Setting the table name which we will insert the data into
        dbc.setTable("client_list");

        // Setting the column names which we will insert the data into
        dbc.setColumns(Arrays.asList(
                "name", "loginid", "loginpw", "department"));

        // Insert a row into the columns of the table given
        // dbc.insertRow(Arrays.asList("이름", "로그인아이디", "로그인비밀번호"));
        dbc.deleteRow("status", "0");

        // Closing dbc after use
        dbc.close();
    }
}
