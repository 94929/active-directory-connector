import main.ActiveDirectoryConnector;

/**
 * Created by jsh3571 on 27/12/2016.
 */

public class Main {

    public static void main(String[] args) {
        // Creating a dao for AD
        ActiveDirectoryConnector adc = new ActiveDirectoryConnector();

        /* Use getUser method according to its 'absolute key' value.
         *
         * If you want to find all users from AD, use "objectclass=user".
         * If you want to find all groups from AD, use "objectclass=group".
         */
        adc.getUsers();

        // Closing context, adc after using it.
        adc.close();
    }
}
