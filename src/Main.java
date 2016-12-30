
/**
 * Created by jsh3571 on 27/12/2016.
 */

public class Main {

    /* TODO::
     * 1. Sort result list by key of each map
     * 2. Use internal iterator in getUser method to encapsulate information
     * 3. Maybe refactor the way to pass the parameters to dao constructor
     */
    public static void main(String[] args) {
        String domain = "cn=users,dc=comtrue,dc=com";
        String filter = "st=";

        ActiveDirectoryConnector dao =
                new ActiveDirectoryConnector(
                        args[0], args[1], args[2], args[3], domain, filter);

        // Use getUser method according to its 'absolute key' value
        System.out.println(dao.getUser("서울특별시"));

        // Closing context, ctx after use
        dao.close();

        /* NOW CONNECTING TO DB AND REPLACE DATA */
        DatabaseConnector dbConnector
                = new DatabaseConnector(args[4], args[5], args[6]);

        dbConnector.updateDescription("ABCD", 200);
    }
}
