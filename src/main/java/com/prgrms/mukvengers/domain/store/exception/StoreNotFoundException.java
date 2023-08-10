package com.prgrms.mukvengers.domain.store.exception;

import com.prgrms.mukvengers.global.base.exception.ErrorCode;
import com.prgrms.mukvengers.global.base.exception.ServiceException;

public class StoreNotFoundException extends ServiceException {

	private static final ErrorCode ERROR_CODE = ErrorCode.STORE_NOT_FOUND;
	private static final String MESSAGE_KEY = "exception.store.notfound";

	public StoreNotFoundException(Long storeId) {
		super(ERROR_CODE, MESSAGE_KEY, new Object[] {storeId});
	}
}
