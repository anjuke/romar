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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import com.anjuke.romar.core.RomarConfig;

public final class SimilarityFileWriterFactory {

    private SimilarityFileWriterFactory() {
    }

    public static SimilarityFileWriter createFileWriter(RomarConfig config)
            throws IOException {
        String filepath = config.getSimilarityFile();
        File file = new File(filepath);
        OutputStream os = getFileOutputStream(file);
        if (config.isBinarySimilarityFile()) {
            return new DataFileWriter(os);
        } else {
            return new TextFileWriter(os);
        }
    }

    static OutputStream getFileOutputStream(File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        String name = file.getName();
        if (name.endsWith(".gz")) {
            return new GZIPOutputStream(os);
        } else if (name.endsWith(".zip")) {
            return new ZipOutputStream(os);
        } else {
            return os;
        }
    }
}
