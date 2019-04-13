package command;

import java.util.StringTokenizer;

// TODO:
public class Man extends Command {

    public Man(String cmdName) {
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
