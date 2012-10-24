package com.anjuke.romar.core.impl.request;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarRequest;

public class MultiItemIdRequest  extends BaseRequest implements RomarRequest{

    private long[] itemId;

    public MultiItemIdRequest(RequestPath path) {
        super(path);
    }

    public long[] getItemId() {
        return itemId;
    }

    public void setItemId(long[] theItemId) {
        this.itemId = theItemId;
    }

}
