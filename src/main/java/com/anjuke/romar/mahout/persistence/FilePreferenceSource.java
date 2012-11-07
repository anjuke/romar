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
package com.anjuke.romar.mahout.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.http.jetty.JettyRomarHandler;
import com.anjuke.romar.mahout.util.Util;

public class FilePreferenceSource extends AbstractFilePreferenceSource
        implements PreferenceSource {
    private static final Logger LOG = LoggerFactory
            .getLogger(JettyRomarHandler.class);
    private PrintWriter _writer;
    private long _logCount = 0;
    private final Object _snapshotWriterLock = new Object();

    public FilePreferenceSource(File path) {
        super(path);
        _writer = createWriter();
    }

    @Override
    public void setPreference(long userID, long itemID, float value) {
        synchronized (this) {
            _logCount++;
            _writer.print(userID);
            _writer.print(',');
            _writer.print(itemID);
            _writer.print(',');
            _writer.println(value);
            _writer.flush();
        }
    }

    @Override
    public void removePreference(long userID, long itemID) {
        synchronized (this) {
            _logCount++;
            _writer.print(userID);
            _writer.print(',');
            _writer.print(itemID);
            _writer.println(',');
            _writer.flush();
        }
    }

    @Override
    public void removePreferenceByUserId(long userID) {
        // FIXME
        throw new UnsupportedOperationException();

    }

    @Override
    public void removePreferenceByItemId(long itemID) {
        // FIXME
        throw new UnsupportedOperationException();
    }

    @Override
    public void commit() {
        synchronized (this) {
            if (_logCount > 0) {
                _writer.flush();
                _writer.close();
                _writer = createWriter();
                _logCount = 0;
            }
        }
    }

    private PrintWriter createWriter() {
        File file = createNewLogFile();
        return createWriter(file);
    }

    private static PrintWriter createWriter(File file) {
        try {

            OutputStream os = new FileOutputStream(file);
            // String name = file.getName();
            // if (name.endsWith(".gz")) {
            // try {
            // os=new GZIPOutputStream(os);
            // } catch (IOException e) {
            // throw new RuntimeException(e);
            // }
            // }

            return new PrintWriter(os);
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private static BufferedReader createReader(File file) {
        try {
            InputStream is = new FileInputStream(file);
            String name = file.getName();
            if (name.endsWith(".gz")) {
                is = new GZIPInputStream(is);
            }
            return new BufferedReader(new InputStreamReader(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void compact() {
        // 避免过长的时间持有对象锁
        final long version = getCurrentVersion() - 1;
        if (version < 0) {
            return;
        }
        File latestSnapshotFile = getLatestSnapshotFile();
        if (latestSnapshotFile != null
                && getSnapshotFileVersion(latestSnapshotFile) == version) {
            return;
        }

        synchronized (_snapshotWriterLock) {
            PrintWriter snapshotWriter = createWriter(getSnapshotFile(version));
            try {
                final List<File> logFileList = getLogFileListUntilVersion(version);
                List<File> fileToIt = new ArrayList<File>(logFileList);
                if (latestSnapshotFile != null) {
                    fileToIt.add(0, latestSnapshotFile);
                }
                PreferenceIterator it = new LogFileIterator(fileToIt);
                FastByIDMap<PreferenceArray> data = new FastByIDMap<PreferenceArray>();
                while (it.hasNext()) {
                    Preference pref = it.next();
                    if (it.getType() == PreferenceType.ADD) {
                        Util.applyAdd(data, pref);
                    } else if (it.getType() == PreferenceType.DELETE) {
                        Util.applyRemove(data, pref);
                    }
                }

                for (Entry<Long, PreferenceArray> entry : data.entrySet()) {
                    PreferenceArray array = entry.getValue();
                    for (int i = 0, length = array.length(); i < length; i++) {
                        long userID = array.getUserID(i);
                        long itemID = array.getItemID(i);
                        float value = array.getValue(i);
                        snapshotWriter.print(userID);
                        snapshotWriter.print(',');
                        snapshotWriter.print(itemID);
                        snapshotWriter.print(',');
                        snapshotWriter.println(value);
                    }
                }

                snapshotWriter.flush();
                snapshotWriter.close();
                removeFile();
            } finally {
                snapshotWriter.close();
            }
        }
    }

    private void removeFile() {
        List<File> snapshotFiles = listSnapshotFileNamesAndSorted();
        if (snapshotFiles.size() < 2) {
            return;
        }
        long version = -1;
        for (int i = 0, length = snapshotFiles.size(); i < length - 2; i++) {
            File file = snapshotFiles.get(i);
            version = getSnapshotFileVersion(file);
            file.delete();
        }

        if (version > 0) {
            List<File> logs = getLogFileListUntilVersion(version);
            for (File file : logs) {
                file.delete();
            }

        }

    }

    private static class LogFileIterator implements PreferenceIterator {
        private Iterator<File> _fileIt;

        private BufferedReader _currentReader = null;
        private Preference _preference;
        private PreferenceType _type;

        public LogFileIterator(List<File> list) {
            super();
            _fileIt = list.iterator();
        }

        BufferedReader createReader() {
            if (!_fileIt.hasNext()) {
                return null;
            } else {
                // create reader
                File file = _fileIt.next();
                LOG.info("read file " + file.getAbsolutePath());
                return FilePreferenceSource.createReader(file);
            }
        }

        @Override
        public boolean hasNext() {
            if (_currentReader == null) {
                _currentReader = createReader();
                if (_currentReader == null) {
                    return false;
                }
            }

            String line;
            try {
                while ((line = _currentReader.readLine()) == null) {
                    close();
                    _currentReader = createReader();
                    if (_currentReader == null) {
                        return false;
                    }
                }
            } catch (IOException e) {
                close();
                throw new RuntimeException(e);
            }
            String[] tmp = line.split(",");
            long userID = Long.parseLong(tmp[0]);
            long itemID = Long.parseLong(tmp[1]);
            float value;
            if (tmp.length == 2 || "".equals(tmp[2])) {
                value = 0;
                _type = PreferenceType.DELETE;
            } else {
                value = Float.parseFloat(tmp[2]);
                _type = PreferenceType.ADD;
            }
            _preference = new GenericPreference(userID, itemID, value);
            return true;
        }

        @Override
        public Preference next() {
            if (_preference == null) {
                throw new NoSuchElementException();
            }
            return _preference;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PreferenceType getType() {
            return _type;
        }

        void close() {
            if (_currentReader != null) {
                try {
                    _currentReader.close();
                } catch (IOException e) {
                    LOG.info(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void close() {
        _writer.close();
    }

    @Override
    public FastByIDMap<PreferenceArray> getPreferenceUserData() {
        File snapshotFile = getLatestSnapshotFile();
        long version;
        if (snapshotFile == null) {
            version = -1;
        } else {
            version = getSnapshotFileVersion(snapshotFile);
        }
        final List<File> list = new ArrayList<File>(
                getLogFileListFromVersion(version));
        if (snapshotFile != null) {
            list.add(0, snapshotFile);
        }
        PreferenceIterator it = new LogFileIterator(list);
        FastByIDMap<PreferenceArray> data = new FastByIDMap<PreferenceArray>();
        while (it.hasNext()) {
            Preference pref = it.next();
            if (it.getType() == PreferenceType.ADD) {
                Util.applyAdd(data, pref);
            } else if (it.getType() == PreferenceType.DELETE) {
                Util.applyRemove(data, pref);
            }
        }
        return data;
    }
}
