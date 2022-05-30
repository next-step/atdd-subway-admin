package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.DatabaseCleanUp;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        databaseCleanUp.execute();
    }

    /*
    * When 지하철 노선을 생성하면
    * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
    * */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLineTest() {
        //when 지하철 노선 생성
        String lineName = "2호선";
        ExtractableResponse<Response> createdLine = createLine(lineName, "bg-green-200", "강남역", "대림역",10);

        //then 지하철 노선 목록 조회 시 createdLine의 name을 찾을 수 있다.
        ExtractableResponse<Response> lines = fetchAllLines();
        List<String> lineNames = lines.jsonPath().getList("name", String.class);
        assertThat(lineNames).contains(lineName);
    }

    /*
    * Given 2개의 지하철 노선을 생성하고
    * When 지하철 노선 목록을 조회하면
    * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
    * */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLinesTest() {
        String line2Name = "2호선";
        String line9Name = "9호선";

        //given : 2개의 지하철 노선 생성
        ExtractableResponse<Response> line2 = createLine(line2Name, "bg-green-200", "강남역", "대림역",10);
        ExtractableResponse<Response> line9 = createLine(line9Name, "bg-gold-200", "당산역", "올림픽공원역",20);

        //when : 지하철 노선 목록을 조회
        ExtractableResponse<Response> lines = fetchAllLines();
        List<String> lineNames = lines.jsonPath().getList("name", String.class);

        //then : 지하철 노선 목록 조회 시 2개의 노선 조회 가능
        assertThat(lineNames).contains(line2Name);
        assertThat(lineNames).contains(line9Name);
    }

    /*
    * Given 지하철 노선을 생성하고
    * When 생성한 지하철 노선을 조회하면
    * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
    *  */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLineTest() {
        String line2Name = "2호선";
        // given : 지하철 노선을 생성
        ExtractableResponse<Response> line2 = createLine(line2Name, "bg-green-200", "강남역", "대림역",10);
        Long createdLineId = (long) (int) line2.jsonPath().get("id");

        // when : 생성한 지하철 노선을 조회
        ExtractableResponse<Response> createdLine = fetchLineById(createdLineId);

        //then 지하철 노선의 정보를 응답 받는다.
        assertAll(
                () -> assertThat((String) createdLine.jsonPath().get("name")).isEqualTo(line2Name),
                () -> assertThat((String) createdLine.jsonPath().get("color")).isEqualTo("bg-green-200"),
                () -> assertThat(createdLine.jsonPath().getList("stations", StationResponse.class).size()).isEqualTo(2)
        );
    }

    /*
    * Given 지하철 노선을 생성하고
    * When 생성한 지하철 노선을 수정하면
    * Then 해당 지하철 노선 정보는 수정된다
    * */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLineTest() {
        //given : 지하철 노선 생성
        String line2Name = "2호선";
        ExtractableResponse<Response> line2 = createLine(line2Name, "bg-green-200", "충정로역", "왕십리역",10);

        //when : 생성한 지하철 노선을 수정
        Long id = (long) (int) line2.jsonPath().get("id");
        String updatedName = "5호선";
        String updatedColor = "bg-purple-200";
        updateLine(id, updatedName, updatedColor);

        //then : 해당 지하철 노선 정보는 수정된다.
        ExtractableResponse<Response> updatedLine = fetchLineById(id);
        assertThat((String) updatedLine.jsonPath().get("name")).isEqualTo(updatedName);
        assertThat((String) updatedLine.jsonPath().get("color")).isEqualTo(updatedColor);
    }

    /*
    * Given 지하철 노선을 생성하고
    * When 생성한 지하철 노선을 삭제하면
    * Then 해당 지하철 노선 정보는 삭제된다
    * */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLineTest() {
        //given : 지하철 노선 생성
        String line2Name = "2호선";
        ExtractableResponse<Response> line2 = createLine(line2Name, "bg-green-200", "강남역", "대림역",10);

        //when : 생성한 지하철 노선 삭제
        Long lineId = (long) (int) line2.jsonPath().get("id");
        deleteLine(lineId);

        //then : 해당 지하철 노선 정보는 삭제된다.
        ExtractableResponse<Response> response = fetchLineById(lineId);
        assertThat(response.statusCode()).isEqualTo(404);
    }

    /*
     * When 존재하지 않는 지하철 노선을 조회 시
     * Then 404 Not Found 응답이 전달된다.
    * */
    @DisplayName("존재하지 않는 지하철 노선 조회")
    @Test
    void notExistLineFetchTest() {
        //When : 존재하지 않는 지하철 노선 조회
        ExtractableResponse<Response> response = fetchLineById(1L);
        //Then : Not Found
        assertThat(response.statusCode()).isEqualTo(404);
    }

    /*
     * When 존재하지 않는 지하철 노선을 수정 시
     * Then 404 Not Found 응답이 전달된다.
     * */
    @DisplayName("존재하지 않는 지하철 노선 수정")
    @Test
    void notExistLineUpdateTest() {
        ExtractableResponse<Response> response = updateLine(1L, "테스트노선이름", "bg-red-200");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    /*
     * When 존재하지 않는 지하철 노선을 삭제 시
     * Then 404 Not Found 응답이 전달된다.
     * */
    @DisplayName("존재하지 않는 지하철 노선 삭제")
    @Test
    void notExistLineDeleteTest() {
        ExtractableResponse<Response> response = deleteLine(1L);

        assertThat(response.statusCode()).isEqualTo(404);
    }

    private ExtractableResponse<Response> createLine(String name, String color, String upStationName, String downStationName, int distance) {
        Long upStationId = createStation(upStationName);
        Long downStationId = createStation(downStationName);

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return post("/lines", params);
    }

    private ExtractableResponse<Response> fetchAllLines() {
        return get("/lines");
    }

    private ExtractableResponse<Response> fetchLineById(Long id) {
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", id);
        return get("/lines/{id}", pathParams);
    }

    private ExtractableResponse<Response> updateLine(Long id, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("id", id);

        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", id);

        return put("/lines/{id}", pathParams, params);
    }

    private ExtractableResponse<Response> deleteLine(Long id) {
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("id", id);

        return delete("/lines/{id}", pathParams);
    }

    private Long createStation(String name) {
        Map<String, Object > params = new HashMap<>();
        params.put("name", name);
        ExtractableResponse<Response> result = post("/stations", params);

        int id = result.jsonPath().get("id");
        return (long) id;
    }

    private ExtractableResponse<Response> post(String path, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> get(String path, Map<String, ?> pathParams) {
        RequestSpecification restAssured = RestAssured.given().log().all();

        return restAssured
                .when().get(path, pathParams)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> get(String path) {
        return RestAssured.given().log().all()
                .when().get(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> put(String path, Map<String, ?> pathParams, Map<String, ?> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path, pathParams)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> delete(String path, Map<String, ?> pathParams) {
        return RestAssured.given().log().all()
                .when().delete(path, pathParams)
                .then().log().all()
                .extract();
    }

}
