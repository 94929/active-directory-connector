package main;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.*;
import java.util.*;

/**
 * Created by jsh3571 on 27/12/2016.
 */
public class ActiveDirectoryConnector {
    private Properties env;
    private DirContext ctx;
    private String domain;
    private String[] attrIDs;

    /**
     * Logging onto active directory server with username and password.
     */
    public ActiveDirectoryConnector() {
        // Create new Properties, props(i.e. env or conf)
        env = new Properties();

        // Init env(i.e. properties) which will contain configuration of ctx
        loadEnv();

        try {
            // Create context, ctx from given configuration object, props
            ctx = new InitialDirContext(env);
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
     * Get all users having input values(attribute) for the filter given
     *
     * @param filter is the key which we are searching for
     * @param value  is the value of the attribute for filter
     * @return all users within a domain of the active directory given
     */
    public List<Map<String, Object>> getUsers(String filter, String value) {
        List<Map<String, Object>> list = new LinkedList<>();

        try {
            // Searching data based on 'domain', 'filter' and searcher
            NamingEnumeration searchResults =
                    ctx.search(domain, filter + value, getControl());

            // Depending on hasData, map will contain searchResult or not
            boolean hasData = searchResults.hasMore();

            // Parse searched data(i.e. result) into resulting map
            while (searchResults.hasMore()) {
                // Creating resulting map which will be appended to the list
                Map<String, Object> result = new HashMap<>();

                // Accessing each element of searchResult
                SearchResult each = (SearchResult) searchResults.nextElement();

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

        // Sort users data
        sortUsers(list);

        // Save users data
        // saveUsers(list);

        return list;
    }

    /**
     * sort users by key
     */
    private void sortUsers(List<Map<String, Object>> users) {

    }

    /**
     * Logging out from Active Directory by closing DirContext, ctx.
     *
     * @return
     */
    public void close() {
        try {
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private void loadEnv() {
        try {
            env.load(new FileInputStream("env.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEnv() {
        try {
            env.store(new FileWriter("env.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SearchControls getControl() {
        // Creating new search control that will handle search configuration
        SearchControls ctls = new SearchControls();

        /* Select attributes to be returned as result, if any of specified attr
         * ('dummy' in this case) is not in the user's attrs then the resulting
         * map will not contain 'dummy' but all.
         *
         * e.g. attrIDs = {"name", "company", "dummy"};
         */
        ctls.setReturningAttributes(attrIDs);

        // Setting search scope, check declaration to see other types of scope
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        return ctls;
    }
}
