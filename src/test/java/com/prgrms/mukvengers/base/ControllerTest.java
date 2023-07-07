package com.prgrms.mukvengers.base;

import static com.prgrms.mukvengers.utils.StoreObjectProvider.*;
import static com.prgrms.mukvengers.utils.UserObjectProvider.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.mukvengers.domain.crew.repository.CrewRepository;
import com.prgrms.mukvengers.domain.crewmember.repository.CrewMemberRepository;
import com.prgrms.mukvengers.domain.proposal.repository.ProposalRepository;
import com.prgrms.mukvengers.domain.review.repository.ReviewRepository;
import com.prgrms.mukvengers.domain.store.model.Store;
import com.prgrms.mukvengers.domain.store.repository.StoreRepository;
import com.prgrms.mukvengers.domain.user.model.User;
import com.prgrms.mukvengers.domain.user.repository.UserRepository;
import com.prgrms.mukvengers.global.config.RestDocsConfig;
import com.prgrms.mukvengers.global.security.token.service.JwtTokenProvider;
import com.prgrms.mukvengers.global.security.token.service.TokenService;

@SpringBootTest
@Transactional
@Import({RestDocsConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
public abstract class ControllerTest {

	protected final String CREW = "모임 API";
	protected final String CREW_MEMBER = "모임원 API";
	protected final String STORE = "가게 API";
	protected final String USER = "유저 API";
	protected final String REVIEW = "리뷰 API";
	protected final String PROPOSAL = "신청서 API";
	protected final String NOTIFICATION = "알림 API";

	protected final String BEARER_TYPE = "Bearer ";

	@Autowired
	protected RestDocumentationResultHandler restDocs;

	@Autowired
	protected JwtTokenProvider jwtTokenProvider;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected StoreRepository storeRepository;

	@Autowired
	protected CrewRepository crewRepository;

	@Autowired
	protected CrewMemberRepository crewMemberRepository;

	@Autowired
	protected ReviewRepository reviewRepository;

	@Autowired
	protected ProposalRepository proposalRepository;

	@Autowired
	protected TokenService tokenService;

	protected MockMvc mockMvc;

	protected String accessToken1;

	protected String accessToken2;

	protected User savedUser1;

	protected User savedUser2;

	protected Store savedStore;

	protected Long savedUser1Id;

	protected Long savedUser2Id;

	@BeforeEach
	void setUpRestDocs(WebApplicationContext webApplicationContext,
		RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.apply(documentationConfiguration(restDocumentation))
			.apply(springSecurity())
			.alwaysDo(restDocs)
			.build();
	}

	@BeforeEach
	void setUpLogin() {

		savedUser1 = userRepository.save(createUser("123"));

		savedUser2 = userRepository.save(createUser("456"));

		savedStore = storeRepository.save(createStore());

		savedUser1Id = savedUser1.getId();

		accessToken1 = jwtTokenProvider.createAccessToken(savedUser1Id, "USER");

		savedUser2Id = savedUser2.getId();

		accessToken2 = jwtTokenProvider.createAccessToken(savedUser2Id, "USER");
	}

}
