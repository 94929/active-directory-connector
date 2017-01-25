import main.ActiveDirectoryConnector;

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
     * 3. Maybe logger is needed and saves log as file.
     */
    public static void main(String[] args) throws Exception {
        // Creating a dao for ad
        ActiveDirectoryConnector adc = new ActiveDirectoryConnector();

        // Setting domain to be searched
        adc.setDomain("cn=users,dc=comtrue,dc=com");

        // Setting attrIDs
        adc.setAttrs(new String[] {"name", "company"});

        /* Use getUser method according to its 'absolute key' value.
         *
         * If you want to find all users from AD, use "objectclass=user".
         * If you want to find all groups from AD, use "objectclass=group".
         */
        List<Map<String, Object>> output =
                adc.getUsers("st=", "서울특별시");

        // Printing out the result of ADC
        System.out.println(output);

        // Closing context, ctx after use
        adc.close();

        // Obtaining url, usr and pwd from program arguments section
        String url = args[0];
        String usr = args[1];
        String pwd = args[2];

        main.DatabaseConnector dbc = new main.DatabaseConnector(url, usr, pwd);

        // Setting the table name which we will insert the data into
        dbc.setTable("client_list");

        // Setting the column names which we will insert the data into
        dbc.setColumns(Arrays.asList("loginpw", "name", "loginid"));

        // Insert row(s) into the columns of the table given
        // dbc.insertRows(output);

        // delete row(s) from table
        // dbc.deleteRow("loginid", "yjlee_root");

        // Closing dbc after use
        dbc.close();
    }
}
