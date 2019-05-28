package management;

import connection.Session;
import connection.UDPServerSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * This class manages all of the connected sessions.
 */
public class SessionManager extends Thread {

    /** Server socket of the server */
    private ServerSocket tcpServerSocket;

    /** Modified socket for simulating ServerSocket behaviour */
    private UDPServerSocket udpServerSocket;

    /** List of active sessions */
    private static ArrayList<Session> sessions = new ArrayList<Session>();

    /** Port of the server */
    private int port;

    /**
     * Default constructor
     *
     * @param usesTcp determines which sockets should be used
     * @param port port
     * @throws IOException on occupied port
     */
    public SessionManager(boolean usesTcp, int port) throws IOException {
        this.setName("SessionManager-Thread");
        this.port = port;
        if (usesTcp)
            this.tcpServerSocket = new ServerSocket(port);
        else
            this.udpServerSocket = new UDPServerSocket(port);
    }

    /**
     * Gets a new session with initialized data
     *
     * @return new initialized session
     */
    private Session getNewSession(int sessionId) {
        Session session = null;
        try {
            if (this.tcpServerSocket == null) {// UDP
                InetAddress addr = this.udpServerSocket.accept(sessionId);
                session = new Session(sessionId, addr,this.port + sessionId + 1);
            }
            else // TCP
                session = new Session(this.tcpServerSocket, sessionId);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return session;
    }

    /**
     * Sets up a new session in the specified sessionId
     *
     * @param sessionId id of the session
     */
    private void setUpNewSession(int sessionId) {
        Session session = null;
        try {
            if (this.tcpServerSocket == null) {
                session = SessionManager.sessions.get(sessionId);
                InetAddress addr = udpServerSocket.accept(sessionId);
                session.connect(sessionId, addr, this.port + sessionId);
            }
            else {
                session = SessionManager.sessions.get(sessionId);
                session.connect(tcpServerSocket, sessionId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        SessionManager.sessions.set(sessionId, session);
        SessionManager.sessions.get(sessionId).start();
    }

    /**
     * Shuts down a session with the given sessionId
     *
     * @param sessionId id of the target session
     */
    public static void shutdownSession(int sessionId) {
        SessionManager.sessions.get(sessionId).closeConnection();
        SessionManager.sessions.set(sessionId, new Session());
    }

    /**
     * Shuts down all of the sessions
     */
    public static void shutdownSessions() {
        for (Session s : sessions) {
            if (s.isConnected())
                SessionManager.sendStringData("SUDDEN_SHUTDOWN", s.getSessionId());
            s.closeConnection();
        }
    }

    /**
     * Sends string data to the session with the specified session id
     *
     * @param data string content of the message
     * @param sessionId id of the target receiver
     */
    public static boolean sendStringData(String data, int sessionId) {
        try {
            return SessionManager.sessions.get(sessionId).sendStringData(data);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all the connected IP addresses
     *
     * @return array of ip addresses in string format
     */
    public static String[] getIPs() {
        String[] ipArr = new String[sessions.size()];
        for (int i = 0; i < sessions.size(); i++)
            ipArr[i] = sessions.get(i).getIp();
        return ipArr;
    }

    /**
     * Assigns tetris multiplayer's database user id to a specific session
     *
     * @param sessionId target session id
     * @param dbId user id
     */
    public static void assignDbId(int sessionId, int dbId) {
        SessionManager.sessions.get(sessionId).setDbUsernameId(dbId);
    }

    /**
     * Gets the specified session
     *
     * @param sessionId target session id
     * @return specified session
     */
    public static Session getSession(int sessionId) {
        return SessionManager.sessions.get(sessionId);
    }

    /**
     * Runs the thread of session manager for handling incoming sessions
     */
    @Override
    public void run() {
        for (int i = 0; i <= SessionManager.sessions.size(); i++) {
            if (SessionManager.sessions.isEmpty() || i == SessionManager.sessions.size()) {
                SessionManager.sessions.add(this.getNewSession(i));
                SessionManager.sessions.get(i).start();
                i = -1;
            } else if (!SessionManager.sessions.get(i).isConnected()) {
                this.setUpNewSession(i);
                i = -1;
            }
        }
    }
}
