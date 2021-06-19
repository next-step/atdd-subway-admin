package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

//    @BeforeEach
//    public void setup() {
//        String name = "9호선";
//        String color = "빨강";
//        ExtractableResponse<Response> response = createLine(name, color);
//    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        String name = "1호선";
        String color = "빨강";
        ExtractableResponse<Response> response = createLine(name, color);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(name);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(color);
        assertThat(response.body().jsonPath().getString("createdDate")).isNotNull();
        assertThat(response.body().jsonPath().getString("modifiedDate")).isNotNull();
    }

    private

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {

        // given
        // 지하철_노선_등록되어_있음
        String name = "1호선";
        String color = "빨강";
        ExtractableResponse<Response> response = createLine(name, color);


        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(name);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(color);

        // when
        // 지하철_노선_생성_요청
        response = RestAssured
                .given().log().all()
                .body(LineRequest.of(name,color))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ExtractableResponse<Response> createLine(String name, String color) {
        return RestAssured
                .given().log().all()
                .body(LineRequest.of(name, color))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        LineResponse line1 = createLine("1호선","빨강").body().jsonPath().getObject("$",LineResponse.class);
        LineResponse line2 = createLine("2호선","파랑").body().jsonPath().getObject("$",LineResponse.class);
        LineResponse line3 = createLine("3호선","초록").body().jsonPath().getObject("$",LineResponse.class);
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> array = response.body().jsonPath().getList(".", LineResponse.class);
        assertThat(array).hasSize(3);
        assertThat(array).contains(line1,line2,line3);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse line1 = createLine("1호선","빨강").body().jsonPath().getObject("$", LineResponse.class);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines/1")
                .then().log().all().extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getLong("id")).isEqualTo(line1.getId());

    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse line1 = createLine("1호선","빨강").body().jsonPath().getObject("$",LineResponse.class);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(LineRequest.of("2호선", "초록"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/1")
                .then().log().all().extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        response = RestAssured.given().log().all()
                .when().get("/lines/1")
                .then().log().all().extract();

        assertThat(response.body().jsonPath().getLong("id")).isEqualTo(1L);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("초록");

    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse line1 = createLine("1호선","빨강").body().jsonPath().getObject("$",LineResponse.class);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete("/lines/1")
                .then().log().all().extract();
        // then
        // 지하철_노선_삭제됨

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        response = RestAssured.given().log().all()
                .when().get("/lines/1")
                .then().log().all().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

    }
}
