package com.prgrms.mukvengers.domain.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prgrms.mukvengers.domain.review.dto.request.CreateLeaderReviewRequest;
import com.prgrms.mukvengers.domain.review.dto.request.CreateMemberReviewRequest;
import com.prgrms.mukvengers.domain.review.dto.response.ReviewResponse;
import com.prgrms.mukvengers.domain.review.dto.response.RevieweeListResponse;
import com.prgrms.mukvengers.global.base.dto.IdResponse;

public interface ReviewService {
	IdResponse createLeaderReview(CreateLeaderReviewRequest request, Long reviewerId, Long crewId);

	IdResponse createMemberReview(CreateMemberReviewRequest request, Long reviewerId, Long crewId);

	ReviewResponse getSingleReview(Long reviewId, Long userId);

	Page<ReviewResponse> getAllReceivedReview(Long userId, Pageable pageable);

	Page<ReviewResponse> getAllWroteReview(Long userId, Pageable pageable);

	List<RevieweeListResponse> getRevieweeListFromCrew(Long userId, Long crewId);
}
