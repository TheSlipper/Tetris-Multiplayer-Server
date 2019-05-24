package connection;

import java.io.IOException;

public class Match {

    private Session p1Session;

    private String p1FieldData;

    private Session p2Session;

    private String p2FieldData;

    public Match(Session p1Session, Session p2Session) {
        this.p1Session = p1Session;
        this.p2Session = p2Session;
    }

    public void processData() {
        if (this.p1FieldData != null) {
            try {
                System.out.println("Player 1's field data: '" + this.p1FieldData + "'");
                p2Session.sendStringData(this.p1FieldData);
                this.p1FieldData = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.p2FieldData != null) {
            try {
                System.out.println("Player 2's field data: '" + this.p2FieldData + "'");
                p1Session.sendStringData(this.p2FieldData);
                this.p2FieldData = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateField(int sessionId, String fieldData) {
        if (sessionId == this.p1Session.getSessionId())
            this.p1FieldData = fieldData.substring(1);
        else
            this.p2FieldData = fieldData.substring(1);
    }

}
