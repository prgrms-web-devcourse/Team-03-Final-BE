package com.prgrms.mukvengers.domain.crew.dto.response;

import com.prgrms.mukvengers.domain.store.dto.response.StoreResponse;
import com.prgrms.mukvengers.domain.user.dto.response.UserProfileResponse;

public record CrewResponse(
	Long id,
	UserProfileResponse leader,
	StoreResponse store,
	String name,
	String latitude,
	String longitude,
	Integer capacity,
	String status,
	String content,
	String category
) {
}
