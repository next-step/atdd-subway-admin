package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		// 지하철_노선_미등록_상태

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = createLine(getLineParameter("bg-red-600", "신분당선"));

		// then
		// 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLineWithDuplicateName() {
		// given
		// 지하철_노선_등록되어_있음
		createLine(getLineParameter("bg-red-600", "신분당선"));

		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = createLine(getLineParameter("bg-red-600", "신분당선"));

		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse1 = createLine(getLineParameter("bg-red-600", "신분당선"));

		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse2 = createLine(getLineParameter("bg-green-600", "2호선"));

		// when
		// 지하철_노선_목록_조회_요청
		ExtractableResponse<Response> response = findAllLines();

		// then
		// 지하철_노선_목록_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 지하철_노선_목록_포함됨
		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
			.map(it -> Long.parseLong(getLineID(it)))
			.collect(Collectors.toList());

		List<Long> resultLineIds = response.jsonPath().getList(".", Line.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());

		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = createLine(getLineParameter("bg-red-600", "신분당선"));

		// when
		// 지하철_노선_조회_요청
		String id = getLineID(createResponse);
		ExtractableResponse<Response> response = findLine(id);

		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 지하철_노선_동일함
		Line line = response.jsonPath().getObject(".", Line.class);
		assertThat(String.valueOf(line.getId())).isEqualTo(id);
	}

	@DisplayName("없는 지하철 노선을 조회한다.")
	@Test
	void getNotExistsLine() {
		// given
		// 지하철_노선_등록되어 있지 않음

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = findLine("1");

		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = createLine(getLineParameter("bg-red-600", "신분당선"));

		// when
		// 지하철_노선_수정_요청
		ExtractableResponse<Response> modifyResponse = updateLine(getLineID(createResponse),
			getLineParameter("bg-red-600", "구분당선"));

		// then
		// 지하철_노선_수정_응답됨
		assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 지하철_노선_수정됨
		ExtractableResponse<Response> response = findLine(getLineID(createResponse));
		Line line = response.jsonPath().getObject(".", Line.class);
		assertThat(line.getName()).isEqualTo("구분당선");
	}

	@DisplayName("없는 지하철 노선을 수정한다.")
	@Test
	void updateNotExistsLine() {
		// given
		// 지하철_노선_등록되어 있지 않음

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> modifyResponse = updateLine("1", getLineParameter("bg-red-600", "구분당선"));

		// then
		// 지하철_노선_응답됨
		assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		ExtractableResponse<Response> createResponse = createLine(getLineParameter("bg-red-600", "신분당선"));

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> deleteResponse = deleteLine(getLineID(createResponse));

		// then
		// 지하철_노선_삭제됨_응답
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

		// 지하철_노선_삭제됨
		ExtractableResponse<Response> response = findLine(getLineID(createResponse));
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("없는 지하철 노선을 삭제한다.")
	@Test
	void deleteNotExistsLine() {
		// given
		// 지하철_노선_등록되어 있지 않음

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> deleteResponse = findLine("1");

		// then
		// 지하철_노선_응답됨
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private Map<String, String> getLineParameter(String color, String name) {
		Map<String, String> parameters = new HashMap<>();

		parameters.put("color", color);
		parameters.put("name", name);

		return parameters;
	}

	private ExtractableResponse<Response> createLine(Map<String, String> parameters) {
		return RestAssured
			.given().log().all()
			.body(parameters)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> findLine(String createLineId) {
		return RestAssured
			.given().log().all()
			.when()
			.get("/lines" + "/" + createLineId)
			.then().log().all()
			.extract();
	}

	private String getLineID(ExtractableResponse<Response> createResponse) {
		return createResponse.header("Location").split("/")[2];
	}

	private ExtractableResponse<Response> updateLine(String id, Map<String, String> parameter) {
		return RestAssured
			.given().log().all()
			.body(parameter)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines" + "/" + id)
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> deleteLine(String id) {
		return RestAssured
			.given().log().all()
			.when()
			.delete("/lines" + "/" + id)
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> findAllLines() {
		return RestAssured
			.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();
	}
}
