import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

/**
 * Created by jsh3571 on 27/12/2016.
 */

public class ActiveDirectoryConnector<K, V> {
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

    public List<Map<K, V>> getUser(String input) {
        List<Map<K, V>> list = new LinkedList<>();

        try {
            // Searching data based on 'domain', 'filter' and searcher
            NamingEnumeration searchResult =
                    ctx.search(domain, filter+input, getControl());

            // Depending on hasData, map will contain searchResult or not
            boolean hasData = searchResult.hasMore();

            // Parse searched data(i.e. result) into resulting map
            while (searchResult.hasMore()) {
                // Creating resulting map which will be appended to the list
                Map result = new HashMap<>();

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
                list.add(result);
            }

            // If searchResult was empty, the method should return an empty map
            if (!hasData)
                list = Collections.EMPTY_LIST;

        } catch (NamingException e) {
            e.printStackTrace();
        }

        return list;
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
        // Creating new search control that will handle search configuration
        SearchControls ctls = new SearchControls();

        /* Select attributes to be returned as result, if any of specified attr
         * ('dummy' in this case) is not in the user's attrs then the resulting
         * map will not contain 'dummy' but all
         */
        String[] attrIDs = {"cn", "co", "company", "countryCode", "dummy"};
        ctls.setReturningAttributes(attrIDs);

        // Setting search scope, check declaration to see other types of scope
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        return ctls;
    }
}
