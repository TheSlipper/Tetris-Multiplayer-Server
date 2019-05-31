package management;

import connection.Match;
import connection.MatchTask;
import connection.Session;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class manages all of the game's match related data: match queueing, handling match tasks, sending player's tetris field information and match disconnection.
 *
 */
public class MatchManager extends Thread {

    /** List of currently ongoing game matches */
    private static ArrayList<Match> matches = new ArrayList<Match>();

    /** List of currently scheduled match tasks */
    private static ArrayList<MatchTask> matchTasks = new ArrayList<MatchTask>();

    /** List of users waiting for a match */
    private static HashMap<Integer, Integer> matchQueue = new HashMap<Integer, Integer>();

    /** List of match related communication codes */
    private final static String[] MATCH_TASK_NAMES = {
            "GAME_SETUP",
            "GAME_FINISH"
    };

    /**
     * Runs the thread of match manager class that is responsible for handling match tasks and for processing match data in the respective order
     */
    @Override
    public void run() {
        while (true) {
            if (matches.isEmpty()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int taskId = 0;
            for (MatchTask task : MatchManager.matchTasks) {
                if (task.isScheduledForNow() && !task.isBeingAccessed()) {
                    task.setAccessed(true);
                    this.handleTask(taskId, task.getTaskName(), task.getTaskContent());
                    break;
                }
                else
                    taskId++;
            }

            for (Match match : MatchManager.matches)
                match.processData();
        }
    }

    /**
     * Sets up a match with two given session ids
     *
     * @param sessionId player 1's session id
     * @param opponentSessionId player 2's session id
     * @throws SQLException on incorrectly initialized player's database id
     */
    private void setUpMatch(int sessionId, int opponentSessionId) throws SQLException {
        StringBuilder sb1 = new StringBuilder(), sb2 = new StringBuilder();
        ResultSet rs1, rs2;
        String opponentDBSessionId = Integer.toString(SessionManager.getSession(opponentSessionId).getDbUserNameId());
        String sessionDBId = Integer.toString(SessionManager.getSession(sessionId).getDbUserNameId());
        int matchId = matches.size();
        Session s1 = SessionManager.getSession(sessionId), s2 = SessionManager.getSession(opponentSessionId);
        s1.setMatchId(matchId);
        s2.setMatchId(matchId);

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
        sb1.append(matchId);
        sb2.append(matchId);

        ResponseManager.processResponse(sb2.toString(), sessionId);
        ResponseManager.processResponse(sb1.toString(), opponentSessionId);

        matches.add(new Match(s1, s2, matches.size()));
    }

    /**
     * Handles a scheduled match task
     *
     * @param taskId id of the scheduled task
     * @param taskName name of the task
     * @param taskContent passed arguments/information about the task
     */
    private void handleTask(int taskId, String taskName, String taskContent) {
        if (taskName.equals(MatchManager.MATCH_TASK_NAMES[0])) /* Game Setup */ {
            try {
                this.setupGame(taskId);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if (taskName.equals(MatchManager.MATCH_TASK_NAMES[1])) /* GAME_FINISH */ {
            try {
                this.gameFinish(taskContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MatchManager.matchTasks.remove(taskId);
    }

    /**
     * Adds a new match task to the scheduled task list
     *
     * @param task task object
     */
    public static void addMatchTask(MatchTask task) {
        MatchManager.matchTasks.add(task);
    }

    /**
     * Creates and adds a match to the match task queue with the specified data
     *
     * @param taskName name of the task
     * @param taskContent content/arguments of the task; <b>The first argument of taskContent must be the concerned user's ID</b>
     * @param scheduleTimeInPOSIXSeconds scheduled POSIX time of task execution
     */
    public static void addMatchTask(String taskName, String taskContent, long scheduleTimeInPOSIXSeconds) {
        // Always keep in mind that when you're adding a task this way you need to specify the concerned user's id
        // in the first argument of the task's content
        StringTokenizer taskContentTokenized = new StringTokenizer(taskContent);
        int sessionId = Integer.parseInt(taskContentTokenized.nextToken());
        MatchTask task = new MatchTask(taskName, taskContentTokenized.nextToken(""), scheduleTimeInPOSIXSeconds, sessionId);
        MatchManager.addMatchTask(task);
    }

    /**
     * Adds tetris field data to the specified match
     *
     * @param matchId id of the match
     * @param sessionId session id of the client that the field originated from
     * @param fieldData field data in string format
     */
    public static void addFieldData(int matchId, int sessionId, String fieldData) {
        MatchManager.matches.get(matchId).updateField(sessionId, fieldData);
    }

    /**
     * Executes the GAME_SETUP task
     *
     * @param taskId id of the task
     */
    private void setupGame(int taskId) throws SQLException, InterruptedException {
        int sessionId = MatchManager.matchTasks.get(taskId).getConcernedUserId();
        int elo = Integer.parseInt(MatchManager.matchTasks.get(taskId).getTaskContent().substring(1));
        if (MatchManager.matchQueue.isEmpty()) {
            MatchManager.matchQueue.put(sessionId, elo);
        }else {
            int opponentSessionId = -1;
            int opponentElo = -1;
            for (Map.Entry<Integer, Integer> person : MatchManager.matchQueue.entrySet()) {
                opponentSessionId = person.getKey();
                opponentElo = person.getValue();
                break;
            }
            this.setUpMatch(sessionId, opponentSessionId);
        }
    }

    /**
     * Gets a StringBuilder object with a formatted endgame message
     *
     * @param session1 first game session
     * @param session2 second game session
     * @param match match data
     * @return formatted endgame message
     */
    private StringBuilder getGameFinishMsg(Session session1, Session session2, Match match) {
        StringBuilder builder = new StringBuilder();

        if ((session1.getDbUserNameId() == match.getP1Session().getDbUserNameId() && match.getP1Pts() > match.getP2Pts())
            || (session1.getDbUserNameId() == match.getP2Session().getDbUserNameId() && match.getP2Pts() > match.getP1Pts())) {
            builder.append("GAME_WON ");
        } else if (match.getP1Pts() == match.getP2Pts()) {
            builder.append("GAME_DRAW ");
        } else {
            builder.append("GAME_LOST ");
        }

        builder.append(session1.getElo() + " " + session2.getElo() + " ");
        builder.append(session1.getUnrankedWins() + " " + session2.getUnrankedWins() + " ");
        builder.append(session1.getUnrankedLosses() + " " + session2.getUnrankedLosses() + " ");
        builder.append(session1.getRankedWins() + " " + session2.getRankedWins() + " ");
        builder.append(session1.getRankedLosses() + " " + session2.getRankedLosses() + " ");
        builder.append(session1.getTetrominoPoints() + " " + session2.getTetrominoPoints() + " ");
        builder.append(session1.getTimePlayed() + " " + session2.getTimePlayed() + " ");

        if (match.getP1Session().getDbUserNameId() == session1.getDbUserNameId()) {
            builder.append(match.getP1Pts() + " " + match.getP2Pts() + " ");
            builder.append(match.getP1Time() + " " + match.getP2Time() + " ");
        } else {
            builder.append(match.getP2Pts() + " " + match.getP1Pts() + " ");
            builder.append(match.getP2Time() + " " + match.getP1Time() + " ");
        }
        return builder;
    }

    /**
     * Executes the GAME_FINISH task
     *
     * @param taskContent content of the task
     */
    private void gameFinish(String taskContent) throws IOException {
        final int matchId = Integer.parseInt(new StringTokenizer(taskContent).nextToken());
        Match match = MatchManager.matches.get(matchId);
        Session s1 = match.getP1Session(), s2 = match.getP2Session();

        s1.sendStringData(this.getGameFinishMsg(s1, s2, match).toString());
        s2.sendStringData(this.getGameFinishMsg(s2, s1, match).toString());

        match = null;
        matches.remove(matchId);
    }
}
