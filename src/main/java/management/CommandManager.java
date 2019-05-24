package management;

import command.*;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class manages all the server-side commands available. The command syntax is the same as on most of the UNIX shells ('&amp;&amp;' is used as a conjunction, '||' is used as a disjunction). As of the moment only conjuction works but the rest is to be implemented in the future versions.<br><br>
 *
 * <b>List of available commands: </b>
 * <ul>
 *     <li>list - lists the specified entities</li>
 *     <li>shutdown - gently shuts down the system</li>
 *     <li>disconnect - disconnects a specified client</li>
 *     <li>ban - bans specified client</li>
 *     <li>man - displays an extensive manual of a specified command</li>
 *     <li>clear - cleans the screen</li>
 * </ul>
 *
 * @author Kornel Domeradzki
 */
public class CommandManager {

    /** Delimiter that represents the logical conjunction */
    private final String conjunctionDelim = "&&";

    /** Array of all available commands */
    public static Command[] commandArr = {
            new List("list"),
            new Shutdown("shutdown"),
            new Disconnect("disconnect"),
            new Ban("ban"),
            new Man("man"),
            new Clear("clear")
    };

    /**
     * Checks if CommandManager has a command with the specified name and returns it if it does.
     *
     * @param name name of the command
     *
     * @return The specified command
    */
    private Command hasCommand(String name) {
        for (Command cmd : commandArr)
            if (cmd.getCommandName().equals(name)) return cmd;
        return null;
    }

    /**
     * Used for intializing command session aliases and other shell settings
     */
    private void initCmdSession() {
    }

    /**
     * Parses and lexes the passed command into an array list of stringtokenizer for further use. <br><br>In future ArrayList will be changed into an AST and the method will have recursive descent parsing implemented so the usage in the future might be different.
     *
     * @param cmd returns the
     *
     * @return array list of stringtokenizer commands
     */
    private ArrayList<StringTokenizer> lexIt(String cmd) {
        StringTokenizer stAnd = new StringTokenizer(cmd, conjunctionDelim);
        int len = 0;
        ArrayList<StringTokenizer> cmds = new ArrayList<StringTokenizer>();
        while (stAnd.hasMoreTokens()) {
            cmds.add(new StringTokenizer(stAnd.nextToken()));
            len++;
        }

        return cmds;
    }

    /**
     * Executes and handles passed commands.
     *
     * @param cmd content of the commands
     *
     * @return execution status
     */
    public boolean manageCommand(String cmd) {
        Command cmdTemp = null;
        ArrayList<StringTokenizer> cmds = this.lexIt(cmd);
        for (StringTokenizer st : cmds) {
            if ((cmdTemp = this.hasCommand(st.nextToken())) == null)
                return false;
            if (!cmdTemp.execute(st))
                return false;
        }

        return true;
    }
}
