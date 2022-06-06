package nextstep.subway.station;

import static nextstep.subway.station.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest{

	@BeforeEach
	void init() {
		// given
		지하철_생성_요청("강남역");
		지하철_생성_요청("새로운지하철역");
		지하철_생성_요청("또다른지하철역");
	}

	/**
	 *	When 지하철 노선을 생성하면
	 * 	Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
	 */
	@DisplayName("지하철노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response = 노선_생성_요청("신분당선", "bg-red-600", 1L, 2L, 10);

		// then
		요청_응답_확인(response, HttpStatus.CREATED);
	}

	/**
	 * Given 2개의 지하철 노선을 생성하고
	 * When 지하철 노선 목록을 조회하면
	 * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
	 */
	@DisplayName("지하철노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		노선_생성_요청("신분당선", "bg-red-600", 1L, 2L, 10);
		노선_생성_요청("분당선", "bg-red-600", 1L, 3L, 5);

		// when
		ExtractableResponse<Response> response = 노선_목록_조회_요청();

		// then
		요청_응답_확인(response, HttpStatus.OK);
		assertThat(response.jsonPath().getList("name")).contains("신분당선", "분당선");
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 조회하면
	 * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
	 */
	@DisplayName("지하철노선을 조회한다.")
	@Test
	void getLine() {
		// given
		ExtractableResponse<Response> creationResponse = 노선_생성_요청("신분당선", "bg-red-600", 1L, 2L, 10);

		// when
		ExtractableResponse<Response> response = 노선_조회_요청(creationResponse);

		// then
		요청_응답_확인(response, HttpStatus.OK);
		assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 수정하면
	 * Then 해당 지하철 노선 정보는 수정된다
	 */
	@DisplayName("지하철노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> creationResponse = 노선_생성_요청("신분당선", "bg-red-600", 1L, 2L, 10);
		Map<String, String> params = new HashMap<>();
		params.put("name", "다른분당선");
		params.put("color", "bg-red-600");

		// when
		ExtractableResponse<Response> response = 노선_수정_요청(creationResponse, params);

		요청_응답_확인(response, HttpStatus.OK);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 삭제하면
	 * Then 해당 지하철 노선 정보는 삭제된다
	 */
	@DisplayName("지하철노선을 삭제한다.")
	@Test
	void deleteLine() {
		// given
		ExtractableResponse<Response> creationResponse = 노선_생성_요청("신분당선", "bg-red-600", 1L, 2L, 10);

		// when
		ExtractableResponse<Response> response = 노선_삭제_요청(creationResponse);

		// then
		요청_응답_확인(response, HttpStatus.NO_CONTENT);
	}
}