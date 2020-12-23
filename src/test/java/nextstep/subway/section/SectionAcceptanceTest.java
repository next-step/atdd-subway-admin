package nextstep.subway.section;

import static nextstep.subway.line.LineTestFixture.*;
import static nextstep.subway.section.SectionTestFixture.*;
import static nextstep.subway.station.StationTestFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseTest {

	@Autowired
	private StationService stationService;

	private StationResponse exampleStation1;
	private StationResponse exampleStation2;
	private StationResponse exampleStation3;

	private LineResponse exampleLine1;

	@BeforeEach
	void setup() {

		exampleStation1 = stationService.saveStation(StationRequest.of(EXAMPLE_STATION1_NAME));
		exampleStation2 = stationService.saveStation(StationRequest.of(EXAMPLE_STATION2_NAME));
		exampleStation3 = stationService.saveStation(StationRequest.of(EXAMPLE_STATION3_NAME));

		exampleLine1 = requestCreateLine(
			LineRequest.of(EXAMPLE_LINE1_NAME, EXAMPLE_LINE1_COLOR, exampleStation1.getId(), exampleStation2.getId(),
				100)
		).as(LineResponse.class);
	}

	@DisplayName("상행역을 기준으로 역사이 새로운 역 추가")
	@Test
	void createSectionCase1() {
		SectionRequest sectionRequest = SectionRequest.of(exampleStation1.getId(), exampleStation3.getId(), 10);

		// when
		// 지하철_구간_생성_요청
		ExtractableResponse<Response> response = requestCreateSection(exampleLine1.getId(), sectionRequest);

		// then
		// 지하철_구간_생성됨
		LineResponse lineResponse = response.as(LineResponse.class);
		List<StationResponse> stationResponses = lineResponse.getStations();
		List<Long> stationIds = stationResponses.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
		List<Integer> nextDistances = stationResponses.stream()
			.map(StationResponse::getNextDistance)
			.collect(Collectors.toList());
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(stationResponses).hasSize(3),
			() -> assertThat(stationIds).isEqualTo(
				Arrays.asList(exampleStation1.getId(), exampleStation3.getId(), exampleStation2.getId())),
			() -> assertThat(nextDistances).isEqualTo(
				Arrays.asList(10, 90, 0)
			)
		);
	}

	@DisplayName("하행역을 기준으로 역사이 새로운 역 추가")
	@Test
	void createSectionCase2() {
		SectionRequest sectionRequest = SectionRequest.of(exampleStation3.getId(), exampleStation2.getId(), 10);

		// when
		// 지하철_구간_생성_요청
		ExtractableResponse<Response> response = requestCreateSection(exampleLine1.getId(), sectionRequest);

		// then
		// 지하철_구간_생성됨
		LineResponse lineResponse = response.as(LineResponse.class);
		List<StationResponse> stationResponses = lineResponse.getStations();
		List<Long> stationIds = stationResponses.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
		List<Integer> nextDistances = stationResponses.stream()
			.map(StationResponse::getNextDistance)
			.collect(Collectors.toList());
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(stationResponses).hasSize(3),
			() -> assertThat(stationIds).isEqualTo(
				Arrays.asList(exampleStation1.getId(), exampleStation3.getId(), exampleStation2.getId())),
			() -> assertThat(nextDistances).isEqualTo(
				Arrays.asList(90, 10, 0)
			)
		);
	}

	@DisplayName("상행 종점 교체")
	@Test
	void createSectionCase3() {
		SectionRequest sectionRequest = SectionRequest.of(exampleStation3.getId(), exampleStation1.getId(), 10);

		// when
		// 지하철_구간_생성_요청
		ExtractableResponse<Response> response = requestCreateSection(exampleLine1.getId(), sectionRequest);

		// then
		// 지하철_구간_생성됨
		LineResponse lineResponse = response.as(LineResponse.class);
		List<StationResponse> stationResponses = lineResponse.getStations();
		List<Long> stationIds = stationResponses.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
		List<Integer> nextDistances = stationResponses.stream()
			.map(StationResponse::getNextDistance)
			.collect(Collectors.toList());
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(stationResponses).hasSize(3),
			() -> assertThat(stationIds).isEqualTo(
				Arrays.asList(exampleStation3.getId(), exampleStation1.getId(), exampleStation2.getId())),
			() -> assertThat(nextDistances).isEqualTo(
				Arrays.asList(10, 100, 0)
			)
		);
	}

	@DisplayName("하행 종점 교체")
	@Test
	void createSectionCase4() {
		SectionRequest sectionRequest = SectionRequest.of(exampleStation2.getId(), exampleStation3.getId(), 10);

		// when
		// 지하철_구간_생성_요청
		ExtractableResponse<Response> response = requestCreateSection(exampleLine1.getId(), sectionRequest);

		// then
		// 지하철_구간_생성됨
		LineResponse lineResponse = response.as(LineResponse.class);
		List<StationResponse> stationResponses = lineResponse.getStations();
		List<Long> stationIds = stationResponses.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
		List<Integer> nextDistances = stationResponses.stream()
			.map(StationResponse::getNextDistance)
			.collect(Collectors.toList());
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(stationResponses).hasSize(3),
			() -> assertThat(stationIds).isEqualTo(
				Arrays.asList(exampleStation1.getId(), exampleStation2.getId(), exampleStation3.getId())),
			() -> assertThat(nextDistances).isEqualTo(
				Arrays.asList(100, 10, 0)
			)
		);
	}
}
