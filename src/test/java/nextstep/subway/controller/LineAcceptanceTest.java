package nextstep.subway.controller;

import static nextstep.subway.util.AcceptanceFixture.결과에_존재한다;
import static nextstep.subway.util.AcceptanceFixture.목록_이름_조회;
import static nextstep.subway.util.AcceptanceFixture.목록_조회;
import static nextstep.subway.util.AcceptanceFixture.아이디_조회;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nextstep.subway.util.AcceptanceTest;
import nextstep.subway.util.DBClean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map 노선_정보 = 맵("2호선", "회색");
        // when
        지하철_노선_정상_생성(노선_정보);

        // then
        ExtractableResponse<Response> 지하철역_목록 = 지하철_노선_목록_조회();
        assertTrue(결과에_존재한다(지하철역_목록, "2호선"));
    }

    /**
     * When 노선명을 null로 노선을 생성하면
     * Then 400 BAD_REQUEST를 리턴한다.
     */
    @DisplayName("지하철 노선을 생성시 노선명이 null이면 400 리턴")
    @Test
    void createLine_null_badRequest() {
        // given
        Map 노선_정보_노선명_없음 = 맵(null, "회색");
        // when
        ExtractableResponse<Response> 노선명_없음_노선_생성_결과 = 지하철_노선_생성(노선_정보_노선명_없음);

        // then
        assertThat(노선명_없음_노선_생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 노선명을 중복해서 노선을 생성하면
     * Then 400 BAD_REQUEST를 리턴한다.
     */
    @DisplayName("지하철 노선을 생성시 unique 위반 400 리턴")
    @Test
    void createLine_not_unique_badRequest() {
        // given
        Map 노선_정보 = 맵("2호선", "회색");
        지하철_노선_정상_생성(노선_정보);
        Map 노선_정보_노선명_중복 = 맵("2호선", "녹색");
        // when
        ExtractableResponse<Response> 노선명_중복_노선_생성_결과 = 지하철_노선_생성(노선_정보_노선명_중복);

        // then
        assertThat(노선명_중복_노선_생성_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void getAllLine() {
        // given
        Map 노선정보_2호선 = 맵("2호선", "회색");
        Map 노선정보_3호선 = 맵("3호선", "노란색");
        지하철_노선_정상_생성(노선정보_2호선);
        지하철_노선_정상_생성(노선정보_3호선);

        // when
        ExtractableResponse<Response> 지하철역_목록 = 지하철_노선_목록_조회();

        // then
        assertAll(
                ()->assertTrue(결과에_존재한다(지하철역_목록, "2호선")),
                ()->assertTrue(결과에_존재한다(지하철역_목록, "3호선"))
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void getLine() {
        // given
        Map 노선_정보 = 맵("2호선", "회색");
        Long 생성된_노선ID = 지하철_노선_정상_생성(노선_정보);

        // when
        ExtractableResponse<Response> 노선_조회_결과 =  아이디로_지하철_노선_조회(생성된_노선ID);
        Map 조회된_노선 = 목록_조회(노선_조회_결과);
        // then
        assertTrue(노선_정보가_동일하다(조회된_노선, 노선_정보));
    }


    /**
     * When 없는 지하철 노선을 조회하면
     * Then 404 NOT_FOUND를 리턴한다.
     */
    @DisplayName("없는 지하철 노선 조회")
    @Test
    void getLine_NotFound() {

        // when
        ExtractableResponse<Response> 조회_결과 = 아이디로_지하철_노선_조회(1L);

        // then
        assertThat(조회_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
        // given
        Map 노선_정보 = 맵("2호선", "회색");
        Long 생성된_노선ID = 지하철_노선_정상_생성(노선_정보);

        // then
        Map 변경_노선_정보 = 맵("3호선", "노란색");
        ExtractableResponse<Response> 수정_결과 = 지하철_노선_수정(생성된_노선ID,변경_노선_정보);
        Map 변경된_노선_정보 = 목록_조회(수정_결과);

        // then
        assertTrue(노선_정보가_동일하다(변경_노선_정보, 변경된_노선_정보));
    }

    /**
     * When 없는 지하철 노선을 수정하려 하면
     * Then 404 NOT_FOUND를 리턴한다.
     */
    @DisplayName("없는 지하철 노선 수정")
    @Test
    void updateLine_NotFound() {

        // when
        ExtractableResponse<Response> 수정_결과 = 지하철_노선_수정(1L, 맵("2호선", "회색"));

        // then
        assertThat(수정_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
        // given
        Map 노선_정보 = 맵("2호선", "회색");
        Long 생성된_노선ID = 지하철_노선_정상_생성(노선_정보);
        // when
        ExtractableResponse<Response> 삭제_결과 = 지하철_노선_삭제(생성된_노선ID);

        // then
        ExtractableResponse<Response> 지하철역_목록 = 지하철_노선_목록_조회();
        assertAll(
                ()->assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                ()->assertFalse(결과에_존재한다(지하철역_목록, "2호선"))
        );
    }

    /**
     * When 없는 지하철 노선을 삭제하려 하면
     * Then 404 NOT_FOUND를 리턴한다.
     */
    @DisplayName("없는 지하철 노선 삭제")
    @Test
    void deleteLine_NotFound() {

        // when
        ExtractableResponse<Response> 삭제_결과 = 지하철_노선_삭제(1L);

        // then
        assertThat(삭제_결과.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }




    private Long 지하철_노선_정상_생성(Map 노선_정보) {

        ExtractableResponse<Response> 생성_결과 =  RestAssured.given().log().all()
                .body(노선_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
        return 아이디_조회(생성_결과);
    }

    private ExtractableResponse<Response> 지하철_노선_생성(Map 노선_정보) {

        return RestAssured.given().log().all()
                .body(노선_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {

        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
    private ExtractableResponse<Response> 지하철_노선_수정(Long 변경할_노선_아이디, Map 변경할_노선_정보) {

        return RestAssured.given().log().all()
                .pathParam("id",변경할_노선_아이디)
                .body(변경할_노선_정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().patch("/lines/{id}")
                .then().log().all()
                .extract();
    }
    private ExtractableResponse<Response> 아이디로_지하철_노선_조회(Long 아이디) {
        return RestAssured.given().log().all()
                .pathParam("id", 아이디)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }
    private boolean 노선_정보가_동일하다(Map 노선, Map 노선_정보) {
        return 노선.get("name").equals(노선_정보.get("name")) && 노선.get("color").equals(노선_정보.get("color"));
    }


    private ExtractableResponse<Response> 지하철_노선_삭제(Long 삭제_노선_아이디) {
        return RestAssured.given().log().all()
                .pathParam("id", 삭제_노선_아이디)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }
    private Map 맵(String 노선명, String 노선색){
        Map<String, String> 노선 = new HashMap<>();
        노선.put("name", 노선명);
        노선.put("color", 노선색);
        return 노선;
    }
}