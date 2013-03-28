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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity.ItemItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericUserSimilarity.UserUserSimilarity;
import org.apache.mahout.common.iterator.FileLineIterator;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.Iterators;

public class RomarFileSimilarityIterator<T> extends ForwardingIterator<T> {
    private static final Pattern SEPARATOR = Pattern.compile("[,	]");
    private final Iterator<T> delegate;
    static final int DATA_SIZE = (2 * Long.SIZE + Double.SIZE) / Byte.SIZE;

    private RomarFileSimilarityIterator(DataFileIterator fileIterator,
            final SimilarityBuilder<T> builder) {
        delegate = Iterators.transform(fileIterator, new Function<byte[], T>() {
            public T apply(byte[] input) {
                ByteBuffer buffer = ByteBuffer.wrap(input);
                return builder.create(buffer.getLong(), buffer.getLong(),
                        buffer.getDouble());
            }
        });
    }

    private RomarFileSimilarityIterator(FileLineIterator fileIterator,
            final SimilarityBuilder<T> builder) {
        delegate = Iterators.transform(fileIterator, new Function<String, T>() {
            public T apply(String input) {
                String[] tokens = SEPARATOR.split(input);
                double value = Double.parseDouble(tokens[2]);
                if (value > 1.0 && value - 1.0 < 0.00001) {
                    value = 1.0;
                }

                return builder.create(Long.parseLong(tokens[0]),
                        Long.parseLong(tokens[1]), value);
            }
        });
    }

    @Override
    protected Iterator<T> delegate() {
        return delegate;
    }

    private static interface SimilarityBuilder<T> {
        T create(long id1, long id2, double value);
    }

    private static class ItemSimilarityBuilder implements
            SimilarityBuilder<ItemItemSimilarity> {
        @Override
        public ItemItemSimilarity create(long id1, long id2, double value) {
            return new ItemItemSimilarity(id1, id2, value);
        }

    }

    public static interface IteratorBuiler<T> {
        Iterator<T> build(File file);
    }

    private static class UserSimilarityBuilder implements
            SimilarityBuilder<UserUserSimilarity> {
        @Override
        public UserUserSimilarity create(long id1, long id2, double value) {
            return new UserUserSimilarity(id1, id2, value);
        }

    }

    public static IteratorBuiler<ItemItemSimilarity> lineFileItemIteratorBuilder() {
        return new IteratorBuiler<ItemItemSimilarity>() {
            @Override
            public Iterator<ItemItemSimilarity> build(File file) {
                try {
                    return new RomarFileSimilarityIterator<ItemItemSimilarity>(
                            new FileLineIterator(file), new ItemSimilarityBuilder());
                } catch (IOException e) {
                    throw new IllegalStateException("Can't read " + file, e);
                }
            }
        };
    }

    public static Iterator<ItemItemSimilarity> lineFileItemIterator(File file) {
        return lineFileItemIteratorBuilder().build(file);
    }

    public static IteratorBuiler<ItemItemSimilarity> dataFileItemIteratorBuilder() {
        return new IteratorBuiler<ItemItemSimilarity>() {
            @Override
            public Iterator<ItemItemSimilarity> build(File file) {
                try {
                    return new RomarFileSimilarityIterator<ItemItemSimilarity>(
                            new DataFileIterator(file, DATA_SIZE),
                            new ItemSimilarityBuilder());
                } catch (IOException e) {
                    throw new IllegalStateException("Can't read " + file, e);
                }
            }
        };
    }

    public static Iterator<ItemItemSimilarity> dataFileItemIterator(File file) {
        return dataFileItemIteratorBuilder().build(file);
    }

    public static IteratorBuiler<UserUserSimilarity> lineFileUserIteratorBuilder() {
        return new IteratorBuiler<UserUserSimilarity>() {
            @Override
            public Iterator<UserUserSimilarity> build(File file) {
                try {
                    return new RomarFileSimilarityIterator<UserUserSimilarity>(
                            new FileLineIterator(file), new UserSimilarityBuilder());
                } catch (IOException e) {
                    throw new IllegalStateException("Can't read " + file, e);
                }
            }
        };
    }

    public static Iterator<UserUserSimilarity> lineFileUserIterator(File file) {
        return lineFileUserIteratorBuilder().build(file);
    }

    public static IteratorBuiler<UserUserSimilarity> dataFileUserIteratorBuilder() {
        return new IteratorBuiler<UserUserSimilarity>() {
            @Override
            public Iterator<UserUserSimilarity> build(File file) {
                try {
                    return new RomarFileSimilarityIterator<UserUserSimilarity>(
                            new DataFileIterator(file, DATA_SIZE),
                            new UserSimilarityBuilder());
                } catch (IOException e) {
                    throw new IllegalStateException("Can't read " + file, e);
                }
            }
        };
    }

    public static Iterator<UserUserSimilarity> dataFileUserIterator(File file) {
        return dataFileUserIteratorBuilder().build(file);
    }
}
