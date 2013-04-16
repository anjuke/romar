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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class DataFileWriter implements SimilarityFileWriter {

    private final DataOutputStream _outputStream;

    DataFileWriter(OutputStream os) {
        super();
        this._outputStream = new DataOutputStream(new BufferedOutputStream(os));
    }

    @Override
    public void write(long id1, long id2, double similairty) throws IOException {
        _outputStream.writeLong(id1);
        _outputStream.writeLong(id2);
        _outputStream.writeDouble(similairty);
    }

    @Override
    public void close() throws IOException {
        _outputStream.flush();
        _outputStream.close();
    }

}
