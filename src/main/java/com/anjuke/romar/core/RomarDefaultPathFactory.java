package com.anjuke.romar.core;

public interface RomarDefaultPathFactory<T> {
    T getInstance();

    void init();

    void setRecommend(RequestPath path);

    void setUpdate(RequestPath path);

    void setRemove(RequestPath path);

    void setCommit(RequestPath path);

    void setItemRecommend(RequestPath path);

    void setSimilarUser(RequestPath path);

    void setOptimize(RequestPath path);

    void setEstimate(RequestPath path);

    void setRemoveUser(RequestPath path);

    void setRemoveItem(RequestPath path);
}
