package com.anjuke.romar.core;

public abstract class RomarDefaultPathFactory<T> {

    protected T getInstance(){
        throw new UnsupportedOperationException();
    }

    protected void init(){

    }

    protected void setRecommend(RequestPath path) {

    }

    protected void setUpdate(RequestPath path) {

    }

    protected void setRemove(RequestPath path) {

    }

    protected void setCommit(RequestPath path) {

    }

    protected void setItemRecommend(RequestPath path) {

    }

    protected void setCompact(RequestPath path){

    }

    protected void setEstimate(RequestPath path){

    }
}
