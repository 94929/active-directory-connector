/**
 * Created by jsh3571 on 27/12/2016.
 */

public class Main {
    public static void main(String[] args) {
        String host = "192.168.100.173";
        String port = "389";
        String username = "admin";
        String password = "privacy!@34";
        String baseDn = "cn=users,dc=comtrue,dc=com";
        String filter = "company=";

        ActiveDirectoryConnector dao =
                new ActiveDirectoryConnector(
                        host, port, username, password, baseDn, filter);

        System.out.println(dao.getUser("test"));
        dao.close();
    }
}
