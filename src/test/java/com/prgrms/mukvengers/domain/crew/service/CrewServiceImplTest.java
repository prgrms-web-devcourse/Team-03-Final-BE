package com.prgrms.mukvengers.domain.crew.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.prgrms.mukvengers.base.ServiceTest;
import com.prgrms.mukvengers.domain.crew.dto.request.CreateCrewRequest;
import com.prgrms.mukvengers.domain.crew.dto.request.SearchCrewRequest;
import com.prgrms.mukvengers.domain.crew.dto.request.UpdateStatusRequest;
import com.prgrms.mukvengers.domain.crew.dto.response.CrewPageResponse;
import com.prgrms.mukvengers.domain.crew.dto.response.CrewResponse;
import com.prgrms.mukvengers.domain.crew.dto.response.CrewResponses;
import com.prgrms.mukvengers.domain.crew.model.Crew;
import com.prgrms.mukvengers.domain.crew.model.vo.Category;
import com.prgrms.mukvengers.domain.crew.model.vo.Status;
import com.prgrms.mukvengers.global.common.dto.IdResponse;
import com.prgrms.mukvengers.utils.CrewObjectProvider;

class CrewServiceImplTest extends ServiceTest {

	@Test
	@DisplayName("[성공] Crew 저장에 성공한다.")
	void create_success() { //given when then 쓰면 좋을듯?

		//given
		CreateCrewRequest createCrewRequest = CrewObjectProvider.getCreateCrewRequest(savedStore.getMapStoreId());

		double parseLatitude = Double.parseDouble(createCrewRequest.latitude());
		double parseLongitude = Double.parseDouble(createCrewRequest.longitude());
		Point location = gf.createPoint(new Coordinate(parseLongitude, parseLatitude));
		IdResponse idResponse = crewService.create(createCrewRequest, savedUser.getId());

		//when
		Optional<Crew> optionalCrew = crewRepository.findById(idResponse.id());

		//then
		assertThat(crewRepository.count()).isNotZero();
		assertThat(optionalCrew).isPresent();
		Crew crew = optionalCrew.get();
		assertThat(crew)
			.hasFieldOrPropertyWithValue("store", savedStore)
			.hasFieldOrPropertyWithValue("name", createCrewRequest.name())
			.hasFieldOrPropertyWithValue("location", location)
			.hasFieldOrPropertyWithValue("capacity", createCrewRequest.capacity())
			.hasFieldOrPropertyWithValue("status", Status.RECRUITING)
			.hasFieldOrPropertyWithValue("content", createCrewRequest.content())
			.hasFieldOrPropertyWithValue("category", Category.of(createCrewRequest.category()));
	}

	@Test
	@DisplayName("[성공] 모임 아이디로 Crew를 단건 조회한다.")
	void getById_success() {

		//given
		Crew crew = CrewObjectProvider.createCrew(savedStore);

		crewRepository.save(crew);

		//when
		CrewResponse response = crewService.getById(crew.getId());

		//then
		assertThat(response)
			.hasFieldOrPropertyWithValue("id", crew.getId())
			.hasFieldOrPropertyWithValue("name", crew.getName())
			.hasFieldOrPropertyWithValue("capacity", crew.getCapacity())
			.hasFieldOrPropertyWithValue("status", crew.getStatus().name())
			.hasFieldOrPropertyWithValue("content", crew.getContent())
			.hasFieldOrPropertyWithValue("category", crew.getCategory().name())
			.hasFieldOrPropertyWithValue("promiseTime", crew.getPromiseTime());
	}

	@Test
	@DisplayName("[성공] map api 아이디로 Crew 조회를 한다")
	void findByMapStoreId_success() {

		//given
		List<Crew> crews = CrewObjectProvider.createCrews(savedStore);

		crewRepository.saveAll(crews);

		Integer page = 0;
		Integer size = 5;
		Pageable pageable = PageRequest.of(page, size);

		//when
		CrewPageResponse crewSliceResponse = crewService.getByMapStoreId(savedStore.getMapStoreId(), pageable);

		//then
		Slice<CrewResponse> responses = crewSliceResponse.responses();
		assertThat(responses).hasSize(size);

	}

	@Test
	@DisplayName("[성공] 사용자의 위치를 위경도로 받아 거리 안에 있는 밥 모임을 조회한다.")
	void findByLocation_success() {

		//given
		Crew crew = CrewObjectProvider.createCrew(savedStore);

		crewRepository.save(crew);

		Double latitude = 35.75413579;
		Double longitude = -147.4654321321;
		Integer distance = 500;

		SearchCrewRequest distanceRequest = new SearchCrewRequest(longitude, latitude, distance);

		//when
		CrewResponses crewResponses = crewService.getByLocation(distanceRequest);

		//then
		List<CrewResponse> responses = crewResponses.responses();
		assertThat(responses).hasSize(1);

	}

	@Test
	@DisplayName("[성공] 모임의 상태를 받아 변경한다.")
	void updateStatus_success() {

		//given
		Crew crew = CrewObjectProvider.createCrew(savedStore);

		crewRepository.save(crew);

		String status = "모집종료";
		UpdateStatusRequest updateStatusRequest = new UpdateStatusRequest(crew.getId(), status);

		//when
		crewService.updateStatus(updateStatusRequest);

		//then
		Optional<Crew> optionalCrew = crewRepository.findById(crew.getId());

		assertThat(optionalCrew).isPresent();
		Crew savedCrew = optionalCrew.get();
		assertThat(savedCrew.getStatus()).isEqualTo(Status.of(status));
	}

}