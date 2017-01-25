package main;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jsh3571 on 27/12/2016.
 */
public class ActiveDirectoryConnector {
    private Properties env;
    private DirContext ctx;
    private String[] attrIDs;

    private final static Logger LOGGER =
            Logger.getLogger(ActiveDirectoryConnector.class.getName());

    /**
     * Logging onto active directory server with username and password.
     */
    public ActiveDirectoryConnector() {
        // Create new Properties, props(i.e. env)
        env = new Properties();

        try {
            // Obtaining the basePath of the current project
            String basePath = new File("").getAbsolutePath();

            // Init env(i.e. properties) which will contain configuration of ctx
            loadProps(env, basePath.concat("/src/config/env.properties"));

            // Create context, ctx from given configuration object, props
            ctx = new InitialDirContext(env);
        } catch (IOException | NamingException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Get all users having input values(attribute) for the filter given
     *
     * @return all users within a domain of the active directory given
     */
    public void getUsers() {
        List<Map<String, Object>> users = new LinkedList<>();

        try {
            // Searching data based on 'domain', 'filter' and searcher
            NamingEnumeration searchResults =
                    ctx.search(getDomain(), getFilter(), getControl());

            // Getting the result into the list given from the searchResults.
            getData(searchResults, users);

            // If the list is actually empty, it should return an empty one.
            if (isEmpty(users))
                users = Collections.EMPTY_LIST;

            // Sorting the result before saving.
            sortUsers(users);

            // Saving data
            saveUsers(users);
        } catch (NamingException | IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Logging out from Active Directory by closing DirContext, ctx.
     */
    public void close() {
        try {
            ctx.close();
        } catch (NamingException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    /**
     * Getting the result into the list given from the searchResults.
     */
    private void getData(NamingEnumeration searchResults,
                         List<Map<String, Object>> list)
            throws NamingException {

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
    }

    /**
     * Check if current users' list is actually empty.
     */
    private boolean isEmpty(List<Map<String, Object>> users) {
        for (int i = 0; i < users.size(); i++) {
            Map<String, Object> curr = users.get(i);
            if (!curr.isEmpty())
                return false;
        }

        return true;
    }

    /**
     * Sort current users before save it into data file.
     */
    private void sortUsers(List<Map<String, Object>> users) {
        String key = env.getProperty("key");

        if (Arrays.asList(attrIDs).contains(key))
            Collections.sort(users, comp);
    }

    /**
     * Save current users that is being hold in the data structure.
     */
    private void saveUsers(List<Map<String, Object>> users) throws IOException {
        String fileName = env.getProperty("fileName");

        Writer out =
                new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(fileName),
                                Charset.forName("UTF-8")));

        for (int i = 0; i < users.size(); i++) {
            Iterator<Map.Entry<String, Object>> it
                    = users.get(i).entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                out.write(entry.getKey() + "=" + entry.getValue());

                if (it.hasNext())
                    out.write(",");
            }

            out.write("\n");
        }

        out.close();
    }

    /**
     * Loading a properties in UTF-8 format.
     *
     * @param props
     * @param propsName
     */
    private void loadProps(Properties props, String propsName)
            throws IOException {
        FileInputStream fileInputStream =
                new FileInputStream(propsName);
        InputStreamReader inputStreamReader =
                new InputStreamReader(
                        fileInputStream, Charset.forName("UTF-8"));

        props.load(inputStreamReader);
    }

    /**
     * Saving a properties in UTF-8 format.
     *
     * @param props
     * @param propsName
     */
    private void saveProps(Properties props, String propsName)
            throws IOException {
        FileOutputStream fileOutputStream =
                new FileOutputStream(propsName);
        OutputStreamWriter outputStreamWriter =
                new OutputStreamWriter(
                        fileOutputStream, Charset.forName("UTF-8"));

        props.store(outputStreamWriter, null);
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

        // Setting attrIDs.
        attrIDs = env.getProperty("attrIDs").split(",");

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

    /**
     * Define own comparator to sort the user list, users.
     */
    private Comparator<Map<String, Object>> comp =
            new Comparator<Map<String, Object>>() {

                public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                    String key = env.getProperty("key");
                    String firstKey = (String) m1.get(key);
                    String secondKey = (String) m2.get(key);

                    return firstKey.compareTo(secondKey);
                }
            };
}
