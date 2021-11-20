package nextstep.subway.line;

import static nextstep.subway.utils.AcceptanceTestUtil.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@BeforeEach
	private void setup() {
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response =
			지하철_노선_생성(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

		// then
		지하철_노선_생성됨(response);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		지하철_노선_생성(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

		// when
		final ExtractableResponse<Response> response =
			지하철_노선_생성(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));

		// then
		존재하는_지하철_노선_이름인_경우_노선_생성_실패(response);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		지하철_노선_생성(new LineRequest("2호선", "bg-green-600", 1L, 2L, 10));
		지하철_노선_생성(new LineRequest("신분당선", "bg-red-600", 3L, 4L, 5));

		// when
		ExtractableResponse<Response> response = 지하철_노선_목록_조회();

		// then
		정상_응답(response);
		노선_목록_2개가_응답에_포함(response);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		지하철_노선_생성(new LineRequest("2호선", "bg-green-600", 1L, 2L, 10));

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회(1L);

		// then
		정상_응답(response);
        노선_2호선_green_1개가_응답에_포함(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		지하철_노선_생성(new LineRequest("2호선", "bg-green-600", 1L, 2L, 10));

		// when
		ExtractableResponse<Response> response = 지하철_노선_수정(1L, "신분당선", "red");

		// then
		정상_응답(response);
        변경된_노선_신분당선_red가_응답에_포함(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		지하철_노선_생성(new LineRequest("2호선", "bg-green-600", 1L, 2L, 10));

		// when
		ExtractableResponse<Response> response = 지하철_노선_삭제(1L);

		// then
		정상_응답(response);
	}

	private ExtractableResponse<Response> 지하철_노선_생성(LineRequest lineRequest) {
		return post("/lines", lineRequest);
	}

	private ExtractableResponse<Response> 지하철_노선_목록_조회() {
		return get("/lines");
	}

	private ExtractableResponse<Response> 지하철_노선_조회(Long lineId) {
		return get("/lines/{id}", pathParamsForId(lineId));
	}

	private ExtractableResponse<Response> 지하철_노선_수정(long lineId, String name, String color) {
		return put("/lines/{id}", new LineRequest(name, color), pathParamsForId(lineId));
	}

	private ExtractableResponse<Response> 지하철_노선_삭제(long lineId) {
		return delete("/lines/{id}", pathParamsForId(lineId));
	}

	private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	private void 존재하는_지하철_노선_이름인_경우_노선_생성_실패(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	private void 정상_응답(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 노선_목록_2개가_응답에_포함(ExtractableResponse<Response> response) {
		assertThat(response.jsonPath().getList(".", LineResponse.class).size()).isEqualTo(2);
	}

    private void 노선_2호선_green_1개가_응답에_포함(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject(".", LineResponse.class))
            .extracting("name", "color")
            .contains("2호선", "green");
    }

    private void 변경된_노선_신분당선_red가_응답에_포함(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getObject(".", LineResponse.class))
            .extracting("name", "color")
            .contains("신분당선", "red");
    }
}
