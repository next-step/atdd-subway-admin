package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionResponse;

public class LineAcceptanceTestResponse {

	public static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	public static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	public static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	public static void 지하철_노선_응답됨(ExtractableResponse<Response> response, String uri) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		String expectedId = uri.split("/")[2];
		assertThat(response.jsonPath().get("id").toString()).isEqualTo(expectedId);
	}

	public static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	public static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	public static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<String> lineLocations) {
		List<Long> expectedLineIds = lineLocations.stream()
			.map(location -> Long.parseLong(location.split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	public static void 지하철_노선_지하철역_목록_포함됨(ExtractableResponse<Response> response, List<Long> expectedStationIds) {
		List<Long> resultLineIds = response.jsonPath().getList("stations", SectionResponse.class).stream()
			.map(SectionResponse::getId)
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedStationIds);
	}
}
