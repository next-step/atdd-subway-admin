package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.응답코드_확인;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;


@Sql("/truncate.sql")
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;
    private Map<String, String> params;


    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        params = new HashMap<>();
    }

    /**
     * When : 지하철 노선을 생성하면 Then : 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createLine() {
        // given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("판교역");
        // when
        ExtractableResponse<Response> 신분당선 = 지하철노선_생성_요청(TestLine.SHINBUNDANG.getName(),
            "bg-red-600", 1L, 2L,
            10L);
        // then
        응답코드_확인(신분당선, HttpStatus.CREATED);
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(신분당선.jsonPath().getLong("id"));
        지하철노선_이름존재_확인(getResponse, TestLine.SHINBUNDANG.getName());
    }

    /**
     * Given: 지하철 노선 하나 생성, When : 동일한 이름의 지하철 노선을 생성하면 Then : Bad Request Error 발생
     */
    @DisplayName("존재하는 지하철노선을 생성한다.")
    @Test
    void createLine_duplicated() {
        // given
        지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);

        // when
        ExtractableResponse<Response> 신분당선 = 지하철노선_생성_요청(TestLine.SHINBUNDANG.getName(),
            "bg-red-600", 1L, 2L,
            10L);
        // then
        응답코드_확인(신분당선, HttpStatus.BAD_REQUEST);
    }


    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철역 목록 조회한다.")
    @Test
    void getStations() {
        //given
        지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        지하철_노선_등록되어_있음(TestLine.NUMBER_2);
        //when
        ExtractableResponse<Response> getResponse = 지하철노선_목록_조회_요청();
        //then
        응답코드_확인(getResponse, HttpStatus.OK);
        지하철노선목록_이름_여러개존재_확인(getResponse,
            Arrays.asList(TestLine.SHINBUNDANG.getName(), TestLine.NUMBER_2.getName()));
    }


    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        //given
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        //when
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(신분당선.jsonPath().getLong("id"));
        //then
        응답코드_확인(getResponse, HttpStatus.OK);
        지하철노선_이름존재_확인(getResponse, TestLine.SHINBUNDANG.getName());
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        //given
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);

        //when
        지하철노선_수정_요청(신분당선.jsonPath().getLong("id"), "다른분당선", "bg-green-600");
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(신분당선.jsonPath().getLong("id"));

        //then
        응답코드_확인(getResponse, HttpStatus.OK);
        지하철노선_이름존재_확인(getResponse, "다른분당선");
    }

    /**
     * Given 지하철 노선을 생성하고 When 존재하지 않는 지하철 노선을 수정하면 Then Not Found 에러 발생
     */
    @DisplayName("없는 지하철역을 수정한다.")
    @Test
    void update_not_exist_station() {
        //given
        지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);

        //when
        ExtractableResponse<Response> updateResponse = 지하철노선_수정_요청(999999L, "다른분당선",
            "bg-green-600");

        //then
        응답코드_확인(updateResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        //given
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);

        //when
        ExtractableResponse<Response> deleteResponse = 지하철노선_삭제_요청(신분당선.jsonPath().getLong("id"));

        //then
        응답코드_확인(deleteResponse, HttpStatus.NO_CONTENT);
        ExtractableResponse<Response> getResponse = 지하철노선_목록_조회_요청();
        지하철노선_목록에_없음(getResponse, TestLine.SHINBUNDANG.getName());
    }

    /**
     * Given 지하철 노선을 생성하고 When 존재하지 않는 지하철 노선을 삭제하면 Then Not Found 에러 발생
     */
    @DisplayName("없는 지하철역을 삭제한다.")
    @Test
    void delete_not_exist_station() {
        //given
        지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);

        //when
        ExtractableResponse<Response> deleteResponse = 지하철노선_삭제_요청(99999L);

        //then
        응답코드_확인(deleteResponse, HttpStatus.NOT_FOUND);
    }

    private void 지하철노선_목록에_없음(ExtractableResponse<Response> getResponse, String name) {
        assertThat(getResponse.jsonPath().getList("name")).doesNotContain(name);
    }

    private void 지하철노선목록_이름_여러개존재_확인(ExtractableResponse<Response> getResponse,
        List<String> names) {
        assertThat(getResponse.jsonPath().getList("name")).hasSameElementsAs(names);
    }

    private void 지하철노선_이름존재_확인(ExtractableResponse<Response> getResponse, String name) {
        assertThat(getResponse.jsonPath().getString("name")).isEqualTo(name);
    }

    private ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(Long id, String name, String color) {
        params.put("name", name);
        params.put("color", color);
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", id)
            .when().put("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
            .pathParam("id", id)
            .when().get("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
            .pathParam("id", id)
            .when().delete("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, Long upStationId,
        Long downStationId, Long distance) {
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(TestLine testLine) {
        if (testLine.equals(TestLine.NUMBER_2)) {
            ExtractableResponse<Response> 잠실역 = 지하철역_생성_요청("잠실역");
            ExtractableResponse<Response> 건대역 = 지하철역_생성_요청("건대역");
            return 지하철노선_생성_요청(testLine.getName(), "bg-blue-600",
                잠실역.jsonPath().getLong("id"),
                건대역.jsonPath().getLong("id"), 10L);
        }
        if (testLine.equals(TestLine.SHINBUNDANG)) {
            ExtractableResponse<Response> 강남역 = 지하철역_생성_요청("강남역");
            ExtractableResponse<Response> 판교역 = 지하철역_생성_요청("판교역");
            return 지하철노선_생성_요청(testLine.getName(), "bg-red-600",
                강남역.jsonPath().getLong("id"),
                판교역.jsonPath().getLong("id"), 10L);
        }
        return null;
    }
}
