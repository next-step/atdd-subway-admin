package nextstep.subway.line;

import static nextstep.subway.AcceptanceTestUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	public static final String COLOR1 = "bg-red-600";
	public static final String NAME1 = "신분당선";
	public static final String COLOR2 = "bg-blue-600";
	public static final String NAME2 = "구분당선";
	public static final String LOCATION_HEADER_NAME = "Location";
	public static final Station STATION1 = new Station("삼성역");
	public static final Station STATION2 = new Station("역삼역");
	public static final Station STATION3 = new Station("선릉역");

	@Autowired
	private StationRepository stationRepository;

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		Station station1 = 지하철역_생성되어_있음1();
		Station station2 = 지하철역_생성되어_있음2();

		// when
		Section section = new Section(station1, station2, 5);
		LineRequest params = new LineRequest(NAME1, COLOR1, section.getUpStation().getId(),
			section.getDownStation().getId(), section.getDistance());
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

		// then
		응답_검증(response, HttpStatus.CREATED);
		지하철_노선_생성_헤더_검증(response);
		지하철_노선_생성_검증(response.header(LOCATION_HEADER_NAME));
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		Station station1 = 지하철역_생성되어_있음1();
		Station station2 = 지하철역_생성되어_있음2();
		Section section1 = new Section(station1, station2, 5);
		지하철_노선_등록되어_있음(section1);

		// when
		Station station3 = 지하철역_생성되어_있음3();
		Section section2 = new Section(station2, station3, 7);
		LineRequest params = new LineRequest(NAME1, COLOR1, section2.getUpStation().getId(),
			section2.getDownStation().getId(), section2.getDistance());
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

		// then
		지하철_노선_생성_실패됨(response);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		Station station1 = 지하철역_생성되어_있음1();
		Station station2 = 지하철역_생성되어_있음2();
		Station station3 = 지하철역_생성되어_있음3();
		지하철_노선_등록되어_있음(new Section(station1, station2, 5));
		지하철_노선_등록되어_있음2(new Section(station2, station3, 7));

		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

		// then
		응답_검증(response, HttpStatus.OK);
		지하철_노선_목록_포함됨(response);
		지하철역_노선_목록에_포함_검증(response);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		Station station1 = 지하철역_생성되어_있음1();
		Station station2 = 지하철역_생성되어_있음2();
		지하철_노선_등록되어_있음(new Section(station1, station2, 5));

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(1L);

		// then
		응답_검증(response, HttpStatus.OK);
		지하철_노선_포함_검증(response, NAME1, COLOR1);
		지하철역_노선에_포함_검증(response);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		Station station1 = 지하철역_생성되어_있음1();
		Station station2 = 지하철역_생성되어_있음2();
		지하철_노선_등록되어_있음(new Section(station1, station2, 5));
		Long lineId = 1L;

		// when
		ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineId);

		// then
		응답_검증(response, HttpStatus.OK);
		지하철역_노선에_포함_검증(지하철_노선_수정_검증(lineId));
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		Station station1 = 지하철역_생성되어_있음1();
		Station station2 = 지하철역_생성되어_있음2();
		지하철_노선_등록되어_있음(new Section(station1, station2, 5));
		Long id = 1L;

		// when
		ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);

		// then
		응답_검증(response, HttpStatus.NO_CONTENT);
		지하철_노선_삭제_검증(id);
	}

	@DisplayName("지하철 노선이 추가되지 않은 상태에서 노선을 제거한다.")
	@Test
	void deleteLine2() {
		Long id = 1L;

		// when
		ExtractableResponse<Response> response = 지하철_노선_제거_요청(id);

		// then
		응답_검증(response, HttpStatus.NOT_FOUND);
	}

	private void 지하철_노선_삭제_검증(Long id) {
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines/" + id)
			.then().log().all()
			.extract();

		응답_검증(response, HttpStatus.NOT_FOUND);
	}

	private void 지하철_노선_생성_헤더_검증(ExtractableResponse<Response> response) {
		assertThat(response.header("Location")).isNotBlank();
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest params) {
		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_등록되어_있음(Section section) {
		LineRequest params = new LineRequest(NAME1, COLOR1, section.getUpStation().getId(),
			section.getDownStation().getId(), section.getDistance());
		return 지하철_노선_생성_요청(params);
	}

	private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		응답_검증(response, HttpStatus.CONFLICT);
	}

	private ExtractableResponse<Response> 지하철_노선_등록되어_있음2(Section section) {
		LineRequest params = new LineRequest(NAME2, COLOR2, section.getUpStation().getId(),
			section.getDownStation().getId(), section.getDistance());
		return 지하철_노선_생성_요청(params);

	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		return RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(String url) {
		return RestAssured.given().log().all()
			.when()
			.get(url)
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
		return 지하철_노선_조회_요청("/lines/" + id);
	}

	private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id) {
		Map<String, String> params = new HashMap<>();
		params.put("color", COLOR2);
		params.put("name", NAME2);

		return RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + id)
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
		return RestAssured.given().log().all()
			.when()
			.delete("/lines/" + id)
			.then().log().all()
			.extract();
	}

	private void 지하철_노선_포함_검증(ExtractableResponse<Response> response, String name, String color) {
		assertThat(response.jsonPath().getString("name")).contains(name);
		assertThat(response.jsonPath().getString("color")).contains(color);
	}

	private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response) {
		assertThat(response.jsonPath().getList("name")).contains(NAME1, NAME2);
		assertThat(response.jsonPath().getList("color")).contains(COLOR1, COLOR2);
	}

	private ExtractableResponse<Response> 지하철_노선_수정_검증(Long id) {
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(id);
		지하철_노선_포함_검증(response, NAME2, COLOR2);
		return response;
	}

	private void 지하철_노선_생성_검증(String url) {
		ExtractableResponse<Response> response = 지하철_노선_조회_요청(url);
		지하철_노선_포함_검증(response, NAME1, COLOR1);
		지하철역_노선에_포함_검증(response);
	}

	private void 지하철역_노선에_포함_검증(ExtractableResponse<Response> response) {
		assertThat(response.as(LineResponse.class).getStations().size()).isGreaterThan(0);
	}

	private Station 지하철역_생성되어_있음1() {
		return stationRepository.save(STATION1);
	}

	private Station 지하철역_생성되어_있음2() {
		return stationRepository.save(STATION2);
	}

	private Station 지하철역_생성되어_있음3() {
		return stationRepository.save(STATION3);
	}

	private void 지하철역_노선_목록에_포함_검증(ExtractableResponse<Response> response) {
		response.jsonPath().getList("stations").forEach(stations -> {
			System.out.println(((List<Object>)stations).size());
			assertThat(((List<Object>)stations).size()).isGreaterThan(0);
		});
	}
}
