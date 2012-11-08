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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractFilePreferenceSource implements PreferenceSource {
    private final File _path;
    public static final String LOG_FILE_PREFIX = "romar.log.";
    public static final String SNAPSHOT_FILE = "romar.snapshot.";
    private long _version;

    public AbstractFilePreferenceSource(File path) {
        if (!path.exists()) {
            if (!path.mkdirs()) {
                throw new IllegalStateException("cannot mkdirs on _path "
                        + path.getAbsolutePath());
            }
        } else if (!path.isDirectory()) {
            throw new IllegalStateException(path.getAbsolutePath() + " must be directory");
        }
        _path = path;
        verifyNewestVersion();
    }

    private synchronized void verifyNewestVersion() {
        List<File> logFileList = listLogFileNamesAndSorted();
        if (logFileList.isEmpty()) {
            _version = -1;
        } else {
            _version = getInitFileVersion(logFileList.get(logFileList.size() - 1));
        }
    }

    protected File getSnapshotFile(long version) {
        File tmpFile = new File(_path, SNAPSHOT_FILE + version);
        return tmpFile;
    }

    protected synchronized File getLatestLogFile() {
        List<File> list = listLogFileNamesAndSorted();
        if (list.isEmpty()) {
            return createNewLogFile();
        } else {
            return list.get(list.size() - 1);
        }
    }

    protected synchronized File createNewLogFile() {
        _version++;
        File file = new File(_path, LOG_FILE_PREFIX + _version);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    protected synchronized File getLatestSnapshotFile() {
        List<File> list = listSnapshotFileNamesAndSorted();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    protected synchronized long getCurrentVersion() {
        return _version;
    }

    protected List<File> listSnapshotFileNamesAndSorted() {

        File[] files = _path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(SNAPSHOT_FILE);
            }
        });
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return Double.compare(getSnapshotFileVersion(f1),
                        getSnapshotFileVersion(f2));
            }
        });
        return Arrays.asList(files);

    }

    protected List<File> listLogFileNamesAndSorted() {
        File[] files = _path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(LOG_FILE_PREFIX);
            }
        });
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return Double.compare(getLogFileVersion(f1), getLogFileVersion(f2));
            }
        });
        return Arrays.asList(files);
    }

    private long getInitFileVersion(File file){
        long version=getLogFileVersion(file);
         if (file.length() == 0) {
             version = version - 1;
         }
         return version;
    }

    protected long getLogFileVersion(File file) {
        String name = file.getName();
        long version = Long.parseLong(name.substring(LOG_FILE_PREFIX.length()));
        return version;
    }

    protected long getSnapshotFileVersion(File file) {
        String name = file.getName();
        return Long.parseLong(name.substring(SNAPSHOT_FILE.length()));
    }

    /**
     * include version
     *
     * @param version
     * @return
     */
    protected List<File> getLogFileListUntilVersion(long version) {
        if (version < 0) {
            return Collections.emptyList();
        }
        List<File> list = listLogFileNamesAndSorted();
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        final List<File> logFileList = new ArrayList<File>(list.size() - 1);

        for (File file : list) {
            long fileVersion = getLogFileVersion(file);
            if (fileVersion > version) {
                break;
            }
            logFileList.add(file);
        }
        return logFileList;
    }

    protected List<File> getLogFileListFromVersion(long version) {
        List<File> list = listLogFileNamesAndSorted();
        final List<File> logFileList = new ArrayList<File>(list.size() - 1);

        for (File file : list) {
            long fileVersion = getLogFileVersion(file);
            if (fileVersion <= version) {
                continue;
            }
            if (fileVersion >= _version) {
                break;
            }
            logFileList.add(file);
        }
        return logFileList;
    }
}
