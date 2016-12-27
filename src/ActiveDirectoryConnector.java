import javax.naming.directory.DirContext;

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


    }
}
