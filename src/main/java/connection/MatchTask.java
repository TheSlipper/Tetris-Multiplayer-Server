package connection;

public class MatchTask {

    private String taskName;

    private String taskContent;

    private long scheduleTimeInPOSIXSeconds;

    private int concernedUserId;

    private boolean accessed = false;

    public MatchTask(String taskName, String taskContent, long scheduleTimeInPOSIXSeconds, int userId) {
        this.taskName = taskName;
        this.taskContent = taskContent;
        this.scheduleTimeInPOSIXSeconds = scheduleTimeInPOSIXSeconds;
        this.concernedUserId = userId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public boolean isScheduledForNow() {
        return this.scheduleTimeInPOSIXSeconds < System.currentTimeMillis() / 1000;
    }

    public int getConcernedUserId() {
        return concernedUserId;
    }

    public void setAccessed(boolean val) {
        this.accessed = val;
    }

    public boolean isBeingAccessed() {
        return this.accessed;
    }
}
