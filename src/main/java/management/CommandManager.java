package management;

import command.*;

import java.util.StringTokenizer;

public class CommandManager {

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

    public boolean manageCommand(String cmd) {
        Command cmdTemp = null;
        StringTokenizer cmdTokenizer = new StringTokenizer(cmd);
        if ((cmdTemp = this.hasCommand(cmdTokenizer.nextToken())) == null)
            return false;
        return cmdTemp.execute(cmdTokenizer);
    }
}
