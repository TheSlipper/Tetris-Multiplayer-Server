package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class SessionManager extends Thread {

    private ServerSocket serverSocket;

    private static ArrayList<Session> sessions = new ArrayList<Session>();

    public SessionManager(String ipAddr, int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    private Session getNewSession() {
        Session session = null;
        try {
            session = new Session(serverSocket, SessionManager.sessions.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return session;
    }

    public static void shutdownSession(int sessionId) {
        SessionManager.sessions.get(sessionId).closeConnection();
        SessionManager.sessions.set(sessionId, new Session());
    }

    public static void shutdownSessions() {
        // TODO: Gracefully shutdown all the sessions
        for (Session s : sessions)
            s.closeConnection();
    }

    public static void sendStringData(String data, int id) {
        try {
            SessionManager.sessions.get(id).sendStringData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getIPs() {
        String[] ipArr = new String[sessions.size()];
        for (int i = 0; i < sessions.size(); i++)
            ipArr[i] = sessions.get(i).getIp();
        return ipArr;
    }

    @Override
    public void run() {
        for (int i = 0; i <= SessionManager.sessions.size(); i++) {
            if (i == SessionManager.sessions.size()) {
                SessionManager.sessions.add(this.getNewSession());
                SessionManager.sessions.get(i).start();
            } else if (!SessionManager.sessions.get(i).isConnected()) {
                try {
                    SessionManager.sessions.get(i).connect(serverSocket, i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SessionManager.sessions.get(i).start();
            }
        }
    }
}
