package com.anjuke.romar.core.impl.request;

import com.anjuke.romar.core.RequestPath;
import com.anjuke.romar.core.RomarRequest;

public class NoneContentRequest  extends BaseRequest implements RomarRequest{

    public NoneContentRequest(RequestPath path) {
        super(path);
    }

}
