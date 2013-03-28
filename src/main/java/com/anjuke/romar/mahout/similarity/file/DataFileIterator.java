package com.anjuke.romar.mahout.similarity.file;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import com.google.common.collect.AbstractIterator;
import com.google.common.io.Closeables;

public class DataFileIterator extends AbstractIterator<byte[]> implements Closeable {

    private final DataInputStream inputStream;
    private final int dataSize;

    public DataFileIterator(File file, int dataSize) throws IOException {
        this(getFileInputStream(file), dataSize);
    }

    public DataFileIterator(InputStream is, int dataSize) throws IOException {
        this.inputStream = new DataInputStream(is);
        this.dataSize = dataSize;

    }

    static InputStream getFileInputStream(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        String name = file.getName();
        if (name.endsWith(".gz")) {
            return new GZIPInputStream(is);
        } else if (name.endsWith(".zip")) {
            return new ZipInputStream(is);
        } else {
            return is;
        }
    }

    @Override
    protected byte[] computeNext() {
        byte[] data = new byte[dataSize];
        try {
            inputStream.readFully(data);
        } catch (EOFException eof) {
            close();
            data=null;
        } catch (IOException ioe) {
            close();
            throw new IllegalStateException(ioe);
        }
        return data;
    }

    @Override
    public void close() {
        endOfData();
        Closeables.closeQuietly(inputStream);
    }

}
