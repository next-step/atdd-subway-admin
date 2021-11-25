package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

	private static final String STATION_PATH = "/stations";
	private static final String SLASH = "/";

	public static StationResponse 지하철역이_등록되어있음(StationRequest params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(STATION_PATH)
			.then().log().all().extract().as(StationResponse.class);
	}

	public static StationRequest 강남역_생성_요청값() {
		return new StationRequest("강남역");
	}

	public static StationRequest 광교역_생성_요청값() {
		return new StationRequest("광교역");
	}

	public static StationRequest 성수역_생성_요청값() {
		return new StationRequest("성수역");
	}

	public static StationRequest 양재역_생성_요청값() {
		return new StationRequest("양재역");
	}

	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		ExtractableResponse<Response> response = 지하철역_생성_요청(강남역_생성_요청값());

		// then
		지하철역이_생성됨(response);
	}

	ExtractableResponse<Response> 지하철역_생성_요청(StationRequest params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post(STATION_PATH)
			.then().log().all()
			.extract();
	}

	void 지하철역이_생성됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		지하철역_생성_요청(강남역_생성_요청값());

		// when
		ExtractableResponse<Response> response = 지하철역_생성_요청(강남역_생성_요청값());

		// then
		지하철역_생성_실패함(response);
	}

	void 지하철역_생성_실패함(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		/// given
		ExtractableResponse<Response> createResponse1 = 지하철역_생성_요청(강남역_생성_요청값());
		ExtractableResponse<Response> createResponse2 = 지하철역_생성_요청(광교역_생성_요청값());

		// when
		ExtractableResponse<Response> response = 지하철역_리스트를_조회함();

		// then
		지하철역_리스트가_조회됨(response, createResponse1, createResponse2);
	}

	ExtractableResponse<Response> 지하철역_리스트를_조회함() {
		return RestAssured.given().log().all()
			.when()
			.get(STATION_PATH)
			.then().log().all()
			.extract();
	}

	void 지하철역_리스트가_조회됨(ExtractableResponse<Response> response, ExtractableResponse<Response> createResponse1,
		ExtractableResponse<Response> createResponse2) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		StationResponse stationResponse = 지하철역이_등록되어있음(강남역_생성_요청값());

		// when
		ExtractableResponse<Response> response = 지하철역을_삭제함(stationResponse.getId());

		// then
		지하철역이_삭제됨(response);
	}

	ExtractableResponse<Response> 지하철역을_삭제함(Long id) {
		return RestAssured.given().log().all()
			.when()
			.delete(STATION_PATH + SLASH + id)
			.then().log().all()
			.extract();
	}

	void 지하철역이_삭제됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
