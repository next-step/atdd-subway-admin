package nextstep.subway.line;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineAndStationResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

	@BeforeEach
	public void initStation() {
		List<StationRequest> stationRequests = new ArrayList<>();

		stationRequests.add(new StationRequest("강남역"));
		stationRequests.add(new StationRequest("역삼역"));
		stationRequests.add(new StationRequest("신촌역"));
		stationRequests.add(new StationRequest("이대역"));

		for (StationRequest stationRequest : stationRequests) {
			ExtractableResponse<Response> response = RestAssured.given().log().all()
				.body(stationRequest)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.when()
				.post("/stations")
				.then().log().all()
				.extract();
			assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
			assertThat(response.header("Location")).isNotBlank();
		}
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		//given
		LineRequest request = createLineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

		// when
		ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

		// then
		지하철_노선_생성됨(response);
	}

	private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
	}

	private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
		return RestAssured.given().log().all()
			.body(request)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();
	}

	@DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
	@Test
	void createLine2() {
		// given
		LineRequest request = createLineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
		지하철_노선_등록되어_있음(request);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(request);

        // then
        지하철_노선_생성_실패됨(response);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineRequest request) {
        return 지하철_노선_생성_요청(request);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
		LineRequest request1 = createLineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        지하철_노선_등록되어_있음(request1);

		LineRequest request2 = createLineRequest("2호선", "bg-green-600", 3L, 4L, 10);
        지하철_노선_등록되어_있음(request2);
		List<LineRequest> requests = new ArrayList<>(Arrays.asList(request1, request2));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(requests, response);
		지하철_노선_목록_구역_포함됨(requests, response);
    }

	private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(List<LineRequest> requests, ExtractableResponse<Response> response) {
		List<String> names = requests.stream().map(LineRequest::getName).collect(toList());
        List<String> resultLineIds = response.jsonPath().getList(".", LineAndStationResponse.class).stream()
            .map(LineAndStationResponse::getName)
            .collect(toList());
		assertThat(resultLineIds).containsAll(names);
    }

    private void 지하철_노선_목록_구역_포함됨(List<LineRequest> requests, ExtractableResponse<Response> response) {
		List<Long> stationResponseIds = response.jsonPath()
			.getList(".", LineAndStationResponse.class)
			.stream()
			.flatMap(s -> s.getStations().stream().map(StationResponse::getId))
			.collect(toList());

		List<Long> ids = new ArrayList<>();
		for (LineRequest request : requests) {
			ids.add(request.getUpStationId());
			ids.add(request.getDownStationId());
		}
		assertThat(stationResponseIds).containsExactlyElementsOf(ids);
	}

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
		LineRequest request = createLineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
		ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(request);
        Long createId = response.jsonPath().getObject(".", LineResponse.class).getId();

        // when
        response = 지하철_노선_조회_요청(createId);

        // then
        지하철_노선_응답됨(response, createId);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response, Long createId) {
        LineAndStationResponse lineAndStationResponse = response.jsonPath().getObject(".", LineAndStationResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineAndStationResponse.getId()).isEqualTo(createId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
		LineRequest request = createLineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(request);
        Long id = response.jsonPath().getObject(".", LineResponse.class).getId();

        // when
		LineRequest updateRequest = createLineRequest("구분당선", "bg-blue-600",1L, 2L, 10);
        response = 지하철_노선_수정_요청(id, updateRequest);

        // then
        지하철_노선_수정됨(response);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
		LineRequest request = createLineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(request);
        Long id = response.jsonPath().getObject(".", LineResponse.class).getId();

        // when
        response = 지하철_노선_제거_요청(id);

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured.given().log().all()
            .when()
            .delete("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

	private LineRequest createLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
		return new LineRequest(name, color, upStationId, downStationId, distance);
	}
}
