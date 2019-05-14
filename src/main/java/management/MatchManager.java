package management;

import connection.Match;
import connection.MatchTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MatchManager extends Thread {

    private static ArrayList<Match> matches = new ArrayList<Match>();

    private static ArrayList<MatchTask> matchTasks = new ArrayList<MatchTask>();

    private static HashMap<Integer, Integer> matchQueue = new HashMap<Integer, Integer>();

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
                    break;
                }
            }
        }
    }

    private void handleTask(int taskId, String taskName, String taskContent) {
        if (taskName.equals(MatchManager.matchTaskNames[0])) { // Game Setup
            int sessionId = MatchManager.matchTasks.get(taskId).getConcernedUserId();
            int elo = Integer.parseInt(MatchManager.matchTasks.get(taskId).getTaskContent().substring(1));
            if (MatchManager.matchQueue.isEmpty())
                MatchManager.matchQueue.put(sessionId, elo);
            else {
                int opponentSessionId = -1;
                int opponentElo = -1;
                for (Map.Entry<Integer, Integer> person : MatchManager.matchQueue.entrySet()) {
                    opponentSessionId = person.getKey();
                    opponentElo = person.getValue();
                    break;
                }
                ResponseManager.processResponse("GAME_SETUP " + opponentSessionId + " " +
                        opponentElo, sessionId);
                ResponseManager.processResponse("GAME_SETUP " + sessionId + " " + elo, opponentSessionId);
            }
        } else if (taskName.equals(MatchManager.matchTaskNames[1])) { // Send opponent data

        }
        MatchManager.matchTasks.remove(taskId);
    }

    public static void addMatchTask(MatchTask task) {
        MatchManager.matchTasks.add(task);
    }

    public static void addMatchTask(String taskName, String taskContent, long scheduleTimeInPOSIXSeconds) {
        // Always keep in mind that when you're adding a task this way you need to specify the concerned user's id
        // in the first argument of the task's content
        StringTokenizer taskContentTokenized = new StringTokenizer(taskContent);
        int sessionId = Integer.parseInt(taskContentTokenized.nextToken());
        MatchTask task = new MatchTask(taskName, taskContentTokenized.nextToken(""), matchTasks.size(),
                scheduleTimeInPOSIXSeconds, sessionId);
        MatchManager.addMatchTask(task);
    }

}
