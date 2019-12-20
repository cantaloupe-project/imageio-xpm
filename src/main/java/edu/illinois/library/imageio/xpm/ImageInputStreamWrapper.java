package edu.illinois.library.imageio.xpm;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps an {@link ImageInputStream}.
 */
final class ImageInputStreamWrapper extends InputStream {

    private ImageInputStream wrappedStream;

    ImageInputStreamWrapper(ImageInputStream wrappedStream) {
        this.wrappedStream = wrappedStream;
    }

    /**
     * N.B.: this method may not work correctly with streams longer than
     * {@link Integer#MAX_VALUE}.
     */
    @Override
    public int available() throws IOException {
        return (int) (wrappedStream.length() -
                wrappedStream.getStreamPosition());
    }

    @Override
    public void close() throws IOException {
        try {
            wrappedStream.close();
        } finally {
            super.close();
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        wrappedStream.mark();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        return wrappedStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return wrappedStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return wrappedStream.read(b, off, len);
    }

    @Override
    public synchronized void reset() throws IOException {
        wrappedStream.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return wrappedStream.skipBytes(n);
    }

}
