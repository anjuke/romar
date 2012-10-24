package com.anjuke.romar.core;

public abstract class RomarDefaultPathFactory<T> {

    protected T getInstance(){
        throw new UnsupportedOperationException();
    }

    protected void init(){

    }

    protected void setRecommend(String path) {

    }

    protected void setUpdate(String path) {

    }

    protected void setRemove(String path) {

    }

    protected void setCommit(String path) {

    }

    protected void setItemRecommend(String path) {

    }

    protected void setCompact(String path){

    }
}
