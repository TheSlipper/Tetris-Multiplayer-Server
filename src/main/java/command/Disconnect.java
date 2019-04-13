package command;

import java.util.StringTokenizer;

// TODO:
public class Disconnect extends Command {

    public Disconnect(String cmdName) {
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
