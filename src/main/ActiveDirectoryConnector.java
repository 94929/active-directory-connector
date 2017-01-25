package main;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by jsh3571 on 27/12/2016.
 */
public class ActiveDirectoryConnector {
    // private static Logger logger = Logger.getLogger("ActiveDirectoryConnector.class");
    private Properties env;
    private DirContext ctx;

    /**
     * Logging onto active directory server with username and password.
     */
    public ActiveDirectoryConnector() {
        // Create new Properties, props(i.e. env or conf)
        env = new Properties();

        // Init env(i.e. properties) which will contain configuration of ctx
        loadProps(env, "env.properties");

        try {
            // Create context, ctx from given configuration object, props
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all users having input values(attribute) for the filter given
     *
     * @return all users within a domain of the active directory given
     */
    public List<Map<String, Object>> getUsers() {
        List<Map<String, Object>> list = new LinkedList<>();

        try {
            // Searching data based on 'domain', 'filter' and searcher
            NamingEnumeration searchResults =
                    ctx.search(getDomain(), getFilter(), getControl());

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

        // Sorting the result before saving.
        Collections.sort(list, comp);

        // Creating a container properties, data.
        Properties data = new Properties();

        // Transferring the list to the properties, data.
        for (int i = 0; i < data.size(); i++)
            data.putAll(list.get(i));

        // Saving resulting data into properties class.
        saveProps(data, "data.properties");

        return list;
    }

    /**
     * Logging out from Active Directory by closing DirContext, ctx.
     */
    public void close() {
        try {
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private Comparator<Map<String, Object>> comp =
            new Comparator<Map<String, Object>>() {

        public int compare(Map<String, Object> m1, Map<String, Object> m2) {
            String key = env.getProperty("key");
            String firstKey = (String) m1.get(key);
            String secondKey = (String) m2.get(key);

            return firstKey.compareTo(secondKey);
        }
    };

    /**
     * Loading a properties in UTF-8 format.
     *
     * @param props
     * @param propsName
     */
    private void loadProps(Properties props, String propsName) {
        try {
            FileInputStream fileInputStream =
                    new FileInputStream(propsName);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(
                            fileInputStream, Charset.forName("UTF-8"));

            props.load(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saving a properties in UTF-8 format.
     *
     * @param props
     * @param propsName
     */
    private void saveProps(Properties props, String propsName) {
        try {
            FileOutputStream fileOutputStream =
                    new FileOutputStream(propsName);
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(
                            fileOutputStream, Charset.forName("UTF-8"));

            props.store(outputStreamWriter, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDomain() {
        return env.getProperty("domain");
    }

    private String getFilter() {
        return env.getProperty("filter").replace(',', '=');
    }

    private SearchControls getControl() {
        // Creating a new search control that will handle search configuration.
        SearchControls ctls = new SearchControls();

        /* Select attributes to be returned as result, if any of specified attr
         * ('dummy' in this case) is not in the user's attrs then the resulting
         * map will not contain 'dummy' but all.
         *
         * e.g. attrIDs = {"name", "company", "dummy"};
         */
        ctls.setReturningAttributes(env.getProperty("attrIDs").split(","));

        // Setting search scope, check declaration to see other types of scope
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        return ctls;
    }
}
