package com.historycraft.launcher;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class CustomProgressMonitorInputStream  extends FilterInputStream {

    private int             nread = 0;
    private int             size = 0;

    private ProgressionFrame monitor;

    public CustomProgressMonitorInputStream(ProgressionFrame progressionFrame, InputStream in) {
        super(in);
        try {
            size = in.available();
        }
        catch(IOException ioe) {
            size = 0;
        }
        this.monitor = progressionFrame;
    }

    /**
     * Overrides <code>FilterInputStream.read</code>
     * to update the progress monitor after the read.
     */
    public int read() throws IOException {
        int c = in.read();
        if (c >= 0) monitor.setProgress(++nread);

        if (monitor.isCanceled()) {
            InterruptedIOException exc =
                    new InterruptedIOException("progress");
            exc.bytesTransferred = nread;
            throw exc;
        }
        return c;
    }


    /**
     * Overrides <code>FilterInputStream.read</code>
     * to update the progress monitor after the read.
     */
    public int read(byte b[]) throws IOException {
        int nr = in.read(b);
        if (nr > 0) monitor.setProgress(nread += nr);
        if (monitor.isCanceled()) {
            InterruptedIOException exc =
                    new InterruptedIOException("progress");
            exc.bytesTransferred = nread;
            throw exc;
        }
        return nr;
    }


    /**
     * Overrides <code>FilterInputStream.read</code>
     * to update the progress monitor after the read.
     */
    public int read(byte b[],
                    int off,
                    int len) throws IOException {
        int nr = in.read(b, off, len);
        if (nr > 0) monitor.setProgress(nread += nr);
        if (monitor.isCanceled()) {
            InterruptedIOException exc =
                    new InterruptedIOException("progress");
            exc.bytesTransferred = nread;
            throw exc;
        }
        return nr;
    }


    /**
     * Overrides <code>FilterInputStream.skip</code>
     * to update the progress monitor after the skip.
     */
    public long skip(long n) throws IOException {
        long nr = in.skip(n);
        if (nr > 0) monitor.setProgress(nread += nr);
        return nr;
    }


    /**
     * Overrides <code>FilterInputStream.close</code>
     * to close the progress monitor as well as the stream.
     */
    public void close() throws IOException {
        in.close();
    }


    /**
     * Overrides <code>FilterInputStream.reset</code>
     * to reset the progress monitor as well as the stream.
     */
    public synchronized void reset() throws IOException {
        in.reset();
        nread = size - in.available();
        monitor.setProgress(nread);
    }
}
