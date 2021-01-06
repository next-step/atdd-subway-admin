package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	@Autowired
	StationRepository stationRepository;

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		Station gangNamStation = stationRepository.save(new Station("강남역"));
		Station seoChoStation = stationRepository.save(new Station("서초역"));
		Map<String, String> params = getParams("2호선", "green", gangNamStation.getId(), seoChoStation.getId(), 10);

		// when
		ExtractableResponse<Response> response = createLine(params);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLineWithDuplicatedName() {
		// given
		Station gangNamStation = stationRepository.save(new Station("강남역"));
		Station seoChoStation = stationRepository.save(new Station("서초역"));
		Map<String, String> params = getParams("2호선", "green", gangNamStation.getId(), seoChoStation.getId(), 10);
		createLine(params);

		// when
		ExtractableResponse<Response> response = createLine(params);

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		Station gangNamStation = stationRepository.save(new Station("강남역"));
		Station seoChoStation = stationRepository.save(new Station("서초역"));
		Station yeouidoStation = stationRepository.save(new Station("여의도역"));
		Station mokDongStation = stationRepository.save(new Station("목동역"));

		// given
		// param1 등록
		Map<String, String> params1 = getParams("2호선", "green", gangNamStation.getId(), seoChoStation.getId(), 10);
		ExtractableResponse<Response> createResponse1 = createLine(params1);

		// param2 등록
		Map<String, String> params2 = getParams("5호선", "purple", yeouidoStation.getId(), mokDongStation.getId(), 20);
		ExtractableResponse<Response> createResponse2 = createLine(params2);

		// when
		// 조회
		ExtractableResponse<Response> response = findAll();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		Station gangNamStation = stationRepository.save(new Station("강남역"));
		Station seoChoStation = stationRepository.save(new Station("서초역"));
		String expectedName = "2호선";
		String expectedColor = "green";
		// given
		// 지하철_노선_등록되어_있음
		Map<String, String> params = getParams(expectedName, expectedColor, gangNamStation.getId(), seoChoStation.getId(), 10);
		ExtractableResponse<Response> createResponse = createLine(params);

		String id = createResponse.header("Location").split("/")[2];

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = findById(id);

		// then
		// 지하철_노선_응답됨
		String actualName = response.body().jsonPath().get("name");
		String actualColor = response.body().jsonPath().get("color");
		assertThat(expectedName).isEqualTo(actualName);
		assertThat(expectedColor).isEqualTo(actualColor);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		Station gangNamStation = stationRepository.save(new Station("강남역"));
		Station seoChoStation = stationRepository.save(new Station("서초역"));
		String expectedName = "5호선";
		String expectedColor = "purple";
		Map<String, String> createParams = getParams("2호선", "green", gangNamStation.getId(), seoChoStation.getId(), 10);
		ExtractableResponse<Response> createResponse = createLine(createParams);

		String id = createResponse.header("Location").split("/")[2];

		// when
		Map<String, String> updateParams = getParams(expectedName, expectedColor, gangNamStation.getId(), seoChoStation.getId(), 10);
		updateLine(id, updateParams);

		// then
		ExtractableResponse<Response> response = findById(id);

		String actualName = response.body().jsonPath().get("name");
		String actualColor = response.body().jsonPath().get("color");
		assertThat(expectedName).isEqualTo(actualName);
		assertThat(expectedColor).isEqualTo(actualColor);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		Station gangNamStation = stationRepository.save(new Station("강남역"));
		Station seoChoStation = stationRepository.save(new Station("서초역"));
		Map<String, String> createParams = getParams("2호선", "green", gangNamStation.getId(), seoChoStation.getId(), 10);
		ExtractableResponse<Response> createResponse = createLine(createParams);

		String id = createResponse.header("Location").split("/")[2];

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> response = deleteLine(id);

		// then
		// 지하철_노선_삭제됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private Map<String, String> getParams(String name, String color, Long upStationId, Long downStationId, int distance) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", Long.toString(upStationId));
		params.put("downStationId", Long.toString(downStationId));
		params.put("distance", Integer.toString(distance));
		return params;
	}

	private ExtractableResponse<Response> createLine(Map<String, String> params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> findAll() {
		return RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> findById(String id) {
		return RestAssured.given().log().all()
			.when()
			.get("/lines/" + id)
			.then().log().all()
			.extract();
	}

	private void updateLine(String id, Map<String, String> updateParams) {
		RestAssured.given().log().all()
			.body(updateParams)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + id)
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> deleteLine(String id) {
		return RestAssured.given().log().all()
			.when()
			.delete("/lines/" + id)
			.then().log().all()
			.extract();
	}
}
