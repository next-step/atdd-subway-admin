package nextstep.subway.line;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	@Disabled
	void createLine() {
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse response = 노선_생성_함수("신분당선", "bg-red-600");
		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	@Disabled
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse response = 노선_생성_함수("신분당선", "bg-red-600");
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		// when
		// 기존에 존재하는 지하철 노선 이름으로 지하철_노선_생성_요청
		ExtractableResponse response2 = 노선_생성_함수("신분당선", "bg-red-600");
		// then
		// 지하철_노선_생성_실패됨
		assertThat(response2.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	@Disabled
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse response = 노선_생성_함수("신분당선", "bg-red-600");
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		// 지하철_노선_등록되어_있음
		ExtractableResponse response2 = 노선_생성_함수("2호선", "bg-green-600");
		assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> linesResponse = RestAssured
				.given().log().all()
				.when().get(LINE_URL)
				.then().log().all().extract();
		// then
		// 지하철_노선_목록_응답됨
		assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> resultLineIds = linesResponse.jsonPath().getList(".", LineResponse.class).stream()
				.map(it -> it.getId())
				.collect(Collectors.toList());
		assertThat(resultLineIds.size()).isNotEqualTo(0);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	@Disabled
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse response = 노선_생성_함수("신분당선", "bg-red-600");
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(1L);
		// then
		// 지하철_노선_응답됨
		assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getId()).isNotNull();
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	@Disabled
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse response = 노선_생성_함수("신분당선", "bg-red-600");
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		// when
		// 지하철_노선_수정_요청
		String name = "new_신분당선";
		지하철_노선_수정_PUT_요청_OK_응답_확인("1", name);
		// then
		// 지하철_노선_수정됨
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(1L);
		assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = linesResponse.jsonPath().getObject(".", LineResponse.class);
		assertThat(lineResponse.getName().equalsIgnoreCase(name)).isTrue();
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	@Disabled
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse response = 노선_생성_함수("신분당선", "bg-red-600");
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		// when
		// 지하철_노선_제거_요청
		노선_제거_요청_후_204_응답_확인(1L);
		// then
		// 지하철_노선_삭제됨
		ExtractableResponse<Response> linesResponse = ID로_노선을_조회한다(1L);
		assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	private void 지하철_노선_수정_PUT_요청_OK_응답_확인(String id, String name) {
		Map<String, String> params = new HashMap<>();
		params.put("id", id);
		params.put("name", name);
		ExtractableResponse<Response> patchResponse = RestAssured.given().log().all().
				body(params).
				contentType(MediaType.APPLICATION_JSON_VALUE).
				when().
				put(LINE_URL).
				then().
				log().all().
				extract();
		assertThat(patchResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	private void 노선_제거_요청_후_204_응답_확인(long id) {
		ExtractableResponse<Response> deleteLineResponse = RestAssured.given().log().all().
				contentType(MediaType.APPLICATION_JSON_VALUE).
				when().
				delete(LINE_URL + "/" + String.valueOf(id)).
				then().
				log().all().
				extract();
		assertThat(deleteLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

}
