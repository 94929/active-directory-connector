import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by jsh3571 on 27/12/2016.
 */

public class ActiveDirectoryConnector {
    private Hashtable<String, Object> env;
    private DirContext ctx;
    private String domain;
    private String filter;

    public ActiveDirectoryConnector(String host, String port,
                                    String username, String password,
                                    String domain, String filter) {
        this.domain = domain;
        this.filter = filter;

        // Init env(i.e. properties) which will contain configuration of ctx
        initEnv(host, port, username, password);

        try {
            // Create context, ctx from given configuration object, env
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public Map getUser(String input) {
        // Creating resulting map which will be returned
        Map result = new HashMap<>();

        try {
            // Searching data based on 'domain', 'filter' and searcher
            NamingEnumeration searchResult =
                    ctx.search(domain, filter+input, getControl());

            // Depending on hasData, map will contain searchResult or not
            boolean hasData = searchResult.hasMore();

            // Parse searched data(i.e. result) into resulting map
            while (searchResult.hasMore()) {
                // Accessing each element of searchResult
                SearchResult each = (SearchResult) searchResult.nextElement();

                /* From each resulting element, get all attributes
                 * However, if you use getAttributes().get(String attrID)
                 * then you can retrieve a specific attribute
                 */
                NamingEnumeration attributes = each.getAttributes().getAll();

                // As I have obtained all attributes, iterate through them
                while (attributes.hasMore()) {

                    // Retrieving each attribute from attributes
                    Attribute attribute = (Attribute) attributes.nextElement();

                    // From the retrieved attribute, put key and value to result
                    result.put(attribute.getID(), attribute.get());
                }
            }

            // If searchResult was empty, the method should return an empty map
            if (!hasData)
                result = Collections.EMPTY_MAP;

        } catch (NamingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void close() {
        try {
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private void initEnv(String host, String port,
                         String username, String password) {
        // Init env(i.e. properties) which will contain configuration of ctx
        env = new Hashtable<>();

        // Connect to active directory using LDAP.
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL,
                "ldap://" + host + ":" + port);

        // Authenticate as standard user using given username and password
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
    }

    private SearchControls getControl() {
        String[] attrIDs = {"cn", "co", "company", "countryCode", "dummy"};
        SearchControls ctls = new SearchControls();
        ctls.setReturningAttributes(attrIDs);
        return ctls;
    }
}
