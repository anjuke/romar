package com.anjukeinc.service.recommender.mahout;

import java.util.Random;

import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.Test;

public class MahoutServiceFactoryTest {

    Random r = new Random();

    int itemRange = 10000;
    int userRange = 500000;

    @Test
    public void testPerformance() throws TasteException{
        MahoutService service=new MahoutServiceFactory().getService();

        int count=0;
        long userId;
        long itemId;
        float pref;




        userId=getUserId();
        itemId=getItemId();
        pref=getPref();

        long t1=System.currentTimeMillis();
        service.setPreference(userId, itemId, pref);
        long t2=System.currentTimeMillis();
        service.refresh(null);
        service.recommend(userId, 5);

    }

    private long getUserId() {
        return r.nextInt(userRange);
    }

    private long getItemId() {
        return r.nextInt(itemRange);
    }

    private float getPref() {
        return r.nextFloat() * 5f;
    }
}
