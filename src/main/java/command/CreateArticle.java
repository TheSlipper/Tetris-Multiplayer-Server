package command;

import java.util.StringTokenizer;

public class CreateArticle extends Command {

    public CreateArticle(String cmdName) {
        super(cmdName);
    }

    @Override
    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while (cmdTokenizer.hasMoreTokens()) {

        }
    }

    @Override
    public boolean execute(StringTokenizer cmdTokenizer) {
        return false;
    }

    @Override
    protected void clearFlags() {

    }
}
