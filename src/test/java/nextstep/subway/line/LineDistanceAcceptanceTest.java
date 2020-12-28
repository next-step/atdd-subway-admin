package nextstep.subway.line;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 조회 기능")
public class LineDistanceAcceptanceTest extends AcceptanceTest {

	private final String LINE_URL = "/lines";

	@DisplayName("노선 생성 시 종점역(상행, 하행)을 함께 추가하기")
	@Test
	void createLineWithTerminal() throws JsonProcessingException {
		Long upStationId = 역_생성_후_stationId응답("강변역");
		Long downStationId = 역_생성_후_stationId응답("건대입구역");
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse response = 노선_생성_함수("2호선", "bg-green-600", upStationId, downStationId, 3);
		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		ExtractableResponse<Response> lineListResponse = RestAssured
				.given().log().all()
				.when().get(LINE_URL)
				.then().log().all().extract();

		assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<LineResponse> lineResponse = lineListResponse.jsonPath().getList(".",LineResponse.class);
		assertThat(lineResponse.get(0).getStations().size()).isNotEqualTo(0);
	}

	private static Long 역_생성_후_stationId응답(String stationName) {
		Map<String, String> params = new HashMap<>();
		params.put("name", stationName);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post("/stations")
				.then().log().all()
				.extract();

		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
		return Long.valueOf(response.header("Location").split("/")[2]);
	}

	private ExtractableResponse 노선_생성_함수(String name, String color, long upStationId, long downStationId, int distance) throws JsonProcessingException {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);

		ExtractableResponse<Response> response = RestAssured.given().log().all().
				body(params).
				contentType(MediaType.APPLICATION_JSON_VALUE).
				when().
				post(LINE_URL).
				then().
				log().all().
				extract();
		return response;
	}

	@DisplayName("노선 조회 응답 결과에 등록된 구간을 참고하여 역 목록 응답 추가하기")
	@Test
	void listLineWithTerminal() throws JsonProcessingException {
		Long upStationId = 역_생성_후_stationId응답("강변역");
		Long downStationId = 역_생성_후_stationId응답("건대입구역");
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse response = 노선_생성_함수("2호선", "bg-green-600", upStationId, downStationId, 3);
		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		ExtractableResponse<Response> lineListResponse = RestAssured
				.given().log().all()
				.when().get(LINE_URL)
				.then().log().all().extract();

		assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<LineResponse> lineResponse = lineListResponse.jsonPath().getList(".",LineResponse.class);
		assertThat(lineResponse.get(0).getStations().size()).isNotEqualTo(0);
	}



}
