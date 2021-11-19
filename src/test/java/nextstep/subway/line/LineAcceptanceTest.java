package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	private static final String LINE_PATH = "/lines";
	private static final String STATION_PATH = "/stations";
	private static final String SLASH = "/";
	private static StationResponse 강남역;
	private static StationResponse 광교역;
	private static StationResponse 성수역;

	@BeforeEach
	void setup() {
		강남역 = 지하철역이_등록되어있음(강남역_생성_요청값());
		광교역 = 지하철역이_등록되어있음(광교역_생성_요청값());
		성수역 = 지하철역이_등록되어있음(성수역_생성_요청값());
	}

	private StationResponse 지하철역이_등록되어있음(StationRequest params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(STATION_PATH)
			.then().log().all().extract().as(StationResponse.class);
	}

	StationRequest 강남역_생성_요청값() {
		return new StationRequest("강남역");
	}

	StationRequest 광교역_생성_요청값() {
		return new StationRequest("광교역");
	}

	StationRequest 성수역_생성_요청값() {
		return new StationRequest("성수역");
	}

	LineRequest 이호선_생성_요청값(StationResponse upStation, StationResponse downStation) {
		return new LineRequest("2호선", "bg-green-600", upStation.getId(), downStation.getId(), 20);
	}

	LineRequest 신분당선_생성_요청값(StationResponse upStation, StationResponse downStation) {
		return new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10);
	}

	@DisplayName("존재하지 않는 역을 종점역으로 가 지하철 노선을 생성한다.")
	@Test
	void createLineNoSuchStation() {
		// given
		StationResponse 을지로입구역 = new StationResponse();
		StationResponse 왕십리역 = new StationResponse();

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_생성_요청값(을지로입구역, 왕십리역));

		// then
		지하철_노선_생성_실패됨(response);
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_생성_요청값(강남역, 광교역));

		// then
		지하철_노선_생성됨(response);
	}

	ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(LINE_PATH)
			.then().log().all().extract();
	}

	void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		지하철_노선_생성_요청(이호선_생성_요청값(강남역, 성수역));

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(이호선_생성_요청값(강남역, 성수역));

		// then
		지하철_노선_생성_실패됨(response);
	}

	void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		LineResponse 이호선_등록되어_있음 = 지하철_노선_등록되어_있음(이호선_생성_요청값(강남역, 성수역));
		LineResponse 신분당선_등록되어_있음 = 지하철_노선_등록되어_있음(신분당선_생성_요청값(강남역, 광교역));

		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

		// then
		지하철_노선_목록_응답됨(response);
		지하철_노선_목록_포함됨(response, 이호선_등록되어_있음, 신분당선_등록되어_있음);
	}

	LineResponse 지하철_노선_등록되어_있음(LineRequest params) {
		return 지하철_노선_생성_요청(params).as(LineResponse.class);
	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		return RestAssured
			.given().log().all()
			.when()
			.get(LINE_PATH)
			.then().log().all().extract();
	}

	void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, LineResponse... createdLine) {
		List<Long> createdLineIdList = Stream.of(createdLine)
			.map(LineResponse::getId)
			.collect(Collectors.toList());

		List<Long> responseLineIdList = response.jsonPath()
			.getList(".", LineResponse.class)
			.stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());

		assertThat(responseLineIdList.containsAll(createdLineIdList)).isTrue();
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		LineResponse 이호선_노선_등록_응답 = 지하철_노선_등록되어_있음(이호선_생성_요청값(강남역, 성수역));

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선_노선_등록_응답.getId());

		// then
		지하철_노선_응답됨(response);
		지하철_역목록_포함됨(response);
	}

	ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get(LINE_PATH + SLASH + id)
			.then().log().all().extract();
	}

	void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	void 지하철_역목록_포함됨(ExtractableResponse<Response> response) {
		LineResponse expectResponse = response.as(LineResponse.class);
		assertAll(
			() -> assertThat(expectResponse.getStations()).hasSize(2),
			() -> assertThat(expectResponse.getStations().get(0).getId().equals(강남역.getId())).isTrue(),
			() -> assertThat(expectResponse.getStations().get(1).getId().equals(성수역.getId())).isTrue()
		);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		LineResponse 이호선 = 지하철_노선_등록되어_있음(이호선_생성_요청값(강남역, 성수역));

		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(이호선.getId(), 신분당선_생성_요청값(강남역, 광교역));

		// then
		지하철_노선_수정됨(response, 이호선);
	}

	ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put(LINE_PATH + SLASH + id)
			.then().log().all().extract();
	}

	void 지하철_노선_수정됨(ExtractableResponse<Response> response, LineResponse expectLine) {
		LineResponse lineResponse = response.as(LineResponse.class);

		assertAll(
			() -> assertThat(lineResponse.getId()).isEqualTo(expectLine.getId()),
			() -> assertThat(lineResponse.getName()).isNotEqualTo(expectLine.getName()),
			() -> assertThat(lineResponse.getName()).isEqualTo(신분당선_생성_요청값(강남역, 광교역).getName())
		);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		LineResponse 이호선 = 지하철_노선_등록되어_있음(이호선_생성_요청값(강남역, 성수역));

		// when
		ExtractableResponse<Response> response = 지하철_노선_제거_요청(이호선.getId());

		// then
		지하철_노선_삭제됨(response);
	}

	ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().delete(LINE_PATH + SLASH + id)
			.then().log().all().extract();
	}

	void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("기존에 존재하지 않는 지하철 노선을 수정한다.")
	@Test
	void updateLineNullException() {
		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(2L, 이호선_생성_요청값(강남역, 성수역));

		// then
		지하철_노선_수정_실패됨(response);
	}

	void 지하철_노선_수정_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@DisplayName("기존에 존재하지 않는 지하철 노선을 삭제한다.")
	@Test
	void deleteLineNullException() {
		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(2L, 이호선_생성_요청값(강남역, 성수역));

		// then
		지하철_노선_삭제_실패됨(response);
	}

	void 지하철_노선_삭제_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
}
