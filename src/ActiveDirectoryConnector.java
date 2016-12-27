import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by jsh3571 on 27/12/2016.
 */

public class ActiveDirectoryConnector {
    private DirContext ctx;
    private String baseDn;
    private String filter;

    public ActiveDirectoryConnector(String host, String port,
                                    String username, String password,
                                    String baseDn, String filter) {
        this.baseDn = baseDn;
        this.filter = filter;

        // Create env(i.e. properties) which will contain configuration of ctx
        Hashtable<String, Object> env = new Hashtable<>();

        // Connect to active directory using LDAP.
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL,
                "ldap://" + host + ":" + port);

        // Authenticate as standard user using given username and password
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            // Create context, ctx from given configuration object, env.
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getUser(String input) {
        Map<String, Object> result = new HashMap<>();
        return result;
    }

    public void close() {

    }
}
