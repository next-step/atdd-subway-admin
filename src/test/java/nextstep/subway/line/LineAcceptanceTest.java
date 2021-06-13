package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given, when 지하철_노선_생성_요청 + 상행, 하행 정보 요청 파라미터에 함께 추가
		StationRequest stationRequest = new StationRequest();
		stationRequest.name("잠실역");
		지하철역_생성_요청(stationRequest);

		LineRequest lineRequest = new LineRequest("2호선", "yellow", 1L, 1L, 1);
		ExtractableResponse<Response> response = 노선정보세팅_메소드(lineRequest);

		// then 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given 지하철_노선_등록되어_있음 + 상행, 하행 정보 요청 파라미터에 함께 추가
		StationRequest stationRequest = new StationRequest();
		stationRequest.name("잠실역");
		지하철역_생성_요청(stationRequest);

		String dupName = "2호선";
		String dupColor = "yellow";
		LineRequest lineRequest = new LineRequest(dupName, dupColor, 1L, 1L, 1);
		노선정보세팅_메소드(lineRequest);

		// when 중복 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 노선정보세팅_메소드(lineRequest);

		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음 + 상행, 하행 정보 요청 파라미터에 함께 추가
		StationRequest stationRequest = new StationRequest();
		stationRequest.name("잠실역");
		지하철역_생성_요청(stationRequest);

		LineRequest lineRequest = new LineRequest("2호선", "yellow", 1L, 1L, 1);
		ExtractableResponse<Response> createdResponse1 = 노선정보세팅_메소드(lineRequest);

		// 지하철_노선_등록되어_있음 + 상행, 하행 정보 요청 파라미터에 함께 추가
		LineRequest lineRequest2 = new LineRequest("8호선", "yellow", 1L, 1L, 1);
		ExtractableResponse<Response> createdResponse2 = 노선정보세팅_메소드(lineRequest2);

		// when
		// 지하철_노선_목록_조회_요청 + 상행 -> 하행역 순으로 정렬되어야 함
		ExtractableResponse<Response> response = get메소드호출("/lines");

		// then
		// 지하철_노선_목록_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 지하철_노선_목록_포함됨
		List<Long> expectedLineIds = Arrays.asList(createdResponse1, createdResponse2).stream()
				.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
				.collect(Collectors.toList());

		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class)
				.stream()
				.map(it -> it.id())
				.collect(Collectors.toList());

		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음 + 상행, 하행 정보 요청 파라미터에 함께 추가
		StationRequest stationRequest = new StationRequest();
		stationRequest.name("잠실역");
		지하철역_생성_요청(stationRequest);

		String name = "2호선";
		LineRequest lineRequest = new LineRequest(name, "yellow", 1L, 1L, 1);
		노선정보세팅_메소드(lineRequest);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = get메소드호출("/lines/" + name);

		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		String resultLineName = response.jsonPath().getString("name");

		assertThat(resultLineName).isEqualTo(name);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음 + 상행, 하행 정보 요청 파라미터에 함께 추가
		StationRequest stationRequest = new StationRequest();
		stationRequest.name("잠실역");
		지하철역_생성_요청(stationRequest);

		String name = "2호선";
		LineRequest lineRequest = new LineRequest(name, "yellow", 1L, 1L, 1);
		노선정보세팅_메소드(lineRequest);

		// when;
		// 지하철_노선_수정_요청
		String newColor = "puple";
		LineRequest lineChangeRequest = new LineRequest(name, newColor, 1L, 1L, 1);

		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(lineChangeRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.patch("/lines")
				.then().log().all()
				.extract();

		// then
		// 지하철_노선_수정됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음 + 상행, 하행 정보 요청 파라미터에 함께 추가
		StationRequest stationRequest = new StationRequest();
		stationRequest.name("잠실역");
		지하철역_생성_요청(stationRequest);

		String name = "2호선";
		LineRequest lineRequest = new LineRequest(name, "yellow", 1L, 1L, 1);
		노선정보세팅_메소드(lineRequest);

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.delete("/lines/" + name)
				.then().log().all()
				.extract();

		// then
		// 지하철_노선_삭제됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> 노선정보세팅_메소드(LineRequest request) {
		return RestAssured.given().log().all()
				.body(request)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post("/lines")
				.then().log().all()
				.extract();
	}

	private ExtractableResponse<Response> 지하철역_생성_요청(StationRequest request) {
		return  RestAssured.given().log().all()
				.body(request)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post("/stations")
				.then().log().all()
				.extract();
	}

	public ExtractableResponse<Response> get메소드호출(String path) {
		return RestAssured.given().log().all()
				.when()
				.get(path)
				.then().log().all()
				.extract();
	}
}
