package nextstep.subway.line;

import static nextstep.subway.line.LineTestFixture.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseTest {

	private LineResponse exampleLine1;

	@BeforeEach
	void setup() {
		exampleLine1 = requestCreateLine(
			LineRequest.of(EXAMPLE_LINE1_NAME, EXAMPLE_LINE1_COLOR)
		).as(LineResponse.class);

		requestCreateLine(LineRequest.of(EXAMPLE_LINE2_NAME, EXAMPLE_LINE2_COLOR));
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		String name = "3호선";
		String color = "빨간색";
		LineRequest lineRequest = LineRequest.of(name, color);

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = requestCreateLine(lineRequest);

		// then
		// 지하철_노선_생성됨
		LineResponse lineResponse = response.as(LineResponse.class);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).startsWith(LINE_URL_PREFIX);
		assertThat(lineResponse.getName()).isEqualTo(name);
		assertThat(lineResponse.getColor()).isEqualTo(color);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = requestCreateLine(
			LineRequest.of(EXAMPLE_LINE1_NAME, EXAMPLE_LINE1_COLOR));

		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = requestGetLines();

		// then
		// 지하철_노선_목록_응답됨
		// 지하철_노선_목록_포함됨
		List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
		List<String> lineNames = CollectionUtils.emptyIfNull(lineResponses)
			.stream()
			.map(LineResponse::getName)
			.collect(Collectors.toList());

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(lineNames).contains(EXAMPLE_LINE1_NAME, EXAMPLE_LINE2_NAME);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = requestGetLineById(exampleLine1.getId());

		// then
		// 지하철_노선_응답됨
		LineResponse lineResponse = response.as(LineResponse.class);

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(lineResponse.getName()).isEqualTo(EXAMPLE_LINE1_NAME);
		assertThat(lineResponse.getColor()).isEqualTo(EXAMPLE_LINE1_COLOR);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_수정_요청

		// then
		// 지하철_노선_수정됨
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음

		// when
		// 지하철_노선_제거_요청

		// then
		// 지하철_노선_삭제됨
	}
}
