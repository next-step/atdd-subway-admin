package nextstep.subway.section;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("구간 관리 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	StationResponse 강남역;
	StationResponse 광교역;
	StationResponse 양재역;
	StationResponse 판교역;
	LineResponse 신분당선;

	@BeforeEach
	public void setUp() {
		super.setUp();

		//given
		강남역 = StationAcceptanceTest.지하철역_등록("강남역").as(StationResponse.class);
		광교역 = StationAcceptanceTest.지하철역_등록("광교역").as(StationResponse.class);
		양재역 = StationAcceptanceTest.지하철역_등록("양재역").as(StationResponse.class);
		판교역 = StationAcceptanceTest.지하철역_등록("판교역").as(StationResponse.class);

		신분당선 = LineAcceptanceTest.지하철_노선_등록되어있음("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10)
			.as(LineResponse.class);
	}

	@DisplayName("역 사이에 새로운 역을 등록할 경우")
	@Test
	void addSectionAtMiddle() {
		SectionRequest request = new SectionRequest(강남역.getId(), 양재역.getId(), 3);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_역_등록_요청(request);

		// then
		// 새로운 역이 등록되었는지 확인
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		지하철_노선에_등록한_구간이_포함(response, Arrays.asList(강남역.getId(), 양재역.getId(), 광교역.getId()));
	}

	@DisplayName("새로운 역을 상행 종점으로 등록할 경우")
	@Test
	void addSectionAtFirst() {
		SectionRequest request = new SectionRequest(양재역.getId(), 강남역.getId(), 3);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_역_등록_요청(request);

		// then
		// 새로운 역이 등록되었는지 확인
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		지하철_노선에_등록한_구간이_포함(response, Arrays.asList(양재역.getId(), 강남역.getId(), 광교역.getId()));
	}

	@DisplayName("새로운 역을 하행 종점으로 등록할 경우")
	@Test
	void addSectionAtLast() {
		SectionRequest request = new SectionRequest(광교역.getId(), 양재역.getId(), 3);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_역_등록_요청(request);

		// then
		// 새로운 역이 등록되었는지 확인
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		지하철_노선에_등록한_구간이_포함(response, Arrays.asList(강남역.getId(), 광교역.getId(), 양재역.getId()));
	}

	@DisplayName("역 사이에 새로운 역 등록시, 기존역 사이 길이보다 크거나 같으면 등록할 수 없는지 테스트")
	@Test
	void addSectionWithErrorDistance() {
		SectionRequest request = new SectionRequest(강남역.getId(), 양재역.getId(), 11);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_역_등록_요청(request);

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
	}

	@DisplayName("상행역과 하행역이 이미 노선에 등록되어 있다면 추가할 수 없는지 테스트")
	@Test
	void addSectionWithExistStations() {
		SectionRequest request = new SectionRequest(강남역.getId(), 광교역.getId(), 11);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_역_등록_요청(request);

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없는지 테스트")
	@Test
	void addSectionWithNotExistStations() {
		SectionRequest request = new SectionRequest(양재역.getId(), 판교역.getId(), 11);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_역_등록_요청(request);

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@DisplayName("중간에 있는 역을 제거할 경우")
	@Test
	void removeSectionAtMiddle() {
		// given
		SectionRequest request = new SectionRequest(강남역.getId(), 양재역.getId(), 3);
		지하철_노선에_역_등록_요청(request);

		// when
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().delete("/lines/" + 신분당선.getId() + "/sections?stationId=" + 양재역.getId())
			.then().log().all().extract();

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 지하철_노선에_역_등록_요청(SectionRequest request) {
		return RestAssured
			.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines/" + 신분당선.getId() + "/sections")
			.then().log().all().extract();
	}

	private void 지하철_노선에_등록한_구간이_포함(ExtractableResponse<Response> response, List<Long> expectedStationIds) {
		List<Long> resultStationIds = response.jsonPath().getList("sections", SectionResponse.class).stream()
			.map(SectionResponse::getId)
			.collect(Collectors.toList());
		Assertions.assertThat(resultStationIds).isEqualTo(expectedStationIds);
	}
}

