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
package com.anjuke.romar.http.jetty;

import java.io.File;
import java.util.Random;

import com.anjuke.romar.mahout.persistence.FilePreferenceSource;

public class PressureTestData {
    public static void main(String[] args) {
        Random random = new Random();
        FilePreferenceSource source = new FilePreferenceSource(new File("/data1/romar"));
        final int COUNT = 5000000;
        for (int i = 0; i < COUNT; i++) {
            source.setPreference(random.nextInt(10000000/4), random.nextInt(10000000/8), 1.0f);
            if (i % 1000 == 0) {
                System.out.println(i);
            }
        }
        source.commit();

    }
}
