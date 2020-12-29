package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineAcceptanceTestSupport.*;

@Deprecated
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("[삭제예정]지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green");

		// then
		assertStatusCode(response, HttpStatus.CREATED);
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine_duplicated() {
		// given
		지하철노선_생성_요청("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철노선_생성_요청("2호선", "green");

		// then
		assertStatusCode(response, HttpStatus.BAD_REQUEST);
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> createResponse1 = 지하철노선_생성_요청("2호선", "green");
		ExtractableResponse<Response> createResponse2 = 지하철노선_생성_요청("3호선", "orange");

		// when
		ExtractableResponse<Response> response = 지하철노선_얻기();

		// then
		지하철노선목록_조회_검사(response, createResponse1, createResponse2);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("2호선", "green");

		// when
		ExtractableResponse<Response> response = 지하철노선_조회(createResponse);

		// then
		지하철노선_조회_검사(createResponse, response);
	}

	@DisplayName("존재하지 않는 지하철 노선을 조회한다.")
	@Test
	void getLine_notFound() {
		// when
		ExtractableResponse<Response> response = 지하철노선_조회("lines/1");

		// then
		assertStatusCode(response, HttpStatus.NOT_FOUND);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("2호선", "green");

		// when
		ExtractableResponse<Response> updateResponse = 지하철노선_수정_요청(createResponse, "3호선", "orange");

		// then
		assertStatusCode(updateResponse, HttpStatus.OK);
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("2호선", "green");

		// when
		ExtractableResponse<Response> deleteResponse = 지하철노선_삭제_요청(createResponse);

		// then
		assertStatusCode(deleteResponse, HttpStatus.NO_CONTENT);
	}
}
