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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

	@DisplayName("상행역 기준, 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
	@ParameterizedTest
	@ValueSource(ints = {100, 1000})
	void createSectionThrow1(int distance) {
		SectionRequest sectionRequest = SectionRequest.of(exampleStation1.getId(), exampleStation3.getId(), distance);

		// when
		// 지하철_구간_생성_요청
		ExtractableResponse<Response> response = requestCreateSection(exampleLine1.getId(), sectionRequest);

		// then
		// 지하철_구간_생성됨
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	@DisplayName("하행역 기준, 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
	@ParameterizedTest
	@ValueSource(ints = {100, 1000})
	void createSectionThrow2(int distance) {
		SectionRequest sectionRequest = SectionRequest.of(exampleStation3.getId(), exampleStation2.getId(), distance);

		// when
		// 지하철_구간_생성_요청
		ExtractableResponse<Response> response = requestCreateSection(exampleLine1.getId(), sectionRequest);

		// then
		// 지하철_구간_생성됨
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
	@Test
	void createSectionThrow3() {
		SectionRequest sectionRequest = SectionRequest.of(exampleStation1.getId(), exampleStation2.getId(), 10);

		// when
		// 지하철_구간_생성_요청
		ExtractableResponse<Response> response = requestCreateSection(exampleLine1.getId(), sectionRequest);

		// then
		// 지하철_구간_생성됨
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
	@Test
	void createSectionThrow4() {
		StationResponse exampleStation4 = stationService.saveStation(StationRequest.of(EXAMPLE_STATION4_NAME));
		StationResponse exampleStation5 = stationService.saveStation(StationRequest.of(EXAMPLE_STATION5_NAME));

		SectionRequest sectionRequest = SectionRequest.of(exampleStation4.getId(), exampleStation5.getId(), 10);

		// when
		// 지하철_구간_생성_요청
		ExtractableResponse<Response> response = requestCreateSection(exampleLine1.getId(), sectionRequest);

		// then
		// 지하철_구간_생성됨
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
		);
	}

	@DisplayName("역과 역사이의 역을 삭제")
	@Test
	void deleteSectionCase1() {

		// given
		createSectionCase1();

		// when
		// 지하철_구간_삭제_요청
		ExtractableResponse<Response> response = requestDeleteSection(exampleLine1.getId(), exampleStation3.getId());

		// then
		// 지하철_구간_삭제됨
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
			() -> assertThat(stationResponses).hasSize(2),
			() -> assertThat(stationIds).isEqualTo(
				Arrays.asList(exampleStation1.getId(), exampleStation2.getId())),
			() -> assertThat(nextDistances).isEqualTo(
				Arrays.asList(100, 0)
			)
		);
	}

	@DisplayName("구간이 여러개인 노선에서 상행 종점 제거")
	@Test
	void deleteSectionCase2() {

		// given
		createSectionCase1();

		// when
		// 지하철_구간_삭제_요청
		ExtractableResponse<Response> response = requestDeleteSection(exampleLine1.getId(), exampleStation1.getId());

		// then
		// 지하철_구간_삭제됨
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
			() -> assertThat(stationResponses).hasSize(2),
			() -> assertThat(stationIds).isEqualTo(
				Arrays.asList(exampleStation3.getId(), exampleStation2.getId())),
			() -> assertThat(nextDistances).isEqualTo(
				Arrays.asList(90, 0)
			)
		);
	}

	@DisplayName("구간이 여러개인 노선에서 하행 종점 제거")
	@Test
	void deleteSectionCase3() {

		// given
		createSectionCase1();

		// when
		// 지하철_구간_삭제_요청
		ExtractableResponse<Response> response = requestDeleteSection(exampleLine1.getId(), exampleStation2.getId());

		// then
		// 지하철_구간_삭제됨
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
			() -> assertThat(stationResponses).hasSize(2),
			() -> assertThat(stationIds).isEqualTo(
				Arrays.asList(exampleStation1.getId(), exampleStation3.getId())),
			() -> assertThat(nextDistances).isEqualTo(
				Arrays.asList(10, 0)
			)
		);
	}

	@DisplayName("노선에 등록되지 않은 역 제거")
	@Test
	void deleteSectionThrow1() {
		// given
		createSectionCase1();
		StationResponse newStation = stationService.saveStation(StationRequest.of(EXAMPLE_STATION4_NAME));

		// when
		// 지하철_구간_삭제_요청
		ExtractableResponse<Response> response = requestDeleteSection(exampleLine1.getId(), newStation.getId());

		// then
		// 지하철_구간_삭제시 오류
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value())
		);
	}

	@DisplayName("구간이 하나인 경우에 역 제거")
	@Test
	void deleteSectionThrow2() {
		// given

		// when
		// 지하철_구간_삭제_요청
		ExtractableResponse<Response> response = requestDeleteSection(exampleLine1.getId(), exampleStation1.getId());

		// then
		// 지하철_구간_삭제시 오류
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
		);
	}

}
