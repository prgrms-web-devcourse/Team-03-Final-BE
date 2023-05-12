package com.prgrms.mukvengers.domain.proposal.api;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.prgrms.mukvengers.domain.proposal.dto.request.CreateProposalRequest;
import com.prgrms.mukvengers.domain.proposal.dto.request.UpdateProposalRequest;
import com.prgrms.mukvengers.domain.proposal.dto.response.ProposalPageResponse;
import com.prgrms.mukvengers.domain.proposal.dto.response.ProposalResponse;
import com.prgrms.mukvengers.domain.proposal.service.ProposalService;
import com.prgrms.mukvengers.global.common.dto.ApiResponse;
import com.prgrms.mukvengers.global.common.dto.IdResponse;
import com.prgrms.mukvengers.global.security.token.dto.jwt.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProposalController {

	private final ProposalService proposalService;

	/**
	 * <pre>
	 *     사용자가 신청서를 작성합니다.
	 * </pre>
	 * @param crewId 신청하고자 하는 밥모임 아이디
	 * @param proposalRequest 신청서 생성 DTO
	 * @param user 유저 정보
	 * @return status : 201, header location : 생성된 신청서 조회 redirectUrl
	 */
	@PostMapping(value = "/crews/{crewId}/proposals", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<IdResponse> create
	(
		@PathVariable Long crewId,
		@RequestBody @Valid CreateProposalRequest proposalRequest,
		@AuthenticationPrincipal JwtAuthentication user
	) {
		IdResponse proposal = proposalService.create(proposalRequest, user.id(), crewId);
		URI location = UriComponentsBuilder.fromUriString("/api/v1/proposals/" + proposal.id()).build().toUri();
		return ResponseEntity.created(location).body(proposal);
	}

	/**
	 * <pre>
	 *     사용자가 신청서를 조회합니다.
	 * </pre>
	 * @param proposalId 조회할 신청서 아이디
	 * @return status : 200, body : 조회된 신청서
	 */
	@GetMapping(value = "/proposals/{proposalId}")
	public ResponseEntity<ApiResponse<ProposalResponse>> findById
	(
		@PathVariable Long proposalId
	) {
		ProposalResponse response = proposalService.getById(proposalId);

		return ResponseEntity.ok(new ApiResponse<>(response));

	}

	/**
	 * <pre>
	 *     사용자가 방장인 모임의 신청서를 모두 조회합니다.
	 * </pre>
	 * @param user 사용자 정보
	 * @return status : 200, body : 조회된 모든 신청서 데이터
	 */
	@GetMapping(value = "/proposals/leader", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<ProposalPageResponse>> getProposalsByLeaderId
	(
		@AuthenticationPrincipal JwtAuthentication user,
		@PageableDefault Pageable pageable
	) {
		ProposalPageResponse responses = proposalService.getProposalsByLeaderId(user.id(), pageable);

		return ResponseEntity.ok().body(new ApiResponse<>(responses));
	}

	/**
	 * <pre>
	 *     사용자가 방장인 아니고 참여자인 모임의 신청서를 모두 조회합니다.
	 * </pre>
	 * @param user 사용자 정보
	 * @return status : 200, body : 조회된 모든 신청서 데이터
	 */
	@GetMapping(value = "/proposals/member", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<ProposalPageResponse>> getProposalsByMemberId
	(
		@AuthenticationPrincipal JwtAuthentication user,
		@PageableDefault Pageable pageable
	) {
		ProposalPageResponse responses = proposalService.getProposalsByMemberId(user.id(), pageable);

		return ResponseEntity.ok().body(new ApiResponse<>(responses));
	}

	/**
	 * <pre>
	 *     방장은 신청서를 수락/거절 응답할 수 있다.
	 * </pre>
	 * @param proposalId 신청서 아이디
	 * @param proposalRequest 신청서 수정 dto
	 * @param user 유저 정보
	 * @return
	 */
	@PatchMapping(value = "/proposals/{proposalId}", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(NO_CONTENT)
	public void changeProposalStatus
	(
		@PathVariable Long proposalId,
		@RequestBody @Valid UpdateProposalRequest proposalRequest,
		@AuthenticationPrincipal JwtAuthentication user
	) {
		proposalService.updateProposalStatus(proposalRequest, user.id(), proposalId);
	}

	@DeleteMapping(value = "/proposals/{proposalId}")
	public ResponseEntity<Void> delete
		(
			@PathVariable Long proposalId,
			@AuthenticationPrincipal JwtAuthentication user
		) {
		proposalService.delete(proposalId, user.id());

		return ResponseEntity.ok().build();
	}
}