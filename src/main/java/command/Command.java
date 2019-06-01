package command;

import java.util.StringTokenizer;

public abstract class Command {

    private String commandName;

    protected boolean executionStatus = true;

    public Command(String cmdName) {
        this.commandName = cmdName;
    }

    protected abstract void loadFlags(StringTokenizer cmdTokenizer);

    public abstract boolean execute(StringTokenizer cmdTokenizer);

    protected abstract void clearFlags();

    public String getCommandName() {
        return this.commandName;
    }

}
