package management;

import connection.Match;
import connection.MatchTask;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MatchManager extends Thread {

    private static ArrayList<Match> matches = new ArrayList<Match>();

    private static ArrayList<MatchTask> matchTasks = new ArrayList<MatchTask>();

    private static String[] matchTaskNames = {
            "GAME_SETUP",
            "SEND_OPPONENT_DATA"
    };

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (MatchTask task : MatchManager.matchTasks) {
                if (task.isScheduledForNow() && !task.isBeingAccessed()) {
                    task.setAccessed(true);
                    this.handleTask(task.getTaskId(), task.getTaskName(), task.getTaskContent());
                }
            }
        }
    }

    private void handleTask(int taskId, String taskName, String taskContent) {
        if (taskName.equals(MatchManager.matchTaskNames[0])) { // Game Setup
            int userId = MatchManager.matchTasks.get(taskId).getConcernedUserId();
            ResponseManager.processResponse("GAME_SETUP", userId);
        } else if (taskName.equals(MatchManager.matchTaskNames[1])) { // Send opponent data

        }
        MatchManager.matchTasks.remove(taskId);
    }

    public static void addMatchTask(MatchTask task) {
        MatchManager.matchTasks.add(task);
    }

    public static void addMatchTask(String taskName, String taskContent, long scheduleTimeInPOSIXSeconds) {
        StringTokenizer taskContentTokenized = new StringTokenizer(taskContent);
        // Always keep in mind that when you're adding a task this way you need to specify the concerned user's id
        // in the first argument of the task's content
        MatchTask task = new MatchTask(taskName, taskContent, matchTasks.size(),
                scheduleTimeInPOSIXSeconds, Integer.parseInt(taskContentTokenized.nextToken()));
        MatchManager.addMatchTask(task);
    }

}
