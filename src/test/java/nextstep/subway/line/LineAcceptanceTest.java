package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.GSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    @LocalServerPort
    int port;

    private LineRequest 신분당선, 이호선, 구분당선;

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("노선 생성하면 해당 노선이 검색이 가능하다.")
    void 노선_생성하면_노선_목록에서_조회가능() {
        // when
        LineResponse 생성된_신분당선 = 노선_생성(신분당선);

        // then
        assertThat(노선_목록()).contains(생성된_신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("여러개 노선 생성시 여러개 노선이 검색이 가능하다.")
    void 여러개_노선_생성후_노선_목록에서_해당노선들_조회가능() {
        // given
        LineResponse 생성된_신분당선 = 노선_생성(신분당선);
        LineResponse 생성된_이호선 = 노선_생성(이호선);

        // when
        final List<LineResponse> 조회한_노선목록 = 노선_목록();

        // then
        assertAll(
                () -> assertThat(조회한_노선목록).hasSize(2),
                () -> assertThat(조회한_노선목록).contains(생성된_신분당선, 생성된_이호선)
        );
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 생성한 지하철 노선을 조회하면
     *  Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("노선 생성 후 조회시 해당 노선에 대한 정보를 받는다.")
    void 노선_조회시_해당_노선정보_응답() {
        // given
        LineResponse 생성된_신분당선 = 노선_생성(신분당선);

        // when
        LineResponse 조회한_신분당선 = 노선_조회(1L);

        // then
        assertThat(조회한_신분당선).isEqualTo(생성된_신분당선);
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 생성한 지하철 노선을 수정하면
     *  Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("노선 생성 후 정보 변경시 해당 노선의 정보가 변경된다.")
    void 노선_생성후_수정시_해당_노선정보가_변경() {
        // given
        LineResponse 생성된_신분당선 = 노선_생성(신분당선);

        // when
        ExtractableResponse<Response> 노선_수정_결과 = 노선_수정(1L, 구분당선);

        // then
        assertThat(노선_수정_결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 생성한 지하철 노선을 삭제하면
     *  Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("노선 생성 후 노선 삭제시 해당 노선의 정보는 삭제된다.")
    void 노선_생성후_삭제시_해당_노선정보_없음() {
        // given
        LineResponse 생성된_신분당선 = 노선_생성(신분당선);

        // when
        ExtractableResponse<Response> 노선_삭제_결과 = 노선_삭제(1L);

        // then
        assertThat(노선_삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static LineResponse 노선_생성(LineRequest 노선) {
        return RestAssured.given().log().all()
                .body(GSON.toJson(노선))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract().jsonPath().getObject("", LineResponse.class);
    }

    public static List<LineResponse> 노선_목록() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("", LineResponse.class);
    }

    public static LineResponse 노선_조회(Long 노선아이디) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + 노선아이디)
                .then().log().all()
                .extract().jsonPath().getObject("", LineResponse.class);
    }

    public static ExtractableResponse<Response> 노선_수정(Long 노선아이디, LineRequest 노선정보) {
        return RestAssured.given().log().all()
                .body(GSON.toJson(노선정보))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + 노선아이디)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제(Long 노선아이디) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + 노선아이디)
                .then().log().all()
                .extract();
    }
}
