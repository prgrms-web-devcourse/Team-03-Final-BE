package com.prgrms.mukvengers.domain.store.api;

import static org.springframework.http.MediaType.*;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.prgrms.mukvengers.domain.store.dto.request.CreateStoreRequest;
import com.prgrms.mukvengers.domain.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	/**
	 * <pre>
	 *     가게 생성
	 * </pre>
	 * @param createStoreRequest 가게 정보 DTO
	 * @return status : 201, body : 생성된 가게 조회 redirectUri
	 */
	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<URI> create(@RequestBody @Valid CreateStoreRequest createStoreRequest) {

		String mapStoreId = storeService.create(createStoreRequest);

		String createURL = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString() + "/" + mapStoreId;

		return ResponseEntity.created(URI.create(createURL)).build();
	}

}
