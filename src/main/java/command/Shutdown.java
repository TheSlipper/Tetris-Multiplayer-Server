package command;

import management.SessionManager;

import java.util.StringTokenizer;

public class Shutdown extends Command {

    private boolean nowFlag = false;

    private boolean specifiedTimeFlag = false;

    private boolean statusFlag = false;

    private int shutdownTime = 60000;

    private int statusNo = 0;

    public Shutdown(String cmdName) {
        super(cmdName);
    }

    protected void loadFlags(StringTokenizer cmdTokenizer) {
        while (cmdTokenizer.hasMoreTokens()) {
            String token = cmdTokenizer.nextToken();
            if (token.startsWith("-")) {
                if (token.contains("n")) // n - now
                    this.nowFlag = true;
                if (token.contains("t")) { // t - specified time
                    this.specifiedTimeFlag = true;
                    this.shutdownTime = Integer.parseInt(cmdTokenizer.nextToken());
                }
                if (token.contains("s")) { // s - status
                    this.statusFlag = true;
                    this.statusNo = Integer.parseInt(cmdTokenizer.nextToken());
                }
            } else if (token.contains("now")) {
                this.shutdownTime = 1;
            }
        }
    }

    public boolean execute(StringTokenizer cmdTokenizer) {
        this.clearFlags();
        if (cmdTokenizer.hasMoreTokens())
            this.loadFlags(cmdTokenizer);

        if (this.shutdownTime == 1) {
            SessionManager.shutdownSessions();
            System.exit(statusNo);
        }

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        SessionManager.shutdownSessions();
                        System.exit(statusNo);
                    }
                },
                this.shutdownTime
        );

        System.out.println("[Scheduled to shutdown after " + this.shutdownTime/1000 + "s]");

        return true;
    }

    protected void clearFlags() {
        this.nowFlag = false;
        this.specifiedTimeFlag = false;
        this.statusFlag = false;
        this.shutdownTime = 60000;
        this.statusNo = 0;
    }
}
