package com.anjuke.romar.core;

public final class CoreContainer {

    private CoreContainer(){

    }

    private static RomarCore _defaultCore = null;

    public static synchronized RomarCore getCore() {
        if (_defaultCore == null) {
            _defaultCore = RomarPathProcessFactory.createCore();
        }
        return _defaultCore;
    }

}
