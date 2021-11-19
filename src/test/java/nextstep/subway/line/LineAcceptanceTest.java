package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 노선_미리생성(new LineRequest("2호선", "green"));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        노선_미리생성(new LineRequest("2호선", "green"));

        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "green");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest secondLine = new LineRequest("2호선", "green");
        노선_미리생성(secondLine);
        // 지하철_노선_등록되어_있음
        LineRequest thirdLine = new LineRequest("3호선", "orange");
        노선_미리생성(thirdLine);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        List<String> lineRequestNames = Arrays.asList(secondLine.getName(), thirdLine.getName());
        List<String> lineResponseNames = response.jsonPath().getList(".", LineResponse.class)
                .stream()
                .map(lineResponse -> lineResponse.getName())
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponseNames).containsAll(lineRequestNames)
        );
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest secondLine = new LineRequest("2호선", "green");
        ExtractableResponse<Response> saveResponse = 노선_미리생성(secondLine);
        Long savedLineId = saveResponse.body().jsonPath().getLong("id");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines/" + savedLineId)
                .then().log().all().extract();

        // then
        // 지하철_노선_응답됨
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().getLong("id")).isEqualTo(savedLineId)
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> saveResponse = 노선_미리생성(new LineRequest("2호선", "green"));
        LineResponse lineResponse = saveResponse.body().as(LineResponse.class);

        // when
        // 지하철_노선_수정_요청
        LineRequest lineRequest = new LineRequest(lineResponse.getName(), "red");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineResponse.getId())
                .then().log().all().extract();

        // then
        // 지하철_노선_수정됨
        LineResponse newLineResponse = response.body().as(LineResponse.class);
        assertAll(
                () -> assertThat(newLineResponse.getId()).isEqualTo(lineResponse.getId()),
                () -> assertThat(newLineResponse.getColor()).isEqualTo("red")
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> saveResponse = 노선_미리생성(new LineRequest("2호선", "green"));
        LineResponse lineResponse = saveResponse.body().as(LineResponse.class);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/" + lineResponse.getId())
                .then().log().all().extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 노선_미리생성(LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all().extract();
    }
}
