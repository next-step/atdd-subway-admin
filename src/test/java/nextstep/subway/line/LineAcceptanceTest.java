package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestAssertions.지하철_노선_삭제됨;
import static nextstep.subway.line.LineAcceptanceTestAssertions.지하철_노선_생성됨;
import static nextstep.subway.line.LineAcceptanceTestAssertions.지하철_노선_존재함;
import static nextstep.subway.line.LineAcceptanceTestAssured.지하철_노선_목록_조회;
import static nextstep.subway.line.LineAcceptanceTestAssured.지하철_노선_삭제;
import static nextstep.subway.line.LineAcceptanceTestAssured.지하철_노선_생성;
import static nextstep.subway.line.LineAcceptanceTestAssured.지하철_노선_수정;
import static nextstep.subway.line.LineAcceptanceTestAssured.지하철_노선_조회;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

		// when
		ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성("신분당선", "상행역", "하행역");

		// then
		지하철_노선_생성됨(지하철_노선_생성_응답);
	}


	/**
	 * Given 2개의 지하철 노선을 생성하고
	 * When 지하철 노선 목록을 조회하면
	 * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
	 */
	@ParameterizedTest
	@CsvSource({"신분당선,1호선"})
	void 지하철노선_목록을_조회한다(String 지하철_노선1, String 지하철_노선2) {
		// given
		지하철_노선_생성(지하철_노선1, "상행역1", "하행역1");
		지하철_노선_생성(지하철_노선2, "상행역2", "하행역2");

		// when
		List<String> 지하철_노선_목록 = 지하철_노선_목록_조회();

		// then
		지하철_노선_존재함(지하철_노선_목록, 지하철_노선1, 지하철_노선2);

	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 조회하면
	 * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
	 */
	@ParameterizedTest
	@CsvSource({"신분당선,상행역3,하행역3"})
	void 지하철_노선_조회을_조회한다(String 지하철_노선, String 상행역, String 하행역) {
		// given
		ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성(지하철_노선, 상행역, 하행역);

		// when
		String 생성된_지하철_노선_이름 = 지하철_노선_조회(지하철_노선_생성_응답);

		// then
		지하철_노선_존재함(생성된_지하철_노선_이름, 지하철_노선);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 수정하면
	 * Then 해당 지하철 노선 정보는 수정된다
	 */
	@ParameterizedTest
	@CsvSource({"신분당선,가양역,상행역4,하행역4"})
	void 지하철_노선을_수정한다(String 지하철_노선, String 수정할_지하철_노선_이름, String 상행역, String 하행역) {
		// given
		ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성(지하철_노선, 상행역, 하행역);

		// when
		지하철_노선_수정(지하철_노선_생성_응답, 수정할_지하철_노선_이름);

		// then
		String 수정된_지하철_노선_이름 = 지하철_노선_조회(지하철_노선_생성_응답);
		지하철_노선_존재함(수정된_지하철_노선_이름, 수정할_지하철_노선_이름);
	}

	/**
	 * Given 지하철 노선을 생성하고
	 * When 생성한 지하철 노선을 삭제하면
	 * Then 해당 지하철 노선 정보는 삭제된다
	 */
	@ParameterizedTest
	@CsvSource({"신분당선,상행역5,하행역5"})
	void 지하철_노선을_삭제한다(String 지하철_노선, String 상행역, String 하행역) {
		// given
		ExtractableResponse<Response> 지하철_노선_생성_응답 = 지하철_노선_생성(지하철_노선, 상행역, 하행역);

		// when
		지하철_노선_삭제(지하철_노선_생성_응답);

		// then
		List<String> 지하철_노선_목록 = 지하철_노선_목록_조회();
		지하철_노선_삭제됨(지하철_노선_목록, 지하철_노선);
	}
}
