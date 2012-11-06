package com.anjuke.romar.core;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.model.IDMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.romar.core.impl.response.ErrorResponse;
import com.anjuke.romar.mahout.MahoutService;

public class RomarCore {

    private static final Logger log = LoggerFactory.getLogger(RomarCore.class);

    private RomarDispatcher _dispatcher;

    private MahoutService _service;

    private IDMigrator _userIdMigrator;

    private IDMigrator _itemIdMigrator;

    private static final ErrorResponse PATH_404_ERROR = new ErrorResponse(
            ErrorResponse.RESOURCE_NOT_FOUND, "path unavailable");

    public RomarResponse execute(RomarRequest request) {
        RomarRequestHandler handler = _dispatcher.getHandler(request);
        if (handler == null) {
            return PATH_404_ERROR;
        }

        try {
            return handler.process(request);
        } catch (NoSuchUserException e) {
            log.info(e.getMessage(), e);
            return new ErrorResponse(ErrorResponse.INTERNAL_ERROR, "no such user id "
                    + e.getMessage());
        } catch (NoSuchItemException e) {
            log.info(e.getMessage(), e);
            return new ErrorResponse(ErrorResponse.INTERNAL_ERROR, "no such item id "
                    + e.getMessage());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return new ErrorResponse(ErrorResponse.INTERNAL_ERROR, "internal error: "
                    + e.getMessage());
        }
    }

    public void setDispatcher(RomarDispatcher dispatcher) {
        this._dispatcher = dispatcher;
    }

    public MahoutService getService() {
        return _service;
    }

    public void setService(MahoutService service) {
        this._service = service;
    }

    public IDMigrator getUserIdMigrator() {
        return _userIdMigrator;
    }

    public void setUserIdMigrator(IDMigrator userIdMigrator) {
        _userIdMigrator = userIdMigrator;
    }

    public IDMigrator getItemIdMigrator() {
        return _itemIdMigrator;
    }

    public void setItemIdMigrator(IDMigrator itemIdMigrator) {
        _itemIdMigrator = itemIdMigrator;
    }

    RomarDispatcher getDispatcher() {
        return _dispatcher;
    }

}
