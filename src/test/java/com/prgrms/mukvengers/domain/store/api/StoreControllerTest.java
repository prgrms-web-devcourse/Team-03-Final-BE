package com.prgrms.mukvengers.domain.store.api;

import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import com.prgrms.mukvengers.base.ControllerTest;
import com.prgrms.mukvengers.domain.store.dto.request.CreateStoreRequest;
import com.prgrms.mukvengers.utils.StoreObjectProvider;

class StoreControllerTest extends ControllerTest {

	@Test
	@DisplayName("[성공]가게를 저장한다.")
	void create_success() throws Exception {

		CreateStoreRequest createStoreRequest = StoreObjectProvider.getCreateStoreRequest();

		String jsonRequest = objectMapper.writeValueAsString(createStoreRequest);

		mockMvc.perform(post("/api/v1/stores")
				.contentType(APPLICATION_JSON)
				.content(jsonRequest)
				.accept(APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", Matchers.containsString("/api/v1/stores")))
			.andExpect(redirectedUrlPattern("http://localhost:8080/api/v1/stores/*"))
			.andDo(print())
			.andDo(document("store-create",
				requestFields(
					fieldWithPath("latitude").type(JsonFieldType.STRING).description("위도"),
					fieldWithPath("longitude").type(JsonFieldType.STRING).description("경도"),
					fieldWithPath("mapStoreId").type(JsonFieldType.STRING).description("지도 api 제공 id")
				)
			));
	}

}