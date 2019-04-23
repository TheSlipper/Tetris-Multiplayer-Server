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

    private void getNewSession() {
        Session session = null;
        try {
            session = new Session(serverSocket, SessionManager.sessions.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        sessions.add(session);
    }

    private static void closeSession(int sessionId) {
        SessionManager.sessions.get(sessionId).closeConnection();
        SessionManager.sessions.set(sessionId, null);
    }

    public static void shutdownSessions() {
        // TODO: Gracefully shutdown all the sessions
        for (Session s : sessions)
            s.closeConnection();
    }

    public static void sendStringData(String data, int id) {
        System.out.println("[Sending this data: " + data + "] TODO: Delete this (SessionManager)");
        try {
            SessionManager.sessions.get(id).sendStringData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[Message sent (SessionManager)]");
    }

    public static String[] getIPs() {
        String[] ipArr = new String[sessions.size()];
        for (int i = 0; i < sessions.size(); i++)
            ipArr[i] = sessions.get(i).getIp();
        return ipArr;
    }

    @Override
    public void run() {
        while (true) {
            this.getNewSession();
            sessions.get(sessions.size()-1).start();
        }
    }
}
