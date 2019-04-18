package command;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

// TODO:
public class Man extends Command {

    private final String manPath = "resources/mans/";

    private String manCmdName = "man";

    private final String fileFormat = ".md";

    public Man(String cmdName) {
        super(cmdName);
    }

    protected void loadFlags(StringTokenizer cmdTokenizer) {
        boolean first = true;
        while (cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();
            if (first && !token.startsWith("-")) {
                this.manCmdName = token;
                first = false;
            } else if (token.startsWith("--")) {
                if (token.contains("n"))
                    this.manCmdName = cmdTokenizer.nextToken();
            }
        }
    }

    private String getMessage() throws IOException {
        FileReader fr = new FileReader(this.manPath + this.manCmdName + this.fileFormat);
        BufferedReader bf = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        String helper = null;
        while ((helper = bf.readLine()) != null)
            sb.append(helper + "\r\n");

        return sb.toString();
    }

    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        this.loadFlags(cmdTokenizer);

        try {
            System.out.println(this.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected void clearFlags() {
        this.manCmdName = "man";
    }
}
