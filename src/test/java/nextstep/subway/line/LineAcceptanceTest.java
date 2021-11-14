package nextstep.subway.line;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineInfoResponse;
import nextstep.subway.line.dto.LineResponse;

import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> response = requestCreateLine(params);

        // then
        // 지하철_노선_생성됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        assertAll(
            () -> Assertions.assertThat(response.as(LineResponse.class).getColor()).isEqualTo(params.get("color")),
            () -> Assertions.assertThat(response.as(LineResponse.class).getName()).isEqualTo(params.get("name"))
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createSubwayLine("신분당선", "bg-red-600");

        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-blue-600");

        ExtractableResponse<Response> response = requestCreateLine(params);

        // then
        // 지하철_노선_생성_실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine("신분당선", "bg-red-600");
        LineResponse secondLine = createSubwayLine("2호선", "bg-green-600");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestSearchLineInfo("");

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());       

        Assertions.assertThat(response.as(LineInfoResponse[].class)).hasSize(2);
        assertAll(
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)[0].getColor()).isEqualTo(redLine.getColor()),
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)[0].getName()).isEqualTo(redLine.getName()),
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)[0].getStations()).isEmpty()
        );
        assertAll(
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)[1].getColor()).isEqualTo(secondLine.getColor()),
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)[1].getName()).isEqualTo(secondLine.getName()),
            () -> Assertions.assertThat(response.as(LineInfoResponse[].class)[1].getStations()).isEmpty()
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine("신분당선", "bg-red-600");
        LineResponse secondLine = createSubwayLine("2호선", "bg-green-600");

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

        assertAll(
            () -> Assertions.assertThat(response.as(LineInfoResponse.class).getName()).isEqualTo(searchingLine.getName()),
            () -> Assertions.assertThat(response.as(LineInfoResponse.class).getColor()).isEqualTo(searchingLine.getColor()),
            () -> Assertions.assertThat(response.as(LineInfoResponse.class).getStations()).isEqualTo(searchingLine.getStations())
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse redLine = createSubwayLine("신분당선", "bg-red-600");


        // when
        // 지하철_노선_수정_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "구분당선");
        params.put("color", "bg-blue-600");

        ExtractableResponse<Response> response = requestUpdateLine(String.valueOf(redLine.getId()), params);

        // then
        // 지하철_노선_수정됨
        ExtractableResponse<Response> searchResponse = requestSearchLineInfo("");

        Assertions.assertThat(searchResponse.as(LineInfoResponse[].class)[0].getName()).isEqualTo(params.get("name"));
        Assertions.assertThat(searchResponse.as(LineInfoResponse[].class)[0].getColor()).isEqualTo(params.get("color"));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }

    private LineResponse createSubwayLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        ExtractableResponse<Response> response = requestCreateLine(params);

        return response.as(LineResponse.class);
    }

    private ExtractableResponse<Response> requestCreateLine(Map<String, String> params) {
        return RestAssured.given().log().all().
                            body(params).
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

    private ExtractableResponse<Response> requestUpdateLine(String subwayLineId, Map<String, String> params) {
        return RestAssured.given().log().all().
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            body(params).
                            when().
                            put("/lines/" + subwayLineId).
                            then().
                            log().all().
                            extract();
    }

}
