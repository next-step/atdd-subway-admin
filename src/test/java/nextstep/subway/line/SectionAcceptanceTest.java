package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

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
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 3);

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
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 10);

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
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선.getId(), 양재역.getId(), 광교역.getId(), 10);

		// then
		지하철_노선에_구간_등록됨(response);
	}

	@ParameterizedTest
	@DisplayName("기존 구간사이 길이와 같거나 더 긴 길이로 역을 등록한다.")
	@ValueSource(ints = {10, 11})
	void addSectionEqualDistance(int longDistance) {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), longDistance);

		// then
		지하철_노선에_구간_등록_실패됨(response);
	}

	@DisplayName("상행역 하행역 모두 노선에 존재하는 구간을 등록한다.")
	@Test
	void addSectionAlreadyExist() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 5)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선.getId(), 강남역.getId(), 광교역.getId(), 5);

		// then
		지하철_노선에_구간_등록_실패됨(response);
	}

	@DisplayName("상행역 하행역 모두 노선에 존재 하지 않는 구간을 등록한다.")
	@Test
	void addSectionNotExist() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		StationResponse 판교역 = 지하철역_생성_요청("판교역").as(StationResponse.class);
		StationResponse 정자역 = 지하철역_생성_요청("정자역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 5)
			.as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선.getId(), 판교역.getId(), 정자역.getId(), 5);

		// then
		지하철_노선에_구간_등록_실패됨(response);
	}

	@DisplayName("노선의 중간 역을 제거한다.")
	@Test
	void removeInnerStationInLine() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10).as(LineResponse.class);
		지하철_노선에_구간_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_제거_요청(신분당선.getId(), 양재역.getId());

		// then
		지하철_노선에_구간이_제거됨(response, 신분당선.getId(), 강남역.getId(), 광교역.getId());
	}

	@DisplayName("노선의 종점역을 제거한다.")
	@Test
	void removeEndStationInLine() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10).as(LineResponse.class);
		지하철_노선에_구간_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_제거_요청(신분당선.getId(), 강남역.getId());

		// then
		지하철_노선에_구간이_제거됨(response, 신분당선.getId(), 양재역.getId(), 광교역.getId());
	}

	@DisplayName("노선에 등록되어 있지 않은 역을 제거한다.")
	@Test
	void removeUnknownStationInLine() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		StationResponse 목동역 = 지하철역_생성_요청("목동역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10).as(LineResponse.class);
		지하철_노선에_구간_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_제거_요청(신분당선.getId(), 목동역.getId());

		// then
		지하철_노선에_구간이_제거_실패됨(response);
	}

	@DisplayName("구간이 하나인 노선에 역을 제거한다.")
	@Test
	void removeStationInLine() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 광교역 = 지하철역_생성_요청("광교역").as(StationResponse.class);
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10).as(LineResponse.class);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_제거_요청(신분당선.getId(), 강남역.getId());

		// then
		지하철_노선에_구간이_제거_실패됨(response);
	}

	@DisplayName("데이터베이스에 저장되지 없는 노선을 제거한다.")
	@Test
	void removeNonExistsLine() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		long DB에_저장되지_않은_노선_ID = 1L;

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_제거_요청(DB에_저장되지_않은_노선_ID, 강남역.getId());

		// then
		지하철_노선에_구간이_제거_실패됨(response);
	}

	@DisplayName("데이터베이스에 저장되지 없는 역을 제거한다.")
	@Test
	void removeNonExistsStation() {
		// given
		StationResponse 강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
		StationResponse 양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
		long DB에_저장되지_않은_지하철역_ID = 3L;
		LineResponse 신분당선 = 지하철_노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10).as(LineResponse.class);
		지하철_노선에_구간_등록_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

		// when
		ExtractableResponse<Response> response = 지하철_노선에_구간_제거_요청(신분당선.getId(), DB에_저장되지_않은_지하철역_ID);

		// then
		지하철_노선에_구간이_제거_실패됨(response);
	}

	private ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(long lineId, long upStationId, long downStationId, int distance) {
		Map<String, Object> params = new HashMap<>();
		params.put("downStationId", downStationId);
		params.put("upStationId", upStationId);
		params.put("distance", distance);

		return RestAssured
		        .given().log().all()
		        .body(params)
		        .contentType(MediaType.APPLICATION_JSON_VALUE)
		        .when().post(LINE_API_ROOT + "/" + lineId + "/sections")
		        .then().log().all().extract();
	}

	private ExtractableResponse<Response> 지하철_노선에_구간_제거_요청(long lineId, long stationId) {
		// when
		Map<String, String> params = new HashMap<>();
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().delete(LINE_API_ROOT + "/" + lineId + "/sections" + "?stationId=" + stationId)
			.then().log().all().extract();
	}

	private void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(OK.value());
	}

	private void 지하철_노선에_구간_등록_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
	}

	private void 지하철_노선에_구간이_제거됨(ExtractableResponse<Response> response, Long lineId, Long... stations) {
		assertThat(response.statusCode()).isEqualTo(OK.value());
		ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(lineId);
		지하철_노선_응답됨(lineResponse, stations);
	}

	private void 지하철_노선에_구간이_제거_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
	}
}
