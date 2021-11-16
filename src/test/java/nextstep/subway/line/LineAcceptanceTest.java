package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final Map<String, String> 팔호선 = new HashMap<String, String>() {{
        put("color", "pink");
        put("name", "팔호선");
    }};

    private static final Map<String, String> 이호선 = new HashMap<String, String>() {{
        put("color", "green");
        put("name", "이호선");
    }};

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_등록(팔호선);

        // then
        지하철_노선_생성_확인(response, 팔호선);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록(팔호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록(팔호선);

        // then
        지하철_노선_실패_확인(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createdResponse1 = 지하철_노선_등록(팔호선);
        ExtractableResponse<Response> createdResponse2 = 지하철_노선_등록(이호선);

        // when
        ExtractableResponse response = 지하철_노선_목록_조회();

        // then
        지하철_노선_목록_응답_확인(response);
        지하철_노선_목록_포함_확인(response, createdResponse1.as(LineResponse.class));
        지하철_노선_목록_포함_확인(response, createdResponse2.as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회(createdResponse);

        // then
        지하철_노선_정상_응답_확인(response);
        지하철_노선_포함_확인(response, createdResponse.as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선);

        // when
        ExtractableResponse response = 지하철_노선_수정(createdResponse, 이호선);

        // then
        지하철_노선_정상_응답_확인(response);
        지하철_노선_수정_확인(지하철_노선_조회(createdResponse), 이호선);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선_등록(팔호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거(createdResponse);

        // then
        지하철_노선_삭제_확인(response);
        지하철_노선_조회_없음_확인(createdResponse);
    }

    ExtractableResponse<Response> 지하철_노선_등록(Map<String, String> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    void 지하철_노선_생성_확인(ExtractableResponse<Response> response, Map<String, String> params) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank(),
            () -> assertThat(response.as(LineResponse.class))
                .extracting(LineResponse::getName, LineResponse::getColor)
                .contains(params.get("name"), params.get("color"))
        );
    }

    void 지하철_노선_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    void 지하철_노선_목록_응답_확인(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    void 지하철_노선_목록_포함_확인(ExtractableResponse response, LineResponse lineResponse) {
        List<LineResponse> lineResponses = response.as(new TypeRef<List<LineResponse>>() {
        });

        assertThat(lineResponses)
            .extracting(LineResponse::getId, LineResponse::getColor, LineResponse::getName)
            .contains(tuple(lineResponse.getId(), lineResponse.getColor(), lineResponse.getName()));
    }

    ExtractableResponse<Response> 지하철_노선_조회(ExtractableResponse response) {
        return RestAssured.given().log().all()
            .when()
            .get(response.header("Location"))
            .then().log().all()
            .extract();
    }

    void 지하철_노선_정상_응답_확인(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    void 지하철_노선_포함_확인(ExtractableResponse response, LineResponse lineResponse) {
        assertThat(response.as(LineResponse.class))
            .extracting(LineResponse::getId, LineResponse::getName, LineResponse::getColor)
            .contains(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
    }

    ExtractableResponse<Response> 지하철_노선_수정(ExtractableResponse response, Map<String, String> params) {

        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .put(response.header("Location"))
            .then().log().all()
            .extract();
    }

    void 지하철_노선_수정_확인(ExtractableResponse response, Map<String, String> params) {
        assertThat(response.as(LineResponse.class))
            .extracting(LineResponse::getName, LineResponse::getColor)
            .contains(params.get("name"), params.get("color"));
    }

    ExtractableResponse<Response> 지하철_노선_제거(ExtractableResponse response) {
        return RestAssured.given().log().all()
            .when()
            .delete(response.header("Location"))
            .then().log().all()
            .extract();
    }

    void 지하철_노선_삭제_확인(ExtractableResponse response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    void 지하철_노선_조회_없음_확인(ExtractableResponse response) {
        assertThat(지하철_노선_조회(response).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
