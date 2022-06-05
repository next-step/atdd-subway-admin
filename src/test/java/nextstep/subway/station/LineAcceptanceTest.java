package nextstep.subway.station;

import static nextstep.subway.station.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest{

	@BeforeEach
	void init() {
		지하철_생성_요청("강남역");
		지하철_생성_요청("역삼역");
	}

	/**
	 *	When 지하철 노선을 생성하면
	 * 	Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
	 */
	@DisplayName("지하철노선을 생성한다.")
	@Test
	void createLine() {
		Map<String, String> params = new HashMap<>();
		params.put("name", "신분당선");
		params.put("color", "bg-red-600");
		params.put("upStationId", "1");
		params.put("downStationId", "2");
		params.put("distance", "10");

		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.post("/lines")
			.then().log().all()
			.extract();

		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	/**
	 * Given 2개의 지하철 노선을 생성하고
	 * When 지하철 노선 목록을 조회하면
	 * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
	 */
	@DisplayName("지하철노선 목록을 조회한다.")
	@Test
	void getLines() {

	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 조회하면
	 * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
	 */
	@DisplayName("지하철노선을 조회한다.")
	@Test
	void getLine() {

	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 수정하면
	 * Then 해당 지하철 노선 정보는 수정된다
	 */
	@DisplayName("지하철노선을 수정한다.")
	@Test
	void updateLine() {

	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 삭제하면
	 * Then 해당 지하철 노선 정보는 삭제된다
	 */
	@DisplayName("지하철노선을 삭제한다.")
	@Test
	void deleteLine() {

	}
}
