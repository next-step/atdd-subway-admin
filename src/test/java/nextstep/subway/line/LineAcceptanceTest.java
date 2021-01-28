package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	private LineRequest 신분당선;
	private LineRequest 일호선;
	private StationResponse 강남역;
	private StationResponse 판교역;
	private StationResponse 온수역;
	private StationResponse 오류역;

	@BeforeEach
	void setup() {
		강남역 = StationAcceptanceTest.지하철역_생성_요청("강남역").as(StationResponse.class);
		판교역 = StationAcceptanceTest.지하철역_생성_요청("판교역").as(StationResponse.class);
		온수역 = StationAcceptanceTest.지하철역_생성_요청("온수역").as(StationResponse.class);
		오류역 = StationAcceptanceTest.지하철역_생성_요청("오류역").as(StationResponse.class);

		신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 판교역.getId(), 10);
		일호선 = new LineRequest("1호선", "bg-blue-600", 온수역.getId(), 오류역.getId(), 10);
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response = 노선_생성_요청(신분당선);

		// then
		노선_생성_성공(response);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLineWithDuplicateName() {
		// given
		노선_생성_요청(신분당선);

		// when
		ExtractableResponse<Response> response = 노선_생성_요청(신분당선);

		// then
		노선_생성_실패(response);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> createResponse1 = 노선_생성_요청(신분당선);
		ExtractableResponse<Response> createResponse2 = 노선_생성_요청(일호선);

		// when
		ExtractableResponse<Response> response = 노선_목록_조회_요청();

		// then
		노선_목록_조회_성공(createResponse1, createResponse2, response);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선_생성_요청(신분당선);

		// when
		ExtractableResponse<Response> response = 노선_조회_요청(createResponse);

		// then
		노선_조회_성공(response, Arrays.asList(강남역, 판교역));
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선_생성_요청(신분당선);
		String id = getIdByLocation(createResponse);

		// when
		ExtractableResponse<Response> response = 노선_수정_요청(id, 일호선);

		// then
		노선_수정_성공(id, 일호선.getName(), 일호선.getColor(), response);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선_생성_요청(신분당선);
		String id = getIdByLocation(createResponse);

		// when
		ExtractableResponse<Response> response = 노선_제거_요청(id);

		// then
		노선_제거_성공(response);
	}

	private String getIdByLocation(ExtractableResponse<Response> createResponse) {
		return createResponse.header(HttpHeaders.LOCATION).split("/")[2];
	}

	public static ExtractableResponse<Response> 노선_생성_요청(LineRequest lineRequest) {
		return RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private void 노선_생성_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	private void 노선_생성_실패(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> 노선_목록_조회_요청() {
		return RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	private void 노선_목록_조회_성공(ExtractableResponse<Response> createResponse1,
		ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
			.map(it -> Long.parseLong(getIdByLocation(it)))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	public static ExtractableResponse<Response> 노선_조회_요청(ExtractableResponse<Response> response) {
		return RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(response.header("Location"))
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 노선_조회_요청(LineResponse line) {
		return RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get(URI.create("/lines/" + line.getId()))
			.then().log().all()
			.extract();
	}

	public static void 노선_조회_성공(ExtractableResponse<Response> response, List<StationResponse> stations) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		LineResponse line = response.as(LineResponse.class);
		assertThat(line.getId()).isNotNull();

		List<Long> stationIds = line.getStations().stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());

		List<Long> expectedStationIds = stations.stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());

		assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
	}

	private ExtractableResponse<Response> 노선_수정_요청(String id, LineRequest lineRequest) {
		return RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(lineRequest)
			.when()
			.put(URI.create("/lines/" + id))
			.then().log().all()
			.extract();
	}

	private void 노선_수정_성공(String id, String updatedName, String updatedColor, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getId()).isEqualTo(Long.parseLong(id));
		assertThat(lineResponse.getName()).isEqualTo(updatedName);
		assertThat(lineResponse.getColor()).isEqualTo(updatedColor);
	}

	private ExtractableResponse<Response> 노선_제거_요청(String id) {
		return RestAssured.given().log().all()
			.when()
			.delete(URI.create("/lines/" + id))
			.then().log().all()
			.extract();
	}

	private void 노선_제거_성공(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
