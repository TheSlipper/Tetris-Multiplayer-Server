package management;

import connection.Match;
import connection.MatchTask;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    private void setUpMatch(int sessionId, int opponentSessionId) throws SQLException {
        StringBuilder sb1 = new StringBuilder(), sb2 = new StringBuilder();
        ResultSet rs1, rs2;
        String opponentDBSessionId = Integer.toString(SessionManager.getSession(opponentSessionId).getDbUserNameId());
        String sessionDBId = Integer.toString(SessionManager.getSession(sessionId).getDbUserNameId());
        rs1 = DBQueryManager.runSQLQuerry("SELECT * FROM `TetrisMP`.`users`, `TetrisMP`.`user_game_data`" +
            " WHERE user_game_data.user_id=" + sessionDBId + " AND " + "users.user_id=" + sessionDBId);
        rs2 = DBQueryManager.runSQLQuerry("SELECT * FROM `TetrisMP`.`users`, `TetrisMP`.`user_game_data`" +
                " WHERE user_game_data.user_id=" + opponentDBSessionId + " AND " + "users.user_id=" + opponentDBSessionId);
        rs1.next();
        rs2.next();
        sb1.append("GAME_SETUP ");
        sb2.append("GAME_SETUP ");
        sb1.append(rs1.getString("elo") + " ");
        sb2.append(rs2.getString("elo") + " ");
        sb1.append(rs1.getString("privilege_group") + " ");
        sb2.append(rs2.getString("privilege_group") + " ");
        sb1.append(rs1.getString("unranked_wins") + " ");
        sb2.append(rs2.getString("unranked_wins") + " ");
        sb1.append(rs1.getString("unranked_losses") + " ");
        sb2.append(rs2.getString("unranked_losses") + " ");
        sb1.append(rs1.getString("ranked_wins") + " ");
        sb2.append(rs2.getString("ranked_wins") + " ");
        sb1.append(rs1.getString("ranked_losses") + " ");
        sb2.append(rs2.getString("ranked_losses") + " ");
        sb1.append(rs1.getString("tetromino_points") + " ");
        sb2.append(rs2.getString("tetromino_points") + " ");
        sb1.append(rs1.getString("time_played") + " ");
        sb2.append(rs2.getString("time_played") + " ");
        sb1.append(rs1.getString("username") + " ");
        sb2.append(rs2.getString("username") + " ");

        ResponseManager.processResponse(sb2.toString(), sessionId);
        ResponseManager.processResponse(sb1.toString(), opponentSessionId);
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
                try {
                    this.setUpMatch(sessionId, opponentSessionId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
