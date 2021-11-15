package nextstep.subway.line;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineInfoResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        verifyLineResponse(response.as(LineResponse.class), LineResponse.of(new Line(lineRequest.getName(), lineRequest.getColor())));
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createSubwayLine(new LineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("신분당선", "bg-blue-600");

        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine(new LineRequest("신분당선", "bg-red-600"));
        LineResponse secondLine = createSubwayLine(new LineRequest("2호선", "bg-green-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestSearchLineInfo("");

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());       

        Assertions.assertThat(response.as(LineInfoResponse[].class)).hasSize(2);

        verifyLineInfoResponse(response.as(LineInfoResponse[].class)[0], LineInfoResponse.of(new Line(redLine.getName(), redLine.getColor())));
        verifyLineInfoResponse(response.as(LineInfoResponse[].class)[1], LineInfoResponse.of(new Line(secondLine.getName(), secondLine.getColor())));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine(new LineRequest("신분당선", "bg-red-600"));
        LineResponse secondLine = createSubwayLine(new LineRequest("2호선", "bg-green-600"));

        LineInfoResponse searchingLine = Arrays.stream(requestSearchLineInfo("").as(LineInfoResponse[].class))
                                                .filter(item -> item.getName().equals("2호선"))
                                                .findFirst()
                                                .get();
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = requestSearchLineInfo(String.valueOf(searchingLine.getId()));

        // then
        // 지하철_노선_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        verifyLineInfoResponse(response.as(LineInfoResponse.class), LineInfoResponse.of(new Line(searchingLine.getName(), searchingLine.getColor())));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine(new LineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_수정_요청
        LineRequest lineRequest = new LineRequest("구분당선", "bg-blue-600");
        ExtractableResponse<Response> response = requestUpdateLine(redLine.getId(), lineRequest);

        // then
        // 지하철_노선_수정됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        
        ExtractableResponse<Response> searchResponse = requestSearchLineInfo("");
        verifyLineInfoResponse(searchResponse.as(LineInfoResponse[].class)[0], LineInfoResponse.of(new Line(lineRequest.getName(), lineRequest.getColor())));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine(new LineRequest("신분당선", "bg-red-600"));

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = requestDeleteLine(redLine.getId());

        // then
        // 지하철_노선_삭제됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> searchResponse = requestSearchLineInfo("");
        Assertions.assertThat(searchResponse.as(LineInfoResponse[].class)).isEmpty();
    }

    private void verifyLineResponse(LineResponse response, LineResponse expectedResponse) throws MultipleFailuresError {
        assertAll(
            () -> Assertions.assertThat(response.getColor()).isEqualTo(expectedResponse.getColor()),
            () -> Assertions.assertThat(response.getName()).isEqualTo(expectedResponse.getName())
        );
    }

    private void verifyLineInfoResponse(LineInfoResponse response, LineInfoResponse expectedResponse) throws MultipleFailuresError {
        assertAll(
            () -> Assertions.assertThat(response.getColor()).isEqualTo(expectedResponse.getColor()),
            () -> Assertions.assertThat(response.getName()).isEqualTo(expectedResponse.getName()),
            () -> Assertions.assertThat(response.getStations()).isEmpty()
        );
    }

    private LineResponse createSubwayLine(LineRequest lineRequest) {
        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        return response.as(LineResponse.class);
    }

    private ExtractableResponse<Response> requestCreateLine(LineRequest lineRequest) {
        return RestAssured.given().log().all().
                            body(lineRequest).
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            post("/lines").
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestSearchLineInfo(String subwayLineId) {
        String requestLineIdURL = "";

        if (subwayLineId != "") {
            requestLineIdURL = "/" + subwayLineId;
        }

        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            get("/lines" + requestLineIdURL).
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestUpdateLine(Long subwayLineId, LineRequest lineRequest) {
        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            body(lineRequest).
                            when().
                            put("/lines/" + subwayLineId).
                            then().
                            log().all().
                            extract();
    }

    private ExtractableResponse<Response> requestDeleteLine(Long subwayLineId) {
        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            when().
                            delete("/lines/" + subwayLineId).
                            then().
                            log().all().
                            extract();
    }
}
