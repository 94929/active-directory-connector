/**
 * Created by jsh3571 on 27/12/2016.
 */

public class Main {

    public static void main(String[] args) {
        String host = "192.168.100.173";
        String port = "389";
        String username = "admin";
        String password = "privacy!@34";
        String domain = "cn=users,dc=comtrue,dc=com";
        String filter = "company=";

        ActiveDirectoryConnector dao =
                new ActiveDirectoryConnector(
                        host, port, username, password, domain, filter);

        System.out.println(dao.getUser("컴트루테크놀로지"));
        dao.close();
    }
}
