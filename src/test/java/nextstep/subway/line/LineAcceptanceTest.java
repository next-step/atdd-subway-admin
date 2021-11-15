package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    private LineRepository lineRepository;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green");
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        lineRepository.save(new Line("2호선", "bg-green"));

        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green");
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        Line lineTwo = lineRepository.save(new Line("2호선", "bg-green"));
        Line lineFour = lineRepository.save(new Line("4호선", "bg-blue"));

        // when
        // 지하철_노선_목록_조회_요청
        Response response = RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines");

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        ValidatableResponse then = response.then().log().all();
        then.body("$", hasSize(2));
        then.body("id", hasItems(lineTwo.getId().intValue(), lineFour.getId().intValue()));
        then.extract();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Line lineTwo = lineRepository.save(new Line("2호선", "bg-green"));

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/{id}", lineTwo.getId())
            .then().log().all().extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Line lineTwo = lineRepository.save(new Line("2호선", "bg-green"));

        // when
        // 지하철_노선_수정_요청
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "4호선");
        params.put("color", "bg-blue");
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}", lineTwo.getId())
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // then
        // 지하철_노선_수정됨
        Line findLine = lineRepository.findById(lineTwo.getId()).orElse(null);
        assertNotNull(findLine);
        assertEquals(params.get("name"), findLine.getName());
        assertEquals(params.get("color"), findLine.getColor());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Line lineTwo = lineRepository.save(new Line("2호선", "bg-green"));

        // when
        // 지하철_노선_제거_요청
        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when()
            .delete("/lines/{id}", lineTwo.getId())
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        // then
        // 지하철_노선_삭제됨
        Line findLine = lineRepository.findById(lineTwo.getId()).orElse(null);
        assertNull(findLine);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all().extract();
    }
}
