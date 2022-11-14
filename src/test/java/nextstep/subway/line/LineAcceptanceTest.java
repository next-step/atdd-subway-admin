package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.Isolationer;

import static nextstep.subway.line.acceptance.LineAcceptance.*;

import nextstep.subway.line.dto.LineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.station.acceptance.StationAcceptaneRequest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private Isolationer dbIsolation;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        dbIsolation.excute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineDto.CreateRequest 라인요청파라미터 = 라인_생성_요청_파라미터();
        //when
        노선을_생성한다(라인요청파라미터);

        // then
        List<String> lineNames = 노선을_조회한다_노선아이디가_없으면_모든_노선이_조회된다("").jsonPath().getList("name", String.class);
        생성한_노선을_찾을_수_있다(lineNames, 라인요청파라미터.getName());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getAllLine() {
        // given
        노선_2개를_생성한다();

        // then
        List<String> lineNames = 노선을_조회한다_노선아이디가_없으면_모든_노선이_조회된다("").jsonPath().getList("name", String.class);

        // then
        생성한_2개의_노선을_찾을_수_있다(lineNames);

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineDto.CreateRequest 라인요청파라미터 = 라인_생성_요청_파라미터();

        // when
        ExtractableResponse<Response> actual = 노선을_생성한다(라인요청파라미터);

        // then
        생성한_지하철_노선의_정보를_응답받을_수_있다(actual, 라인요청파라미터);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        LineDto.CreateRequest 라인요청파라미터 = 라인_생성_요청_파라미터();
        int id = 노선을_생성한다(라인요청파라미터).jsonPath().get("id");

        // when
        LineDto.UpdateRequest expect = new LineDto.UpdateRequest()
                .setName("신-분당선")
                .setColor("bg-purple-600");

        ExtractableResponse<Response> actual = 지하철_노선을_수정한다(expect, id);

        // then
        지하철_노선의_정보가_수정된다(actual);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        LineDto.CreateRequest 라인요청파라미터 = 라인_생성_요청_파라미터();
        int id = 노선을_생성한다(라인요청파라미터).jsonPath().get("id");

        // when
        해당_노선을_제거한다(id);

        // then
        지하철_노선의_정보가_삭제된다(id);
    }

    public static ExtractableResponse<Response> 노선을_생성한다(LineDto.CreateRequest line) {

        return RestAssured.given()
                .body(line).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선을_조회한다_노선아이디가_없으면_모든_노선이_조회된다(String lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines"+lineId)
                .then().log().all()
                .extract();
    }

    public static void 노선_2개를_생성한다() {
        int upStationId = 지하철역을_생성한다("강남역").jsonPath().get("id");
        int downStationId = 지하철역을_생성한다("양재역").jsonPath().get("id");
        노선을_생성한다(new LineDto.CreateRequest()
                .setName("신분당선")
                .setColor("bg-red-600")
                .setUpStationId(upStationId)
                .setDownStationId(downStationId)
                .setDistance(10));


        int upStationId2 = 지하철역을_생성한다("용산역").jsonPath().get("id");
        int downStationId2 = 지하철역을_생성한다("노량진역").jsonPath().get("id");
        노선을_생성한다(new LineDto.CreateRequest()
                .setName("1호선")
                .setColor("bg-blue-600")
                .setUpStationId(upStationId2)
                .setDownStationId(downStationId2)
                .setDistance(10));
    }

    public static ExtractableResponse<Response> 지하철_노선을_수정한다(LineDto.UpdateRequest line, int lineId) {
        return RestAssured.given()
                .body(line).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/"+lineId)
                .then().log().all()
                .extract();

    }

    public ExtractableResponse<Response> 해당_노선을_제거한다(int stationId) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + stationId)
                .then().log().all()
                .extract();
    }

    public void 생성한_노선을_찾을_수_있다(List<String> actual, String expect) {
        assertThat(actual).containsAnyOf(expect);
    }

    public void 생성한_2개의_노선을_찾을_수_있다(List<String> actual) {
        assertThat(actual).hasSize(2);
    }

    public void 생성한_지하철_노선의_정보를_응답받을_수_있다(ExtractableResponse<Response> response, LineDto.CreateRequest expect) {
        assertAll(
                () -> assertThat(response.jsonPath().get("id").toString()).isEqualTo("1"),
                () -> assertThat(response.jsonPath().get("name").toString()).isEqualTo(expect.getName()),
                ()-> assertThat(response.jsonPath().get("color").toString()).isEqualTo(expect.getColor())
        );
    }

    public void 지하철_노선의_정보가_수정된다(ExtractableResponse<Response> response){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public void 지하철_노선의_정보가_삭제된다(int lineId){
        ExtractableResponse<Response> response = 노선을_조회한다_노선아이디가_없으면_모든_노선이_조회된다("/"+lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }



}
