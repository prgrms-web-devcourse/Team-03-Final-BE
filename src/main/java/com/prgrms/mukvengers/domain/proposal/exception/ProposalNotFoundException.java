package com.prgrms.mukvengers.domain.proposal.exception;

import com.prgrms.mukvengers.global.base.exception.ErrorCode;
import com.prgrms.mukvengers.global.base.exception.ServiceException;

public class ProposalNotFoundException extends ServiceException {

	private static final ErrorCode ERROR_CODE = ErrorCode.PROPOSAL_NOT_FOUND;
	private static final String MESSAGE_KEY = "exception.proposal.notfound";

	public ProposalNotFoundException(Long proposalId) {
		super(ERROR_CODE, MESSAGE_KEY, new Object[] {proposalId});
	}
}
