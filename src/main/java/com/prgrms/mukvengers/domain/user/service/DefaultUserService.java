package com.prgrms.mukvengers.domain.user.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.mukvengers.domain.user.dto.request.UpdateUserRequest;
import com.prgrms.mukvengers.domain.user.dto.response.UserProfileResponse;
import com.prgrms.mukvengers.domain.user.exception.UserNotFoundException;
import com.prgrms.mukvengers.domain.user.mapper.UserMapper;
import com.prgrms.mukvengers.domain.user.model.User;
import com.prgrms.mukvengers.domain.user.repository.UserRepository;
import com.prgrms.mukvengers.global.auth.oauth.dto.AuthUserInfo;
import com.prgrms.mukvengers.global.auth.oauth.dto.OAuthUserInfo;
import com.prgrms.mukvengers.global.auth.token.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultUserService implements UserService {

	public static final String DEFAULT_ROLE = "ROLE_USER";

	private final UserMapper userMapper;
	private final UserRepository userRepository;
	private final TokenService tokenService;

	/* [회원 인증 정보 조회 및 저장] 등록된 유저 정보 찾아서 제공하고 없으면 등록합니다. */
	@Override
	@Transactional
	@Cacheable(value = "User", key = "#oauthUserInfo.oauthId")
	public AuthUserInfo getOrRegisterUser(OAuthUserInfo oauthUserInfo) {
		User user = userRepository
			.findByUserIdByProviderAndOauthId(oauthUserInfo.provider(), oauthUserInfo.oauthId())
			.orElseGet(() -> userRepository.save(userMapper.toUser(oauthUserInfo)));

		return new AuthUserInfo(user.getId(), DEFAULT_ROLE, user.getNickname());
	}

	/* [회원 조회] 사용자 ID를 통해 등록된 유저 정보 찾아서 제공하고 없으면 예외가 발생합니다. */
	@Override
	@Cacheable(value = "User", key = "#userId")
	public UserProfileResponse getUserProfile(Long userId) {
		return userRepository.findById(userId)
			.map(userMapper::toSingleUserResponse)
			.orElseThrow(() -> new UserNotFoundException(userId));
	}

	/* [회원 프로필 수정] UpdateUserRequest DTO를 사용해서 사용자의 프로필(닉네임, 프로필 이미지, 자기소개)를 한번에 수정합니다. */
	@Override
	@Transactional
	@CachePut(value = "User", key = "#userId")
	public UserProfileResponse updateUserProfile(UpdateUserRequest updateUserRequest, Long userId) {
		return userRepository.findById(userId)
			.map(user -> user.changeProfile(updateUserRequest))
			.map(userMapper::toSingleUserResponse)
			.orElseThrow(() -> new UserNotFoundException(userId));
	}

	/* [회원 탈퇴] 계정을 삭제합니다. soft delete가 적용됩니다.*/
	@Override
	@Transactional
	@CacheEvict(value = "User", key = "#userId")
	public void deleteUser(Long userId, String refreshToken) {
		userRepository.findById(userId)
			.ifPresentOrElse(user -> {
				user.deleteInfo();
				tokenService.deleteRefreshToken(refreshToken);
			}, () -> {
				throw new UserNotFoundException(userId);
			});
	}
}
