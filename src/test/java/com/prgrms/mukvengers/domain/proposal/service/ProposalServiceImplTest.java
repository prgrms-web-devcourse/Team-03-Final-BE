package com.prgrms.mukvengers.domain.proposal.service;

import static com.prgrms.mukvengers.domain.proposal.model.vo.ProposalStatus.*;
import static com.prgrms.mukvengers.utils.CrewObjectProvider.*;
import static com.prgrms.mukvengers.utils.ProposalObjectProvider.*;
import static com.prgrms.mukvengers.utils.UserObjectProvider.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgrms.mukvengers.base.ServiceTest;
import com.prgrms.mukvengers.domain.crew.model.Crew;
import com.prgrms.mukvengers.domain.crewmember.model.CrewMember;
import com.prgrms.mukvengers.domain.crewmember.model.vo.CrewMemberRole;
import com.prgrms.mukvengers.domain.proposal.dto.request.CreateProposalRequest;
import com.prgrms.mukvengers.domain.proposal.dto.request.UpdateProposalRequest;
import com.prgrms.mukvengers.domain.proposal.dto.response.ProposalResponse;
import com.prgrms.mukvengers.domain.proposal.dto.response.ProposalResponses;
import com.prgrms.mukvengers.domain.proposal.exception.CrewMemberOverCapacity;
import com.prgrms.mukvengers.domain.proposal.exception.DuplicateProposalException;
import com.prgrms.mukvengers.domain.proposal.exception.ExistCrewMemberRoleException;
import com.prgrms.mukvengers.domain.proposal.exception.InvalidProposalStatusException;
import com.prgrms.mukvengers.domain.proposal.model.Proposal;
import com.prgrms.mukvengers.domain.proposal.model.vo.ProposalStatus;
import com.prgrms.mukvengers.domain.user.model.User;
import com.prgrms.mukvengers.global.common.dto.IdResponse;
import com.prgrms.mukvengers.utils.CrewMemberObjectProvider;
import com.prgrms.mukvengers.utils.ProposalObjectProvider;

class ProposalServiceImplTest extends ServiceTest {

	private User leader;
	private Crew crew;

	@BeforeEach
	void setUp() {
		User createUser = createUser("12121212");
		leader = userRepository.save(createUser);

		Crew createCrew = createCrew(savedStore);
		crew = crewRepository.save(createCrew);

		CrewMember createCrewMember = CrewMemberObjectProvider.createCrewMember(leader.getId(), crew,
			CrewMemberRole.LEADER);
		crewMemberRepository.save(createCrewMember);
	}

	@Test
	@DisplayName("[성공] 사용자는 신청서를 작성할 수 있다.")
	void createProposal_success() {

		//given
		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(leader.getId());

		// when
		IdResponse response = proposalService.create(proposalRequest, savedUser1Id, crew.getId());

		// then
		Optional<Proposal> findProposal = proposalRepository.findById(response.id());
		assertThat(findProposal).isPresent();
		assertThat(findProposal.get())
			.hasFieldOrPropertyWithValue("user", savedUser1)
			.hasFieldOrPropertyWithValue("leaderId", leader.getId())
			.hasFieldOrPropertyWithValue("crewId", crew.getId())
			.hasFieldOrPropertyWithValue("content", proposalRequest.content());
	}

	@Test
	@DisplayName("[실패] 해당 밥모임에 대기중인 신청서가 이미 존재한다면 신청서를 작성할 수 없다.")
	void createProposal_fail_duplicate() {

		// given
		Proposal createProposal = createProposal(savedUser1, leader.getId(), crew.getId());
		proposalRepository.save(createProposal);

		CreateProposalRequest worstRequest = createProposalRequest(leader.getId());

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(worstRequest, savedUser1Id, crew.getId())
			).isInstanceOf(DuplicateProposalException.class);
	}

	@Test
	@DisplayName("[실패] 모집 정원이 다 찬 밥모임에는 신청서를 작성할 수 없다.")
	void createProposal_fail_countOverCapacity() {

		//given
		List<CrewMember> crewMembers = CrewMemberObjectProvider.createCrewMembers(savedUser1Id, crew,
			CrewMemberRole.MEMBER,
			crew.getCapacity());

		crewMemberRepository.saveAll(crewMembers);

		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(leader.getId());

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(proposalRequest, savedUser1Id, crew.getId())
			)
			.isInstanceOf(CrewMemberOverCapacity.class);
	}

	@Test
	@DisplayName("[실패] 해당 밥모임에 강퇴된 사용자라면 신청서를 작성할 수 없다.")
	void createProposal_fail_blockedUser() {

		//given
		CrewMember createCrewMember = CrewMemberObjectProvider.createCrewMember(savedUser1Id, crew,
			CrewMemberRole.BLOCKED);
		crewMemberRepository.save(createCrewMember);

		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(savedUser1Id);

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(proposalRequest, savedUser1Id, crew.getId())
			)
			.isInstanceOf(ExistCrewMemberRoleException.class);
	}

	@Test
	@DisplayName("[실패] 사용자가 해당 밥모임의 리더라면 신청서를 작성할 수 없다.")
	void createProposal_fail_LeaderUser() {

		//given
		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(leader.getId());

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(proposalRequest, leader.getId(), crew.getId())
			)
			.isInstanceOf(ExistCrewMemberRoleException.class);
	}

	@Test
	@DisplayName("[실패] 사용자가 이미 해당 밥모임에 참여자라면 신청서를 작성할 수 없다.")
	void createProposal_fail_DuplicatedUser() {

		//given
		CrewMember createCrewMember = CrewMemberObjectProvider.createCrewMember(savedUser1Id, crew,
			CrewMemberRole.MEMBER);
		crewMemberRepository.save(createCrewMember);

		CreateProposalRequest proposalRequest = ProposalObjectProvider.createProposalRequest(savedUser1Id);

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.create(proposalRequest, savedUser1Id, crew.getId())
			)
			.isInstanceOf(ExistCrewMemberRoleException.class);
	}

	@Test
	@DisplayName("[성공] 방장이 신청서를 '승인'하는 경우 밥모임원에 등록된다.")
	void update_proposalStatus_approve_success() {

		//given
		String inputProposalStatus = "승인";

		User createUser = createUser("1232456789");
		User user = userRepository.save(createUser);

		Proposal createProposal = ProposalObjectProvider.createProposal(user, leader.getId(), crew.getId());
		Proposal proposal = proposalRepository.save(createProposal);

		UpdateProposalRequest proposalRequest = new UpdateProposalRequest(inputProposalStatus);

		// when
		proposalService.updateProposalStatus(proposalRequest, leader.getId(), proposal.getId());
		Optional<CrewMember> result = crewMemberRepository.findCrewMemberByCrewIdAndUserId(
			crew.getId(), user.getId());

		// then
		assertThat(proposal.getStatus()).isEqualTo(APPROVE);
		assertThat(result).isPresent();
		assertThat(result.get().getUserId()).isEqualTo(user.getId());
	}

	@Test
	@DisplayName("[성공] 방장이 신청서를 '거절'하는 경우 밥모임원에 등록되지 않는다.")
	void update_proposalStatus_refuse_success() {

		//given
		String inputProposalStatus = "거절";

		User createUser = createUser("1232456789");
		User user = userRepository.save(createUser);

		Proposal createProposal = ProposalObjectProvider.createProposal(user, leader.getId(), crew.getId());
		Proposal proposal = proposalRepository.save(createProposal);

		UpdateProposalRequest proposalRequest = new UpdateProposalRequest(inputProposalStatus);

		// when
		proposalService.updateProposalStatus(proposalRequest, leader.getId(), proposal.getId());
		Optional<CrewMember> saveCrewMember = crewMemberRepository.findCrewMemberByCrewIdAndUserId(
			crew.getId(), user.getId());

		// then
		assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.REFUSE);
		assertThat(saveCrewMember).isEmpty();
	}

	@Test
	@DisplayName("[실패] 방장이 올바르지 않은 상태를 응답하면 에러가 발생한다.")
	void update_proposalStatus_fail_otherProposalStatus() {

		//given
		String inputProposalStatus = "모름";

		User createUser = createUser("1232456789");
		User user = userRepository.save(createUser);

		Proposal createProposal = ProposalObjectProvider.createProposal(user, leader.getId(), crew.getId());
		Proposal proposal = proposalRepository.save(createProposal);

		UpdateProposalRequest proposalRequest = new UpdateProposalRequest(inputProposalStatus);

		// when & then
		assertThatThrownBy
			(
				() -> proposalService.updateProposalStatus(proposalRequest, leader.getId(), proposal.getId())
			)
			.isInstanceOf(InvalidProposalStatusException.class);
	}

	@Test
	@DisplayName("[성공] 신청서 아이디로 신청서를 조회한다.")
	void getById() {
		//given
		User user = createUser("1232456789");
		userRepository.save(user);

		Proposal proposal = ProposalObjectProvider.createProposal(user, leader.getId(), crew.getId());
		proposalRepository.save(proposal);

		//when
		ProposalResponse response = proposalService.getById(proposal.getId());

		assertThat(response)
			.hasFieldOrPropertyWithValue("id", proposal.getId())
			.hasFieldOrPropertyWithValue("leaderId", leader.getId())
			.hasFieldOrPropertyWithValue("crewId", crew.getId())
			.hasFieldOrPropertyWithValue("content", proposal.getContent())
			.hasFieldOrPropertyWithValue("status", proposal.getStatus());

		assertThat(response.user())
			.hasFieldOrPropertyWithValue("id", user.getId())
			.hasFieldOrPropertyWithValue("nickname", user.getNickname())
			.hasFieldOrPropertyWithValue("profileImgUrl", user.getProfileImgUrl())
			.hasFieldOrPropertyWithValue("introduction", user.getIntroduction())
			.hasFieldOrPropertyWithValue("leaderCount", user.getLeaderCount())
			.hasFieldOrPropertyWithValue("crewCount", user.getCrewCount())
			.hasFieldOrPropertyWithValue("tasteScore", user.getTasteScore())
			.hasFieldOrPropertyWithValue("mannerScore", String.valueOf(user.getMannerScore()));
	}

	@Test
	@DisplayName("[성공] 사용자가 방장인 모임의 모든 신청서를 조회한다.")
	void getProposalsByLeaderId_success() {

		//given
		User user = createUser("1232456789");
		userRepository.save(user);

		List<Proposal> proposals = createProposals(user, leader.getId(), crew.getId());
		proposalRepository.saveAll(proposals);

		//when
		ProposalResponses responses = proposalService.getProposalsByLeaderId(leader.getId());

		//then
		assertThat(responses.responses()).hasSize(proposals.size());
	}

	@Test
	@DisplayName("[성공] 사용자가 방장인 아니고 참여자인 밥모임의 신청서를 모두 조회합니다.")
	void getProposalsByMemberId_success() {

		//given
		User user = createUser("1232456789");
		userRepository.save(user);

		List<Proposal> proposals = createProposals(user, leader.getId(), crew.getId());
		proposalRepository.saveAll(proposals);

		//when
		ProposalResponses responses = proposalService.getProposalsByMemberId(user.getId());

		//then
		assertThat(responses.responses()).hasSize(proposals.size());
	}

}