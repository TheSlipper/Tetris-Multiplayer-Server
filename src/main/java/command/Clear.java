package command;

import java.util.StringTokenizer;

public class Clear extends Command {

    int lines = 40;

    public Clear(String cmdName) {
        super(cmdName);
    }

    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while (cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();
            if (token.startsWith("--")) {
                if (token.contains("l"))
                    this.lines = Integer.parseInt(cmdTokenizer.nextToken());
            }
        }
    }

    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        this.loadFlags(cmdTokenizer);

        for (int i = 0; i < this.lines; i++)
            System.out.println();

        return true;
    }

    protected void clearFlags() {
        this.lines = 40;
    }
}
