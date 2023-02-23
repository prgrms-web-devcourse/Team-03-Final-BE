package com.prgrms.mukvengers.domain.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.mukvengers.domain.store.dto.request.CreateStoreRequest;
import com.prgrms.mukvengers.domain.store.mapper.StoreMapper;
import com.prgrms.mukvengers.domain.store.model.Store;
import com.prgrms.mukvengers.domain.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultStoreService implements StoreService{

	private final StoreRepository storeRepository;
	private final StoreMapper storeMapper;

	@Override
	@Transactional
	public String create(CreateStoreRequest createStoreRequest) {

		Store store = storeMapper.toStore(createStoreRequest);
		storeRepository.save(store);

		return store.getMapStoreId();
	}

}
