package command;

import java.util.StringTokenizer;

// TODO:
public class Ban extends Command {

    public Ban(String cmdName) {
        super(cmdName);
    }

    protected void loadFlags(StringTokenizer cmdTokenizer) {

    }

    public boolean execute(StringTokenizer cmdTokenizer) {
        return false;
    }

    protected void clearFlags() {

    }
}
