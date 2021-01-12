package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when : 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green");

		// then : 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("지하철 노선을 종점역(상행, 하행, 거리)을 포함하여 생성한다.")
	@Test
	void createLineWithLastStop() {
		// when : 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green",
			"강남역", "역삼역", 10);

		// then : 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given : 지하철_노선_등록되어_있음
		Long id = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green"));

		// when : 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green");

		// then : 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("상행과 하행역을 동일하게 설정하여 지하철 노선을 생성한다.")
	@Test
	void createLineWithSameUpDownStation() {
		// given : 지하철_역_등록되어_있음
		long 강남역_ID = location_header에서_ID_추출(StationAcceptanceTest.지하철_생성_요청("강남역"));

		// when : 지하철_노선_생성_요청 && 상행_하행역_동일
		ExtractableResponse<Response> response =
			지하철노선_생성_요청("2호선", "green", 강남역_ID, 강남역_ID, 10);

		// then : 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given : 지하철_노선_등록되어_있음
		List<Long> 지하철노선_ID_목록 = Stream.of(
			지하철노선_생성_요청("2호선", "green"),
			지하철노선_생성_요청("5호선", "purple")
		).map(AcceptanceTest::location_header에서_ID_추출)
			.collect(Collectors.toList());

		// when : 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();

		// then : 지하철_노선_목록_응답됨 & 지하철_노선_목록_포함됨
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("id", Long.class)).containsAll(지하철노선_ID_목록)
		);
	}

	@DisplayName("종점역을 포함하는 종점역을 포함한 지하철 노선 목록을 조회한다.")
	@Test
	void getLinesWithLastStop() {
		// given : 지하철_노선_등록되어_있음
		List<Long> 지하철노선_ID_목록 = Stream.of(
			지하철노선_생성_요청("2호선", "green", "강남역", "역삼역", 10),
			지하철노선_생성_요청("5호선", "purple", "천호역", "군자역", 15)
		).map(AcceptanceTest::location_header에서_ID_추출)
			.collect(Collectors.toList());

		// when : 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();

		// then : 지하철_노선_목록_응답됨 & 지하철_노선_목록_포함됨 & 지하철_노선_지하철역_목록_포함됨
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("id", Long.class)).containsAll(지하철노선_ID_목록),
			() -> assertThat(response.jsonPath().getList("stations.name", List.class))
				.map(list -> list.stream().sorted().collect(Collectors.joining(",")))
				.containsAll(Arrays.asList("강남역,역삼역", "군자역,천호역"))
		);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given : 지하철_노선_등록되어_있음
		long 이호선_ID = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green"));

		// when : 지하철_노선_조회_요청
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선_ID);

		// then : 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

	}

	@DisplayName("종점역을 포함하는 지하철 노선을 조회한다.")
	@Test
	void getLineWithLastStop() {
		// given : 지하철_노선_등록되어_있음
		long 이호선_ID = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green",
			"강남역", "역삼역", 10));

		// when : 지하철_노선_조회_요청
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(이호선_ID);

		// then : 지하철_노선_응답됨 & 지하철_노선_지하철역_목록_포함됨
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList("stations.name", String.class))
				.containsAll(Arrays.asList("강남역", "역삼역"))
		);
	}

	@DisplayName("존재하지 않는 지하철 노선을 조회한다.")
	@Test
	void getNotExistLine() {
		// when : 지하철_노선_조회_요청
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(999);

		// then : 지하철_노선_조회_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given : 지하철_노선_등록되어_있음
		long 이호선_ID = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green"));

		// when : 지하철_노선_수정_요청
		Map<String, String> params = new HashMap<>();
		params.put("name", "구분당선");
		params.put("color", "bg-blue-600");
		RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/lines/" + 이호선_ID)
			.then().log().all().extract();

		// then : 지하철_노선_수정됨
		assertThat(지하철_노선_조회_요청(이호선_ID).jsonPath().<String>get("name")).isEqualTo("구분당선");
	}

	@DisplayName("존재하지 않는 지하철 노선을 수정한다.")
	@Test
	void updateNotExistLine() {
		// when : 지하철_노선_수정_요청
		Map<String, String> params = new HashMap<>();
		params.put("name", "구분당선");
		params.put("color", "bg-blue-600");
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/lines/999")
			.then().log().all().extract();

		// then : 지하철_노선_수정_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given : 지하철_노선_등록되어_있음
		long 이호선_ID = location_header에서_ID_추출(지하철노선_생성_요청("2호선", "green"));

		// when : 지하철_노선_제거_요청
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().delete("/lines/" + 이호선_ID)
			.then().log().all().extract();

		// then : 지하철_노선_삭제됨
		assertThat(지하철_노선_조회_요청(이호선_ID).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(long id) {
		return RestAssured
			.given().log().all()
			.when().get("/lines/" + id)
			.then().log().all().extract();
	}

	public static ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);

		return 지하철노선_생성_요청(params);
	}

	public static ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color,
		String upStationName, String downStationName, int distance) {

		long upStationId = location_header에서_ID_추출(StationAcceptanceTest.지하철_생성_요청(upStationName));
		long downStationId = location_header에서_ID_추출(StationAcceptanceTest.지하철_생성_요청(downStationName));
		return 지하철노선_생성_요청(name, color, upStationId, downStationId, distance);
	}

	public static ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color,
		long upStationId, long downStationId, int distance) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId);
		params.put("downStationId", downStationId);
		params.put("distance", distance);

		return 지하철노선_생성_요청(params);
	}

	public static ExtractableResponse<Response> 지하철노선_생성_요청(Map<String, Object> params) {
		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
	}
}