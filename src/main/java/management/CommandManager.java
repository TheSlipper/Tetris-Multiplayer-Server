package management;

import command.*;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class CommandManager {

    private final String lineDelim = "&&";

    public static Command[] commandArr = {
            new List("list"),
            new Shutdown("shutdown"),
            new Disconnect("disconnect"),
            new Ban("ban"),
            new Man("man"),
            new Clear("clear")
    };

    private Command hasCommand(String name) {
        for (Command cmd : commandArr)
            if (cmd.getCommandName().equals(name)) return cmd;
        return null;
    }

    private void initCmdSession() {

    }

    private ArrayList<StringTokenizer> lexIt(String cmd) {
        StringTokenizer stAnd = new StringTokenizer(cmd, lineDelim);
        int len = 0;
        ArrayList<StringTokenizer> cmds = new ArrayList<StringTokenizer>();
        while (stAnd.hasMoreTokens()) {
            cmds.add(new StringTokenizer(stAnd.nextToken()));
            len++;
        }

        return cmds;
    }

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
