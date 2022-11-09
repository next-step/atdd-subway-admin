package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestAssertions.지하철_노선_생성됨;
import static nextstep.subway.line.LineAcceptanceTestAssured.지하철_노선_생성;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceTestAssured.지하철역_식별자;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.SubwayAcceptanceTest;

@DisplayName("지하철 노선 관련 기능 ")
class LineAcceptanceTest extends SubwayAcceptanceTest {

	/**
	 * When 지하철 노선을 생성하면
	 * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
	 */
	@Test
	void 지하철노선을_생성한다() {
		// given
		Long 상행역_아이디 = 지하철역_식별자(지하철역_생성("상행역"));
		Long 하행역_아이디 = 지하철역_식별자(지하철역_생성("하행역"));

		// when
		ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성("신분당선", 상행역_아이디, 하행역_아이디);

		// then
		지하철_노선_생성됨(지하철_노선_생성_응답);
	}


	/**
	 * Given 2개의 지하철 노선을 생성하고
	 * When 지하철 노선 목록을 조회하면
	 * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
	 */
	@Test
	void 지하철노선_목록을_조회한다() {

	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 조회하면
	 * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
	 */
	@Test
	void 지하철_노선_조회() {

	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 수정하면
	 * Then 해당 지하철 노선 정보는 수정된다
	 */
	@Test
	void 지하철노선_수정() {

	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 삭제하면
	 * Then 해당 지하철 노선 정보는 삭제된다
	 */
	@Test
	void 지하철노선_삭제() {

	}
}
