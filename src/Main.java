/**
 * Created by jsh3571 on 27/12/2016.
 */

public class Main {

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
    }
}
