package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

public class LineAcceptanceMethods extends AcceptanceTest {

	public static ExtractableResponse<Response> 지하철_노선_제거_요청(LineResponse line) {
		return delete("/lines/" + line.getId());
	}

	public static ExtractableResponse<Response> 지하철_노선_수정_요청(LineResponse line, String updateName, String updateColor) {
		LineRequest lineRequest = createLineRequest(updateName, updateColor);
		return put("/lines/" + line.getId(), lineRequest);
	}

	public static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
		return get("/lines/" + id);
	}

	public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
		return get("/lines");
	}

	public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
		LineRequest lineRequest = createLineRequest(name, color);
		return post("/lines", lineRequest);
	}

	public static LineResponse 지하철_노선_등록되어_있음(String name, String color) {
		LineRequest lineRequest = createLineRequest(name, color);
		ExtractableResponse<Response> response = post("/lines", lineRequest);

		return response.as(LineResponse.class);
	}

	public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	public static void 지하철_노선_포함됨(ExtractableResponse<Response> response, LineResponse line) {
		LineResponse resultLine = response.jsonPath()
										  .getObject(".", LineResponse.class);
		assertThat(resultLine.getId()).isEqualTo(line.getId());
	}

	public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<LineResponse> expectedLines) {
		List<Long> expectedLineIds = expectedLines.stream()
												  .map(LineResponse::getId)
												  .collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath()
										   .getList(".", LineResponse.class)
										   .stream()
										   .map(LineResponse::getId)
										   .collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	public static void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		Assertions.assertThat(response.header("Location")).isNotBlank();
	}

	public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private static LineRequest createLineRequest(String name, String color) {
		return new LineRequest(name, color);
	}

	private LineAcceptanceMethods() {}
}
