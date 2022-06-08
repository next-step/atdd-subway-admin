package nextstep.subway.acceptanceTest.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.SectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:/db/truncate.sql", "classpath:/db/data.sql"})
public class LineAcceptance {
    @LocalServerPort
    public int port;
    public ExtractableResponse<Response> _2호선;
    public int _2호선_lineId;

    @BeforeEach
    void setup() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        // given
        _2호선 = createLine("2호선", "bg-red-600", "1", "3", "10");
        _2호선_lineId = _2호선.jsonPath().get("id");
    }

    public ExtractableResponse<Response> createLine(String name, String color, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().log().all()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public List<String> getLines(String jsonPath) {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList(jsonPath, String.class);
    }

    public ExtractableResponse<Response> addSection(int lineId, int upStationId, int downStationId, int distance) {
        Map<String, Integer> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().log().all()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> deleteStation(int lineId, int stationId) {
        return RestAssured.given().log().all()
                .when().log().all()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }

    public List<SectionResponse> getSectionResponse(int lineId) {
        List<SectionResponse> sectionResponses = RestAssured.given().log().all()
                .when().log().all()
                .get("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract()
                .body()
                .jsonPath()
                .getList(".", SectionResponse.class);
        return sectionResponses;
    }
}
