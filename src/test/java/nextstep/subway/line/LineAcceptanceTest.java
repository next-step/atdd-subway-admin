package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@Test
	@DisplayName("상행,하행 종점정보를 추가한 노선 생성을 테스트")
	void testCreateLine() {
		StationResponse upStation = 지하철역을_생성요청("강남역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation = 지하철역을_생성요청("잠실역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest = createLineRequest("2호선", "bg-green-600", upStation, downStation);

		ExtractableResponse<Response> createLineResponse = this.지하철노선을_생성_요청(lineRequest);
		assertThat(createLineResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(createLineResponse.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		// 지하철_노선_등록되어_있음
		StationResponse upStation = 지하철역을_생성요청("강남역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation = 지하철역을_생성요청("잠실역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", upStation, downStation);
		지하철노선을_생성_요청(lineRequest);
		// when
		// 지하철_노선_생성_요청
		ExtractableResponse<Response> response = 지하철노선을_생성_요청(lineRequest);
		// then
		// 지하철_노선_생성_실패됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void getLines() {
		// given
		StationResponse upStation1 = 지하철역을_생성요청("강남역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation1 = 지하철역을_생성요청("판교역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest1 = createLineRequest("신분당선", "bg-red-600", upStation1, downStation1);
		StationResponse upStation2 = 지하철역을_생성요청("수원역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation2 = 지하철역을_생성요청("왕십리역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest2 = createLineRequest("분당선", "bg-yellow-600", upStation2, downStation2);

		ExtractableResponse<Response> createResponse1 = 지하철노선을_생성_요청(lineRequest1);
		ExtractableResponse<Response> createResponse2 = 지하철노선을_생성_요청(lineRequest2);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());

		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());

		assertThat(resultLineIds).containsAll(expectedLineIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void getLine() {
		// given
		// 지하철_노선_등록되어_있음
		StationResponse upStation = 지하철역을_생성요청("강남역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation = 지하철역을_생성요청("판교역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", upStation, downStation);
		ExtractableResponse<Response> createResponse = 지하철노선을_생성_요청(lineRequest);

		LineResponse addLineResponse = createResponse.response().getBody().as(LineResponse.class);
		Long id = addLineResponse.getId();

		// when
		// 지하철_노선_조회_요청
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines/" + id)
			.then().log().all()
			.extract();
		// then
		// 지하철_노선_응답됨
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
		assertThat(lineResponse.getId()).isEqualTo(id);
		assertThat(lineResponse.getStations()).containsExactly(upStation, downStation);
	}

	@DisplayName("존재하지 않는 노선 업데이트요청시 오류발생확인")
	@Test
	void test_updateLine_존재하지않음() {
		StationResponse upStation = 지하철역을_생성요청("강남역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation = 지하철역을_생성요청("판교역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", upStation, downStation);
		ExtractableResponse<Response> errorResponse = 지하철노선을_수정요청(999L, lineRequest);
		assertThat(errorResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		// 지하철_노선_등록되어_있음
		StationResponse upStation = 지하철역을_생성요청("강남역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation = 지하철역을_생성요청("판교역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", upStation, downStation);

		ExtractableResponse<Response> addLineResponse = 지하철노선을_생성_요청(lineRequest);
		LineResponse createdLine = addLineResponse.response().getBody().as(LineResponse.class);

		// when
		// 지하철_노선_수정_요청
		StationResponse upStation2 = 지하철역을_생성요청("수원역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation2 = 지하철역을_생성요청("왕십리역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest2 = createLineRequest("분당선", "bg-yellow-600", upStation2, downStation2);
		ExtractableResponse<Response> lineResponse = 지하철노선을_수정요청(createdLine.getId(), lineRequest2);

		// then
		// 지하철_노선_수정됨
		assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 제거한다.")
	@Test
	void deleteLine() {
		// given
		// 지하철_노선_등록되어_있음
		StationResponse upStation = 지하철역을_생성요청("강남역")
			.response()
			.getBody()
			.as(StationResponse.class);
		StationResponse downStation = 지하철역을_생성요청("판교역")
			.response()
			.getBody()
			.as(StationResponse.class);
		LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", upStation, downStation);
		ExtractableResponse<Response> addLineResponse = this.지하철노선을_생성_요청(lineRequest);
		LineResponse createdLine = addLineResponse.response().as(LineResponse.class);
		// when
		// 지하철_노선_제거_요청
		ExtractableResponse<Response> deleteResponse = 지하철노선을_삭제요청(createdLine.getId());
		// then
		// 지하철_노선_삭제됨
		assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@Test
	@DisplayName("없는 아이디를 제거하면 오류발생 확인")
	void test_delete없는아이디() {
		ExtractableResponse<Response> errorResponse = 지하철노선을_삭제요청(999L);
		assertThat(errorResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private LineRequest createLineRequest(String name, String color, StationResponse upStation,
		StationResponse downStation) {
		return new LineRequest(name, color, upStation.getId(), downStation.getId(), 20);
	}

	private ExtractableResponse<Response> 지하철노선을_삭제요청(long deleteId) {
		ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
			.when()
			.delete("/lines/" + deleteId)
			.then()
			.log().all().extract();
		return deleteResponse;
	}

	public static ExtractableResponse<Response> 지하철노선을_생성_요청(LineRequest lineRequest) {
		return RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	private ExtractableResponse<Response> 지하철노선을_수정요청(long createdLineId,
		LineRequest lineRequest) {
		return RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + createdLineId)
			.then().log().all().extract();
	}

	private ExtractableResponse<Response> 지하철역_생성_요청(StationRequest stationRequest) {
		return RestAssured.given().log().all()
			.body(stationRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();
	}
}
