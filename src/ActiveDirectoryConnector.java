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
    private String baseDn;
    private String filter;

    public ActiveDirectoryConnector(String host, String port,
                                    String username, String password,
                                    String baseDn, String filter) {
        this.baseDn = baseDn;
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

    public Map<String, Object> getUser(String input) {
        // Creating resulting map which will be returned
        Map<String, Object> result = new HashMap<>();

        // Creating searcher which will be passed into ctx.search
        SearchControls searcher = new SearchControls();
        searcher.setSearchScope(SearchControls.SUBTREE_SCOPE);

        try {
            // Searching data based on 'domain', 'filter' and searcher
            NamingEnumeration searchResult =
                    ctx.search(baseDn, filter+input, searcher);

            // Depending on hasData, map will contain searchResult or not
            boolean hasData = searchResult.hasMore();

            // Parse searched data(i.e. result) into resulting map
            while (searchResult.hasMore()) {
                SearchResult each = (SearchResult) searchResult.nextElement();
                NamingEnumeration attributes = each.getAttributes().getAll();
                while (attributes.hasMore()) {
                    Attribute attribute = (Attribute) attributes.nextElement();
                    result.put(attribute.getID(), attribute.get());
                }
            }

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
        // Create env(i.e. properties) which will contain configuration of ctx
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
}
