package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.line.TestLineFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    StationRepository stationRepository;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Station anyangStation = createStation(1L, "안양역");
        Station gwanakStation = createStation(2L, "관악역");
        LineRequest lineRequest = new LineRequest("1호선", "blue", anyangStation.getId(), gwanakStation.getId(), 10);

        // when
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineRequest);
        // then
        지하철_노선_생성됨(createResponse);
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> createResponse) {
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given().log().all()
          .accept(MediaType.ALL_VALUE)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(lineRequest)
          .when()
          .post("/lines")
          .then().log().all().extract();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        Station anyangStation = createStation(1L, "안양역");
        Station gwanakStation = createStation(2L, "관악역");
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        지하철_노선_생성_요청(lineRequest);

        // when
        ExtractableResponse<Response> errorResponse = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(errorResponse);
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> errorResponse) {
        assertThat(errorResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Station anyangStation = createStation(1L, "안양역");
        Station gwanakStation = createStation(2L, "관악역");
        지하철_노선_생성_요청(new LineRequest("1호선", "blue", 1L, 2L, 10));
        지하철_노선_생성_요청(new LineRequest("2호선", "green", 1L, 2L, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_조회됨(response);
    }

    private void 지하철_노선_목록_조회됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
          .given().log().all()
          .accept(MediaType.APPLICATION_JSON_VALUE)
          .when().get("/lines")
          .then().log().all()
          .extract();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Station anyangStation = createStation(1L, "안양역");
        Station gwanakStation = createStation(2L, "관악역");
        지하철_노선_생성_요청(new LineRequest("1호선", "blue", 1L, 2L, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청();

        // then
        지하철_노선_응답됨(response);
        지하철_노선_결과확인됨(response);
    }

    private void 지하철_노선_결과확인됨(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getList("stations.id")).contains(1, 2);
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청() {
        return RestAssured
          .given().log().all()
          .accept(MediaType.APPLICATION_JSON_VALUE)
          .when().get("/lines/1")
          .then().log().all()
          .extract();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Station anyangStation = createStation(1L, "안양역");
        Station gwanakStation = createStation(2L, "관악역");
        지하철_노선_생성_요청(new LineRequest("1호선", "blue", 1L, 2L, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청();

        // then
        지하철_노선_수정됨(response);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청() {
        return RestAssured
          .given().log().all()
          .body(new LineRequest("분당선", "bg-blue-600", 1L, 2L, 10))
          .accept(MediaType.ALL_VALUE)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .when().put("/lines/1")
          .then().log().all()
          .extract();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Station anyangStation = createStation(1L, "안양역");
        Station gwanakStation = createStation(2L, "관악역");
        지하철_노선_생성_요청(new LineRequest("1호선", "blue", 1L, 2L, 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청();

        // then
        지하철_노선_삭제됨(response);
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청() {
        return RestAssured
          .given().log().all()
          .accept(MediaType.ALL_VALUE)
          .when().delete("/lines/1")
          .then().log().all()
          .extract();
    }

    private Station createStation(long id, String name) {
        return stationRepository.save(stationOf(id, name));
    }
}
