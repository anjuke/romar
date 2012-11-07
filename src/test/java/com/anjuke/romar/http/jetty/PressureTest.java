package com.anjuke.romar.http.jetty;

import java.io.File;
import java.util.Random;

import com.anjuke.romar.mahout.persistence.FilePreferenceSource;

public class PressureTest {
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
