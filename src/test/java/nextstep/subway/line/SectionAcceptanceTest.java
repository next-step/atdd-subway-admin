package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

@Disabled("아직 구현코드가 없음")
@DisplayName("지하철 노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

	@DisplayName("역 사이에 새로운 역이 추가되도록 구간을 등록한다.")
	@Test
	void addSectionBetweenStationAndStation() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 5)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 양재역, 3);

		// then
		지하철_노선에_구간_등록됨(response);
	}

	@DisplayName("추가하는 역이 상행 종점이 되도록 구간을 등록한다.")
	@Test
	void addSectionUpStation() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 양재역.getId(), 광교역.getId(), 5)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 양재역, 10);

		// then
		지하철_노선에_구간_등록됨(response);
	}

	@DisplayName("추가하는 역이 하행 종점이 되도록 구간을 등록한다.")
	@Test
	void addSectionDownStation() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 5)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 양재역, 광교역, 10);

		// then
		지하철_노선에_구간_등록됨(response);
	}

	@DisplayName("기존 역 사이 길이와 같은 길이로 역을 등록한다.")
	@Test
	void addSectionEqualDistance() {
		// given
		int distance = 5;
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), distance)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 양재역, distance);

		// then
		지하철_노선에_구간_등록_실패됨(response);
	}

	@DisplayName("상행역 하행역 모두 노선에 존재하는 구간을 등록한다.")
	void addSectionAlreadyExist() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 5)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 광교역, 5);

		// then
		지하철_노선에_구간_등록_실패됨(response);
	}

	@DisplayName("상행역 하행역 모두 노선에 존재 하지 않는 구간을 등록한다.")
	void addSectionNotExist() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		StationResponse 판교역 = 지하철역_생성_요청("판교역").as(StationResponse.class);
		StationResponse 정자역 = 지하철역_생성_요청("정자역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 5)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 판교역, 정자역, 5);

		// then
		지하철_노선에_구간_등록_실패됨(response);
	}

	private ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(LineResponse line, StationResponse upStation,
		StationResponse downStation, int distance) {
		Map<String, Object> params = new HashMap<>();
		params.put("downStationId", downStation.getId());
		params.put("upStation", upStation.getId());
		params.put("distance", distance);

		return RestAssured
		        .given().log().all()
		        .body(params)
		        .contentType(MediaType.APPLICATION_JSON_VALUE)
		        .when().post("/lines/" + line.getId() + "/sections")
		        .then().log().all().extract();
	}

	private void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 지하철_노선에_구간_등록_실패됨(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
