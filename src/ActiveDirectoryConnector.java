import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

/**
 * Created by jsh3571 on 27/12/2016.
 */

public class ActiveDirectoryConnector {
    private Properties props;
    private DirContext ctx;
    private String domain;
    private String[] attrIDs;

    /**
     * Logging onto active directory server with username and password.
     *
     * @param host
     * @param port
     * @param username
     * @param password
     */
    public ActiveDirectoryConnector(String host, String port,
                                    String username, String password) {
        // Init new Properties, props(i.e. env or conf)
        props = new Properties();

        // Init env(i.e. properties) which will contain configuration of ctx
        initEnv(host, port, username, password);

        try {
            // Create context, ctx from given configuration object, env
            ctx = new InitialDirContext(props);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logging onto active directory server as anonymous.
     *
     * @param host
     * @param port
     */
    public ActiveDirectoryConnector(String host, String port) {
        props = new Properties();

        initEnv(host, port);

        try {
            ctx = new InitialDirContext(props);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setAttrs(String[] attrIDs) {
        this.attrIDs = attrIDs;
    }

    /**
     * Get all users in the current active directory given.
     *
     * @return
     */
    public List<Map<String, Object>> getAllUsers() {
        return new LinkedList<>();
    }

    /**
     * Get all users having input values(attribute) for the filter given
     *
     * @param filter is the key which we are searching for
     * @param input is the value of the attribute for filter
     * @return all users within a domain of the active directory given
     */
    public List<Map<String, Object>> getUsers(String filter, String input) {
        List<Map<String, Object>> list = new LinkedList<>();

        try {
            // Searching data based on 'domain', 'filter' and searcher
            NamingEnumeration searchResult =
                    ctx.search(domain, filter + input, getControl());

            // Depending on hasData, map will contain searchResult or not
            boolean hasData = searchResult.hasMore();

            // Parse searched data(i.e. result) into resulting map
            while (searchResult.hasMore()) {
                // Creating resulting map which will be appended to the list
                Map<String, Object> result = new HashMap<>();

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

                // Appending result map into the list
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

    /**
     * Logging out from Active Directory by closing DirContext, ctx.
     * @return
     */
    public void close() {
        try {
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private void initEnv(String host, String port,
                         String username, String password) {

        // Connect to active directory using LDAP.
        props.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);

        // Authenticate as standard user using given username and password
        props.put(Context.SECURITY_AUTHENTICATION, "simple");
        props.put(Context.SECURITY_PRINCIPAL, username);
        props.put(Context.SECURITY_CREDENTIALS, password);
    }

    private void initEnv(String host, String port) {
        props.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);
        props.put(Context.SECURITY_AUTHENTICATION, "none");
    }

    private SearchControls getControl() {
        // Creating new search control that will handle search configuration
        SearchControls ctls = new SearchControls();

        /* Select attributes to be returned as result, if any of specified attr
         * ('dummy' in this case) is not in the user's attrs then the resulting
         * map will not contain 'dummy' but all.
         * e.g. attrIDs = {"name", "company", "dummy"};
         */
        ctls.setReturningAttributes(attrIDs);

        // Setting search scope, check declaration to see other types of scope
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        return ctls;
    }
}
