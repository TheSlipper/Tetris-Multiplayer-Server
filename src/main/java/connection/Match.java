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
        if (p1FieldData != null) {
            try {
                p2Session.sendStringData(this.p1FieldData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (p2FieldData != null) {
            try {
                p1Session.sendStringData(this.p2FieldData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateField(int sessionId, String fieldData) {
        if (sessionId == this.p1Session.getSessionId())
            this.p1FieldData = fieldData;
        else
            this.p2FieldData = fieldData;
    }

}
