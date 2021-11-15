package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.testFactory.AcceptanceTestFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	private static final String lineServicePath="/lines";

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		// 지하철 노선은 이름과 색깔을 속성으로 가진다.
		Map<String, String> params = AcceptanceTestFactory.getNameAndColorContent("신분당선","bg-red-600");

		// when
		// 지하철 노선 생성 요청
		ExtractableResponse<Response> response = AcceptanceTestFactory.post(params,lineServicePath);

		// then
		// 지하철 노선 생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		// 지하철 노선 A가 등록되어있다.
		Map<String, String> params =  AcceptanceTestFactory.getNameAndColorContent("신분당선","bg-red-600");
		AcceptanceTestFactory.post(params,lineServicePath);

		// when
		// 지하철 노선 A와 동일한 이름으로 노선 등록을 요청한다.
		ExtractableResponse<Response> response = AcceptanceTestFactory.post(params,lineServicePath);

		// then
		// 중복 등록 오류로 생성에 실패한다.
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철 노선이 등록되어 있다.
		Map<String, String> params1 = AcceptanceTestFactory.getNameAndColorContent("신분당선","bg-red-600");
		ExtractableResponse<Response> createResponse1 = AcceptanceTestFactory.post(params1,lineServicePath);

		Map<String, String> params2 = AcceptanceTestFactory.getNameAndColorContent("2호선","bg-green-600");
		ExtractableResponse<Response> createResponse2 =   AcceptanceTestFactory.post(params2,lineServicePath);

		// when
		// 지하철 노선 목록을 요청한다.
		ExtractableResponse<Response> response = AcceptanceTestFactory.get(lineServicePath);

		// then
		// 지하철 노선 목록을 응답한다.
		// 등록했던 노선이 포함되어있는지 확인한다.
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
			.map(it -> Long.parseLong(AcceptanceTestFactory.getIdFromHeaderLocation(it)))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(it -> it.getId())
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("특정 지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철 노선이 등록되어 있다.
		Map<String, String> params = AcceptanceTestFactory.getNameAndColorContent("신분당선","bg-red-600");
		ExtractableResponse<Response> createResponse = AcceptanceTestFactory.post(params,lineServicePath);
		String createdLineId = AcceptanceTestFactory.getIdFromHeaderLocation(createResponse);

		// when
		// 등록 되어있는 지하철 노선을 조회한다.
		ExtractableResponse<Response> response = AcceptanceTestFactory.get(lineServicePath+"/"+ createdLineId);

		// then
		// 지하철 노선을 응답한다.
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getObject(".", LineResponse.class).getId()).isEqualTo(
			Long.parseLong(createdLineId));
	}

	@DisplayName("없는 지하철 노선을 조회한다.")
	@Test
	void getLine2() {
		// given
		// 조회 대상 지하철 노선이 등록되어있지 않다.
		String notCreatedLineId = "1";
		// when
		// 등록 되어있는 지하철 노선을 조회한다.
		ExtractableResponse<Response> response = AcceptanceTestFactory.get(lineServicePath+"/"+ notCreatedLineId);

		// then
		// 없는 지하철역 수정 오류
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철 노선이 등록되어있다.
		Map<String, String> params = AcceptanceTestFactory.getNameAndColorContent("신분당선","bg-red-600");
		ExtractableResponse<Response> createResponse =  AcceptanceTestFactory.post(params,lineServicePath);
		String createdLineId = AcceptanceTestFactory.getIdFromHeaderLocation(createResponse);

		// when
		// 지하철 노선 수정 요청
		Map<String, String> params2 = AcceptanceTestFactory.getNameAndColorContent("구분당","bg-blue-600");
		ExtractableResponse<Response> response = AcceptanceTestFactory.put(params,lineServicePath+"/" + createdLineId);

		// then
		// 지하철 노선 수정 정상 처리
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("없는 지하철 노선을 수정한다.")
	@Test
	void updateLine2() {
		// given
		// 수정 대상 지하철 노선이 등록되어있지 않다.
		String notCreatedLineId = "1";

		// when
		// 지하철 노선 수정 요청
		Map<String, String> params = AcceptanceTestFactory.getNameAndColorContent("구분당","bg-blue-600");
		ExtractableResponse<Response> response = AcceptanceTestFactory.put(params,lineServicePath+"/" + notCreatedLineId);

		// then
		// 없는 지하철 수정 요청 오류
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철 노선이 등록되어있다.
		Map<String, String> params = AcceptanceTestFactory.getNameAndColorContent("신분당선","bg-red-600");
		ExtractableResponse<Response> createResponse =  AcceptanceTestFactory.post(params,lineServicePath);
		String createdLineId = AcceptanceTestFactory.getIdFromHeaderLocation(createResponse);

		// when
		// 지하철 노선 제거 요청
		ExtractableResponse<Response> response =  AcceptanceTestFactory.delete(lineServicePath+"/" + createdLineId);

		// then
		// 지하철 노선 삭제됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
