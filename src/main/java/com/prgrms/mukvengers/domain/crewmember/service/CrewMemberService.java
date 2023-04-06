package com.prgrms.mukvengers.domain.crewmember.service;

import com.prgrms.mukvengers.domain.crew.model.Crew;
import com.prgrms.mukvengers.domain.crewmember.model.vo.CrewMemberRole;
import com.prgrms.mukvengers.global.common.dto.IdResponse;

public interface CrewMemberService {

	IdResponse create(Crew crew, Long userId, CrewMemberRole crewMemberRole);

	void block(Long userId, Long blockUserId, Long crewId);

	void delete(Long userId, Long crewId);
}
