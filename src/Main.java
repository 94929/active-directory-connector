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
        String host = args[0];
        String port = args[1];
        String username = args[2];
        String password = args[3];

        // Creating a dao for ad
        ActiveDirectoryConnector dao =
                new ActiveDirectoryConnector(
                        host, port, username, password);

        // ActiveDirectoryConnector dao = new ActiveDirectoryConnector(host, port);

        // Setting domain to be searched
        dao.setDomain("cn=users,dc=comtrue,dc=com");

        // Setting attrIDs
        String[] attrIDs = {"name", "company", "lastLogoff"};
        dao.setAttrs(attrIDs);

        // Use getUser method according to its 'absolute key' value
        List<Map<String, Object>> output =
                dao.getUsers("st=", "서울특별시");

        // Printing out the result of ADC
        System.out.println(output);

        // Closing context, ctx after use
        dao.close();

        // Obtaining url, usr and pwd from program arguments section
        String url = args[4];
        String usr = args[5];
        String pwd = args[6];

        /*
        DatabaseConnector dbc = new DatabaseConnector(url, usr, pwd);

        // Setting the table name which we will insert the data into
        dbc.setTable("client_list");

        // Setting the column names which we will insert the data into
        dbc.setColumns(Arrays.asList("loginpw", "name", "loginid"));

        // Insert row(s) into the columns of the table given
        dbc.insertRows(output);

        // delete row(s) from table
        // dbc.deleteRow("loginid", "컴트루테크놀로지");

        // Closing dbc after use
        dbc.close();*/
    }
}
