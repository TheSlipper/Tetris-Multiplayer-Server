package misc;

import java.io.OutputStream;

public class SessionOutputStream extends java.io.BufferedOutputStream {

    public SessionOutputStream(OutputStream out) {
        super(out);
    }

    public SessionOutputStream(OutputStream out, int size) {
        super(out, size);
    }

    /** throw away everything in a buffer without writing it */
    public synchronized void skip() {
//        count = 0;
        this.count = 0;
    }

}
