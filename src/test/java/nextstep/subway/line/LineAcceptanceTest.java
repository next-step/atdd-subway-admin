package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	Map<String, String> params;
	Map<String, String> params2;

	@BeforeEach
	void setBeforeEach() {
		StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();
		stationAcceptanceTest.지하철역을_생성한다("강남역");
		stationAcceptanceTest.지하철역을_생성한다("역삼역");
		stationAcceptanceTest.지하철역을_생성한다("테스트역1");
		stationAcceptanceTest.지하철역을_생성한다("테스트역2");

		params = new HashMap<>();
		params.put("color", "br-red-600");
		params.put("name", "신분당선");
		params.put("upStationId", "1");
		params.put("downStationId", "2");
		params.put("distance", "10");

		params2 = new HashMap<>();
		params2.put("color", "bg-blue-600");
		params2.put("name", "구분당선");
		params2.put("upStationId", "3");
		params2.put("downStationId", "4");
		params2.put("distance", "20");
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response = 노선을_생성한다(params);

		// then
		노선_생성_완료(response);
	}

	private void 노선_생성_완료(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		노선을_생성한다(params);

		// when
		ExtractableResponse<Response> response = 노선을_생성한다(params);

		// then
		노선_생성시_에러가_발생한다(response);
	}

	private void 노선_생성시_에러가_발생한다(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> createResponse1 = 노선을_생성한다(params);
		ExtractableResponse<Response> createResponse2 = 노선을_생성한다(params2);

		// when
		ExtractableResponse<Response> response = 노선목록을_조회한다();

		// then
		생성된_노선목록이_조회된다(createResponse1, createResponse2, response);
	}

	private void 생성된_노선목록이_조회된다(ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2, ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
				.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
				.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
				.map(LineResponse::getId)
				.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	private ExtractableResponse<Response> 노선목록을_조회한다() {
		return RestAssured.given().log().all()
				.when()
				.get("/lines")
				.then().log().all()
				.extract();
	}

	@DisplayName("지하철 노선 목록을 조회하면 포함 된 역도 함께 조회된다")
	@Test
	void getLinesWithStations() {
		// given
		노선을_생성한다(params);

		// when
		ExtractableResponse<Response> response = 노선목록을_조회한다();

		// then
		노선목록을_역과_함께_조회한다(response);
	}

	private void 노선목록을_역과_함께_조회한다(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<String> responseStationNames = (List<String>) response.jsonPath().getList("stations.name").get(0);
		assertThat(responseStationNames).containsExactly("강남역", "역삼역");
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선을_생성한다(params);

		// when
		ExtractableResponse<Response> response = 특정_노선을_조회한다(getLocation(createResponse));

		// then
		특정_노선이_조회된다(response);
	}

	private void 특정_노선이_조회된다(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		String expectedName = "신분당선";
		String resultName = response.jsonPath().getString("name");
		assertThat(resultName).isEqualTo(expectedName);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선을_생성한다(params);

		// when
		ExtractableResponse<Response> response = 특정_노선을_수정한다();

		ExtractableResponse<Response> modifiedResponse = 특정_노선을_조회한다(getLocation(createResponse));

		// then
		수정된_노선이_조회된다(response, modifiedResponse);
	}

	private void 수정된_노선이_조회된다(ExtractableResponse<Response> response, ExtractableResponse<Response> modifiedResponse) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		String expected = "구분당선";
		String actual = modifiedResponse.jsonPath().getString("name");
		assertThat(actual).isEqualTo(expected);
	}

	private ExtractableResponse<Response> 특정_노선을_수정한다() {
		return RestAssured.given().log().all()
				.body(params2)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.put("/lines/1")
				.then().log().all()
				.extract();
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		ExtractableResponse<Response> createResponse = 노선을_생성한다(params);

		// when
		ExtractableResponse<Response> response = 노선_삭제_요청을_한다(getLocation(createResponse));

		// then
		노선이_삭제되어_노선을_반환하지_않는다(response, getLocation(createResponse));
	}

	private String getLocation(ExtractableResponse<Response> response) {
		return response.header("Location");
	}

	private void 노선이_삭제되어_노선을_반환하지_않는다(ExtractableResponse<Response> response, String createLocation) {
		ExtractableResponse<Response> actual = 특정_노선을_조회한다(createLocation);
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(actual.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> 노선_삭제_요청을_한다(String url) {
		return RestAssured.given().log().all()
				.when()
				.delete(url)
				.then().log().all()
				.extract();
	}

	private ExtractableResponse<Response> 노선을_생성한다(Map<String, String> params) {
		return RestAssured.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post("/lines")
				.then().log().all()
				.extract();
	}

	private ExtractableResponse<Response> 특정_노선을_조회한다(String url) {
		return RestAssured.given().log().all()
				.when()
				.get(url)
				.then().log().all()
				.extract();
	}
}
