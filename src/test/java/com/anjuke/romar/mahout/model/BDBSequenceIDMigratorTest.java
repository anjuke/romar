package com.anjuke.romar.mahout.model;

public class BDBSequenceIDMigratorTest extends BDBIDMigratorTest {
    protected BDBIDMigrator createIDMigrator(String path) {
        return new BDBSequenceIDMigrator(path, CACHE_SIZE);
    }
}
