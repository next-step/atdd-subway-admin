package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
	private Map<String, String> createParams;

	private StationResponse 강남역;
	private StationResponse 광교역;

	private ExtractableResponse<Response> 신분당선;

	@BeforeEach
	void setBeforeEach() {
		super.setUp();

		// given
		강남역 = StationAcceptanceTest.지하철역을_생성한다("강남역").as(StationResponse.class);
		광교역 = StationAcceptanceTest.지하철역을_생성한다("광교역").as(StationResponse.class);

		createParams = new HashMap<>();
		createParams.put("name", "신분당선");
		createParams.put("color", "bg-red-600");
		createParams.put("upStationId", 강남역.getId().toString());
		createParams.put("downStationId", 광교역.getId().toString());
		createParams.put("distance", "10");
		신분당선 = LineAcceptanceTest.노선을_생성한다(createParams);
	}

	@Test
	@DisplayName("노선에 구간을 등록한다.")
	void addSection() {
		// given
		StationResponse 양재역 = StationAcceptanceTest.지하철역을_생성한다("양재역").as(StationResponse.class);

		Map<String, String> sectionParams = new HashMap<>();
		sectionParams.put("upStationId", 강남역.getId().toString());
		sectionParams.put("downStationId", 양재역.getId().toString());
		sectionParams.put("distance", "3");

		// when
		ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(신분당선.header("Location"), sectionParams);

		// then
		노선에_새로_등록된_역이_순서대로_조회된다(response, 강남역.getName(), 양재역.getName(), 광교역.getName());
	}

	private void 노선에_새로_등록된_역이_순서대로_조회된다(ExtractableResponse<Response> addSectionResponse, String... stations) {
		ExtractableResponse<Response> 신분당선_조회_결과 = LineAcceptanceTest.특정_노선을_조회한다(신분당선.header("Location"));
		assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(신분당선_조회_결과.jsonPath().getList("stations.name")).containsExactly(Arrays.stream(stations).toArray());
	}

	private ExtractableResponse<Response> 지하철_노선에_새로운_구간_등록_요청(String resourcePath, Map<String, String> sectionParams) {
		return RestAssured.given().log().all()
				.body(sectionParams)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post(resourcePath + "/sections")
				.then().log().all()
				.extract();
	}

	@Test
	@DisplayName("새로운 역을 상행 종점으로 등록한다")
	void addSection2() {
		// given
		StationResponse 강남위역 = StationAcceptanceTest.지하철역을_생성한다("강남위역").as(StationResponse.class);

		Map<String, String> sectionParams = new HashMap<>();
		sectionParams.put("upStationId", 강남위역.getId().toString());
		sectionParams.put("downStationId", 강남역.getId().toString());
		sectionParams.put("distance", "3");

		// when
		ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(신분당선.header("Location"), sectionParams);

		// then
		노선에_새로_등록된_역이_순서대로_조회된다(response, 강남위역.getName(), 강남역.getName(), 광교역.getName());
	}

	@Test
	@DisplayName("새로운 역을 하행 종점으로 등록한다")
	void addSection3() {
		// given
		StationResponse 광교아래역 = StationAcceptanceTest.지하철역을_생성한다("광교아래역").as(StationResponse.class);

		Map<String, String> sectionParams = new HashMap<>();
		sectionParams.put("upStationId", 광교역.getId().toString());
		sectionParams.put("downStationId", 광교아래역.getId().toString());
		sectionParams.put("distance", "3");

		// when
		ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_등록_요청(신분당선.header("Location"), sectionParams);

		// then
		노선에_새로_등록된_역이_순서대로_조회된다(response, 강남역.getName(), 광교역.getName(), 광교아래역.getName());
	}
}
