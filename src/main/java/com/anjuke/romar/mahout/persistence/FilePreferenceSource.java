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
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
    private static final Logger log = LoggerFactory
            .getLogger(JettyRomarHandler.class);
    private PrintWriter writer;
    private long logCount = 0;
    private final Object snapshotWriterLock = new Object();

    public FilePreferenceSource(File path) {
        super(path);
        writer = createWriter();
    }

    @Override
    public void setPreference(long userID, long itemID, float value) {
        synchronized (this) {
            logCount++;
            writer.print(userID);
            writer.print(',');
            writer.print(itemID);
            writer.print(',');
            writer.println(value);
            writer.flush();
        }
    }

    @Override
    public void removePreference(long userID, long itemID) {
        synchronized (this) {
            logCount++;
            writer.print(userID);
            writer.print(',');
            writer.print(itemID);
            writer.println(',');
            writer.flush();
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
            if (logCount > 0) {
                writer.flush();
                writer.close();
                writer = createWriter();
                logCount = 0;
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
        if (version < 0)
            return;
        File latestSnapshotFile = getLatestSnapshotFile();
        if (latestSnapshotFile != null
                && getSnapshotFileVersion(latestSnapshotFile) == version)
            return;

        synchronized (snapshotWriterLock) {
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


    private void removeFile(){
        List<File> snapshotFiles=listSnapshotFileNamesAndSorted();
        if(snapshotFiles.size()<2)
            return;
        long version=-1;
        for(int i=0,length=snapshotFiles.size();i<length-2;i++){
            File file=snapshotFiles.get(i);
            version=getSnapshotFileVersion(file);
            file.delete();
        }

        if(version>0){
            List<File> logs=getLogFileListUntilVersion(version);
            for(File file:logs){
                file.delete();
            }

        }


    }


    static class LogFileIterator implements PreferenceIterator {
        List<File> list;
        Iterator<File> fileIt;

        public LogFileIterator(List<File> list) {
            super();
            this.list = list;
            fileIt = list.iterator();
        }

        BufferedReader currentReader = null;
        Preference preference;
        PreferenceType type;

        BufferedReader createReader() {
            if (!fileIt.hasNext()) {
                return null;
            } else {
                // create reader
                File file = fileIt.next();
                log.info("read file " + file.getAbsolutePath());
                return FilePreferenceSource.createReader(file);
            }
        }

        @Override
        public boolean hasNext() {
            if (currentReader == null) {
                currentReader = createReader();
                if (currentReader == null)
                    return false;
            }

            String line;
            try {
                while ((line = currentReader.readLine()) == null) {
                    close();
                    currentReader = createReader();
                    if (currentReader == null)
                        return false;
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
                type = PreferenceType.DELETE;
            } else {
                value = Float.parseFloat(tmp[2]);
                type = PreferenceType.ADD;
            }
            preference = new GenericPreference(userID, itemID, value);
            return true;
        }

        @Override
        public Preference next() {
            if (preference == null) {
                throw new NoSuchElementException();
            }
            return preference;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PreferenceType getType() {
            return type;
        }

        void close() {
            if (currentReader != null) {
                try {
                    currentReader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void close() {
        writer.close();
    }

    @Override
    public FastByIDMap<PreferenceArray> getPreferenceUserData() {
        File snapshotFile = getLatestSnapshotFile();
        long version = snapshotFile == null ? 0
                : getSnapshotFileVersion(snapshotFile);
        final List<File> list = new ArrayList<File>(
                getLogFileListFromVersion(version));
        if (snapshotFile != null)
            list.add(0, snapshotFile);
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
