package com.anjuke.romar.core.impl.request;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarRequest;

public class MultiItemIdRequest  extends BaseRequest implements RomarRequest{

    private long[] _itemId;

    public MultiItemIdRequest(RequestPath path) {
        super(path);
    }

    public long[] getItemId() {
        return _itemId;
    }

    public void setItemId(long[] theItemId) {
        _itemId = theItemId;
    }

}
