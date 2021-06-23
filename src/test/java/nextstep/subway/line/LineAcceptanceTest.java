package nextstep.subway.line;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.SectionAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	private static Long 광명역ID;
	private static Long 오송역ID;

	private static Long 서울역ID;
	private static Long 회현역ID;

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();

		광명역ID = StationAcceptanceTest.지하철역_등록되어_있음("광명역").getId();
		오송역ID = StationAcceptanceTest.지하철역_등록되어_있음("오송역").getId();

		서울역ID = StationAcceptanceTest.지하철역_등록되어_있음("서울역").getId();
		회현역ID = StationAcceptanceTest.지하철역_등록되어_있음("회현역").getId();
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철_노선_생성_요청("경부고속선", "blue", 광명역ID, 오송역ID, 7);

		// then
		// 지하철_노선_생성됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.CONFLICT.value());
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청(final String name, final String color, final Long upStationId,
		final Long downStationId, final int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId.toString());
		params.put("downStationId", downStationId.toString());
		params.put("distance", Integer.toString(distance));

		return RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine_exception() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("경부고속선", "blue", 광명역ID, 오송역ID, 8);

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철_노선_생성_요청("경부고속선", "blue", 1L, 2L, 7);

		// then
		// 지하철_노선_생성_실패됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
		Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("경부고속선", "blue", 광명역ID, 오송역ID, 8);
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("4호선", "green", 서울역ID, 회현역ID, 4);

		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

		// then
		// 지하철_노선_목록_응답됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 값 검증
		List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
		Assertions.assertThat(lineResponses)
			.hasSize(2)
			.extracting("name").contains("경부고속선", "4호선");
	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();
		return response;
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("경부고속선", "blue", 광명역ID, 오송역ID, 8);
		지하철_노선_등록되어_있음("4호선", "green", 서울역ID, 회현역ID, 4);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

		// then
		// 지하철_노선_응답됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.NOT_FOUND.value());
		// 값 검증
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		Assertions.assertThat(lineResponse.getName()).isEqualTo("경부고속선");
		Assertions.assertThat(lineResponse.getStations()).extracting("name").first().isEqualTo("광명역");
		Assertions.assertThat(lineResponse.getStations()).extracting("name").last().isEqualTo("오송역");
	}

	@DisplayName("등록되지않은 지하철 노선을 조회한다.")
	@Test
	void getLine_exception() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("경의선", "blue", 광명역ID, 오송역ID, 8);
		지하철_노선_등록되어_있음("4호선", "green", 서울역ID, 회현역ID, 4);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(3L);

		// then
		// 지하철_노선_응답됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
		Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(final Long id) {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get("/lines/{id}", id)
			.then().log().all().extract();
		return response;
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("경부고속선", "blue", 광명역ID, 오송역ID, 8);

		// when
		// 지하철_노선_수정_요청
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(1L, "경의선", "red");

		// then
		// 지하철_노선_수정됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("등록되지않은 지하철 노선을 수정한다.")
	@Test
	void updateLine_exception() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("경부고속선", "blue", 광명역ID, 오송역ID, 8);

		// when
		// 지하철_노선_수정_요청
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(3L, "경의선", "red");

		// then
		// 지하철_노선_수정됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
		Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 지하철_노선_수정_요청(final Long id, final String name, final String colorToModify) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", colorToModify);

		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/lines/{id}", id)
			.then().log().all().extract();
		return response;
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("경부고속선", "blue", 광명역ID, 오송역ID, 8);

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> response = 지하철_노선_제거_요청(1L);

		// then
		// 지하철_노선_삭제됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("등록되지않은 지하철 노선을 제거한다.")
	@Test
	void deleteLine_exception() {
		// given
		// 지하철_노선_등록되어_있음
		지하철_노선_등록되어_있음("경부고속선", "blue", 광명역ID, 오송역ID, 8);

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> response = 지하철_노선_제거_요청(3L);

		// then
		// 지하철_노선_삭제됨
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
		Assertions.assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("노선의 구간목록 중 끝 역을 삭제한다.")
	@Test
	void deleteSection() {
		//given
		Long 운정역ID = StationAcceptanceTest.지하철역_등록되어_있음("운정역").getId();
		Long 야당역ID = StationAcceptanceTest.지하철역_등록되어_있음("야당역").getId();
		Long 탄현역ID = StationAcceptanceTest.지하철역_등록되어_있음("탄현역").getId();

		Long 경의선ID = 지하철_노선_등록되어_있음("경의선", "brown", 운정역ID, 탄현역ID, 13).getId();

		SectionAcceptanceTest.노선에_구간_등록되어_있음(경의선ID, 운정역ID, 야당역ID, 5);

		// when
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().delete("/lines/{id}/sections?stationId={stationId}", 경의선ID, 탄현역ID)
			.then().log().all().extract();

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("노선의 구간목록 중 중간 역을 삭제한다.")
	@Test
	void deleteSection2() {
		//given
		Long 운정역ID = StationAcceptanceTest.지하철역_등록되어_있음("운정역").getId();
		Long 야당역ID = StationAcceptanceTest.지하철역_등록되어_있음("야당역").getId();
		Long 탄현역ID = StationAcceptanceTest.지하철역_등록되어_있음("탄현역").getId();

		Long 경의선ID = 지하철_노선_등록되어_있음("경의선", "brown", 운정역ID, 탄현역ID, 13).getId();

		SectionAcceptanceTest.노선에_구간_등록되어_있음(경의선ID, 운정역ID, 야당역ID, 5);

		// when
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().delete("/lines/{id}/sections?stationId={stationId}", 경의선ID, 야당역ID)
			.then().log().all().extract();

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private ExtractableResponse<Response> 지하철_노선_제거_요청(final Long id) {
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().delete("/lines/{id}", id)
			.then().log().all().extract();
		return response;
	}

	public static LineResponse 지하철_노선_등록되어_있음(final String name, final String color, final Long upStationId,
		final Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId.toString());
		params.put("downStationId", downStationId.toString());
		params.put("distance", Integer.toString(distance));

		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();

		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

		return response.jsonPath().getObject(".", LineResponse.class);
	}
}
