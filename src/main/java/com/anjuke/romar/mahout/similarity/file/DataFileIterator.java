/**
 * Copyright 2012 Anjuke Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private final DataInputStream _inputStream;
    private final int _dataSize;

    public DataFileIterator(File file, int dataSize) throws IOException {
        this(getFileInputStream(file), dataSize);
    }

    public DataFileIterator(InputStream is, int dataSize) throws IOException {
        this._inputStream = new DataInputStream(is);
        this._dataSize = dataSize;

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
        byte[] data = new byte[_dataSize];
        try {
            _inputStream.readFully(data);
        } catch (EOFException eof) {
            close();
            data = null;
        } catch (IOException ioe) {
            close();
            throw new IllegalStateException(ioe);
        }
        return data;
    }

    @Override
    public void close() {
        endOfData();
        Closeables.closeQuietly(_inputStream);
    }

}
