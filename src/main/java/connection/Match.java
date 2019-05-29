package connection;

import management.DBQueryManager;
import management.MatchManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class Match {

    private int matchId;

    private boolean isRanked = false;

    private Session p1Session;

    private String p1FieldData;

    private boolean p1Lost = false;

    private int p1Pts;

    private int p1Time;

    private Session p2Session;

    private String p2FieldData;

    private boolean p2Lost = false;

    private int p2Pts;

    private int p2Time;

    public Match(Session p1Session, Session p2Session, int matchId) {
        this.p1Session = p1Session;
        this.p2Session = p2Session;
        this.matchId = matchId;
    }

    public void processData() {
        if (this.p1FieldData != null) {
            if (this.p1FieldData.charAt(0) == 'L' && !this.p1Lost)
                this.sendLossData(this.p1Session.getSessionId(), new StringTokenizer(this.p1FieldData));
            try {
                this.p2Session.sendStringData(this.p1FieldData);
                this.p1FieldData = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.p2FieldData != null) {
            if (this.p2FieldData.charAt(0) == 'L' && !this.p2Lost)
                this.sendLossData(this.p2Session.getSessionId(), new StringTokenizer(this.p2FieldData));
            try {
                this.p1Session.sendStringData(this.p2FieldData);
                this.p2FieldData = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateField(int sessionId, String fieldData) {
        System.out.println("Field Data of session " + sessionId + ": " + fieldData);
        if (sessionId == this.p1Session.getSessionId())
            this.p1FieldData = fieldData.substring(1);
        else
            this.p2FieldData = fieldData.substring(1);
    }


    public void sendLossData(int sessionId, StringTokenizer lossData) {
        lossData.nextToken();
        int pts = Integer.parseInt(lossData.nextToken());
        lossData.nextToken();
        float time = Float.parseFloat(lossData.nextToken());
        if (sessionId == this.p1Session.getSessionId()) {
            this.p1Pts = pts;
            this.p1Time = (int)time;
            this.p1Lost = true;
        } else {
            this.p2Pts = pts;
            this.p2Time = (int)time;
            this.p2Lost = true;
        }

        if (this.p1Lost && this.p2Lost)
           this.finalizeMatch();
    }

    private void finalizeMatch() {
        // TODO: Normal ELO calculation
        int p1Elo = this.p1Session.getElo(), p2Elo = this.p2Session.getElo();
        long p1TetrominoPts = this.p1Session.getTetrominoPoints(), p2TetrominoPts = this.p2Session.getTetrominoPoints();
        long p1FullTime = this.p1Session.getTimePlayed(), p2FullTime = this.p2Session.getTimePlayed();
        int p1UnrankedWins = this.p1Session.getUnrankedWins(), p2UnrankedWins = this.p2Session.getUnrankedWins();
        int p1UnrankedLosses = this.p1Session.getUnrankedLosses(), p2UnrankedLosses = this.p2Session.getUnrankedLosses();
        int p1RankedWins = this.p1Session.getRankedWins(), p2RankedWins = this.p2Session.getRankedWins();
        int p1RankedLosses = this.p1Session.getRankedLosses(), p2RankedLosses = this.p2Session.getRankedLosses();
        int p1Id = this.p1Session.getDbUserNameId(), p2Id = this.p2Session.getDbUserNameId();

        if (this.p1Pts > this.p2Pts) {
            if (this.isRanked) {
                p1Elo += 20;
                p2Elo += -20;
                p1RankedWins += 1;
                p2RankedLosses += 1;
            } else {
                p1UnrankedWins += 1;
                p2UnrankedLosses += 1;
            }
        } else if (this.p2Pts > this.p1Pts) {
            if (this.isRanked) {
                p1Elo += -20;
                p2Elo += 20;
                p1RankedLosses += 1;
                p2RankedWins += 1;
            } else {
                p1UnrankedLosses += 1;
                p2UnrankedWins += 1;
            }
        }

        p1TetrominoPts += p1Pts;
        p2TetrominoPts += p2Pts;
        p1FullTime += this.p1Time;
        p2FullTime += this.p2Time;

        try {
            DBQueryManager.runSQLueryNoRet("UPDATE `user_game_data` SET  `tetromino_points` = '" + p1TetrominoPts + "',`time_played` = '" + p1FullTime + "', `unranked_wins` = '" + p1UnrankedWins
                    + "', `unranked_losses` = '" + p1UnrankedLosses + "', `ranked_wins` = '" + p1RankedWins + "', `ranked_losses` = '" + p1RankedLosses
                    + "', `elo` = '" + p1Elo + "' WHERE `user_game_data`.`user_data_id` = " + p1Id);
            DBQueryManager.runSQLueryNoRet("UPDATE `user_game_data` SET  `tetromino_points` = '" + p2TetrominoPts + "',`time_played` = '" + p2FullTime + "', `unranked_wins` = '" + p2UnrankedWins
                    + "', `unranked_losses` = '" + p2UnrankedLosses + "', `ranked_wins` = '" + p2RankedWins + "', `ranked_losses` = '" + p2RankedLosses
                    + "', `elo` = '" + p2Elo + "' WHERE `user_game_data`.`user_data_id` = " + p2Id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        p1Session.setElo(p1Elo);
        p2Session.setElo(p2Elo);
        p1Session.setTetrominoPoints(p1TetrominoPts);
        p2Session.setTetrominoPoints(p2TetrominoPts);
        p1Session.setTimePlayed(p1FullTime);
        p2Session.setTimePlayed(p2FullTime);
        p1Session.setUnrankedWins(p1UnrankedWins);
        p2Session.setUnrankedWins(p2UnrankedWins);
        p1Session.setUnrankedLosses(p1UnrankedLosses);
        p2Session.setUnrankedLosses(p2UnrankedLosses);
        p1Session.setRankedWins(p1UnrankedWins);
        p2Session.setRankedWins(p2UnrankedWins);
        p1Session.setRankedLosses(p1UnrankedLosses);
        p2Session.setRankedLosses(p2UnrankedLosses);

        MatchManager.addMatchTask("GAME_FINISH", p1Id + " " + Integer.toString(matchId), System.currentTimeMillis() / 1000);
    }

    public int getP1Pts() {
        return p1Pts;
    }

    public int getP2Pts() {
        return p2Pts;
    }

    public int getP1Time() {
        return p1Time;
    }

    public int getP2Time() {
        return p2Time;
    }

    public Session getP1Session() {
        return p1Session;
    }

    public Session getP2Session() {
        return p2Session;
    }
}
