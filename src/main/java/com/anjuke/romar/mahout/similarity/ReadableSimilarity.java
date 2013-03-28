package com.anjuke.romar.mahout.similarity;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;

public interface ReadableSimilarity {
    FastByIDMap<FastByIDMap<Double>> getSimilarityMaps();
}
