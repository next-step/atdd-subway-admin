package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// gciven, when 지하철_노선_생성_요청
		// + 상행, 하행 정보 요청 파라미터에 함께 추가
		ExtractableResponse<Response> response = 노선정보세팅_메소드("잠실역", "yellow", 1L, 1L, 1);
		// then 지하철_노선_생성됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given 지하철_노선_등록되어_있음
		// + 상행, 하행 정보 요청 파라미터에 함께 추가
		String dupName = "잠실역";
		String dupColor = "yellow";
		노선정보세팅_메소드(dupName, dupColor, 1L, 1L, 1);

		// when 중복 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 노선정보세팅_메소드(dupName, dupColor, 1L, 1L, 1);

		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		// 지하철_노선_등록되어_있음
		// + 상행, 하행 정보 요청 파라미터에 함께 추가
		ExtractableResponse<Response> createdResponse1 = 노선정보세팅_메소드("잠실역", "yellow", 1L, 1L, 1);

		// 지하철_노선_등록되어_있음
		// + 상행, 하행 정보 요청 파라미터에 함께 추가
		ExtractableResponse<Response> createdResponse2 = 노선정보세팅_메소드("강남역", "green", 1L, 1L, 1);

		// when
		// 지하철_노선_목록_조회_요청
		// + 상행 -> 하행역 순으로 정렬되어야 함
		ExtractableResponse<Response> response = get메소드호출("/lines");

		// then
		// 지하철_노선_목록_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		// 지하철_노선_목록_포함됨
		List<Long> expectedLineIds = Arrays.asList(createdResponse1, createdResponse2).stream()
				.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
				.collect(Collectors.toList());

		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class)
				.stream()
				.map(it -> it.id())
				.collect(Collectors.toList());

		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		// + 상행, 하행 정보 요청 파라미터에 함께 추가
		String name = "잠실역";
		노선정보세팅_메소드(name, "yellow", 1L, 1L, 1);

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = get메소드호출("/lines/" + name);

		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		String resultLineName = response.jsonPath().getString("name");

		assertThat(resultLineName).isEqualTo(name);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		// + 상행, 하행 정보 요청 파라미터에 함께 추가
		String name = "잠실역";
		String originColor = "yellow";
		노선정보세팅_메소드(name, originColor, 1L, 1L, 1);

		// when;
		// 지하철_노선_수정_요청
		String newColor = "puple";

		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", newColor);

		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.patch("/lines")
				.then().log().all()
				.extract();

		// then
		// 지하철_노선_수정됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		// + 상행, 하행 정보 요청 파라미터에 함께 추가
		String name = "잠실역";
		노선정보세팅_메소드(name, "yellow", 1L, 1L, 1);

		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> response = RestAssured.given().log().all()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.delete("/lines/" + name)
				.then().log().all()
				.extract();

		// then
		// 지하철_노선_삭제됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private ExtractableResponse<Response> 노선정보세팅_메소드(String name, String color, Long upStationId, Long downStationId, Integer distance){
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);
		params.put("upStationId", upStationId.toString());
		params.put("downStationId", downStationId.toString());
		params.put("distance", distance.toString());

		return RestAssured.given().log().all()
				.body(params)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post("/lines")
				.then().log().all()
				.extract();
	}

	public ExtractableResponse<Response> get메소드호출(String path) {
		return RestAssured.given().log().all()
				.when()
				.get(path)
				.then().log().all()
				.extract();
	}
}
