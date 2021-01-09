package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.common.exception.ErrorResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class SectionAcceptanceTest extends AcceptanceTest {
	private StationResponse 강남역;

	private StationResponse 광교역;

	private LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();
		// given
		강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역");
		광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역");

		LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse response = 노선_생성_함수(lineRequest);

		String lineId = response.header("Location").split("/")[2];
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(lineId));
		assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		신분당선 = lineResponse;
	}

	@DisplayName("노선에 구간을 등록한다.")
	@Test
	void addSection() {
		// when
		StationResponse 새로운역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운역");
		SectionRequest sectionRequest = new SectionRequest(광교역.getId(), 새로운역.getId(), 3);
		ExtractableResponse<Response> response = 구간등록하기(sectionRequest);

		// then
		// 지하철_노선에_지하철역_등록됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		String lineId = response.header("Location").split("/")[2];

		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(lineId));

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getName()).isEqualTo(신분당선.getName());

		//등록한 역 검증
		List<SectionResponse> sections = lineResponse.getStations().stream().collect(Collectors.toList());
		assertThat(sections.size()).isEqualTo(3);
		List<Long> expectedStationId = Arrays.asList(광교역.getId(), 강남역.getId(), 새로운역.getId());
		List<Long> resultLineIds = sections.stream()
				.map(it -> it.getId())
				.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedStationId);
	}

	ExtractableResponse<Response> 구간등록하기(SectionRequest section) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(section)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(String.format("/lines/%s/sections", String.valueOf(신분당선.getId())))
				.then().log().all()
				.extract();

		return response;
	}

	@DisplayName("새로운 역을 상행 종점으로 등록할 경우")
	@Test
	void addNewStationForUpTerminal() {
		StationResponse 새로운상행역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운상행역");
		SectionRequest sectionRequest = new SectionRequest(새로운상행역.getId(), 강남역.getId(), 3);

		ExtractableResponse<Response> response = 구간등록하기(sectionRequest);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(신분당선.getId());

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getName()).isEqualTo(신분당선.getName());

		List<SectionResponse> sections = lineResponse.getStations().stream().collect(Collectors.toList());
		assertThat(sections.size()).isEqualTo(3);
		SectionResponse section = sections.stream().filter(s -> s.getName().equalsIgnoreCase(새로운상행역.getName())).findAny().get();
		assertThat(section.getDistance()).isEqualTo(0);
	}

	@DisplayName("새로운 역을 행 종점으로 등록할 경우")
	@Test
	void addNewStationForDownTerminal() {
		StationResponse 새로운하행역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운하행역");
		SectionRequest sectionRequest = new SectionRequest(광교역.getId(), 새로운하행역.getId(), 3);

		ExtractableResponse<Response> response = 구간등록하기(sectionRequest);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(신분당선.getId());

		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getName()).isEqualTo(신분당선.getName());

		List<SectionResponse> sections = lineResponse.getStations().stream().collect(Collectors.toList());
		assertThat(sections.size()).isEqualTo(3);
		SectionResponse section = sections.stream().filter(s -> s.getName().equalsIgnoreCase(새로운하행역.getName())).findAny().get();
		assertThat(section.getDistance()).isEqualTo(3);
	}

	@DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
	@Test
	void betweenAddSectionExceptionDistanceError() {
		// when
		StationResponse 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역");
		SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 판교역.getId(), 15);

		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(sectionRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(String.format("/lines/%s/sections", String.valueOf(신분당선.getId())))
				.then().log().all()
				.extract();

		assertThat(response.jsonPath().getObject(".", ErrorResponse.class).getCode()).isEqualTo(700);
	}

	@DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
	@Test
	void alreadyExistsUpDownStation() {
		// when
		SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 광교역.getId(), 15);

		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(sectionRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(String.format("/lines/%s/sections", String.valueOf(신분당선.getId())))
				.then().log().all()
				.extract();

		assertThat(response.jsonPath().getObject(".", ErrorResponse.class).getCode()).isEqualTo(701);
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
	@Test
	void notIncludeLineBothStations() {
		StationResponse 청계산역 = StationAcceptanceTest.지하철역_등록되어_있음("청계산역");
		StationResponse 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역");
		SectionRequest sectionRequest = new SectionRequest(청계산역.getId(), 판교역.getId(), 15);

		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(sectionRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(String.format("/lines/%s/sections", String.valueOf(신분당선.getId())))
				.then().log().all()
				.extract();

		assertThat(response.jsonPath().getObject(".", ErrorResponse.class).getCode()).isEqualTo(702);
	}

	@DisplayName("노선의 구간을 제거하는 기능, 종점 제거")
	@Test
	void deleteSections() {
		Long lineId = 신분당선.getId();
		StationResponse 새로운역 = StationAcceptanceTest.지하철역_등록되어_있음("새로운역");
		SectionRequest sectionRequest = new SectionRequest(광교역.getId(), 새로운역.getId(), 3);

		ExtractableResponse<Response> postResponse = RestAssured.given().log().all()
				.body(sectionRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(String.format("/lines/%s/sections", String.valueOf(신분당선.getId())))
				.then().log().all()
				.extract();
		assertThat(postResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(Long.valueOf(postResponse.header("Location").split("/")[2]));
		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		lineResponse.getStations().size();

		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.when()
				.delete(String.format("/%d/sections?stationId=%d", lineId, 강남역.getId()))
				.then().log().all()
				.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}


}
