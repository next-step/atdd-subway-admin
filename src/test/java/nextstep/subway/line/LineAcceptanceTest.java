package nextstep.subway.line;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.util.PreDataUtil;

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;
    @Autowired
    PreDataUtil preDataUtil;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        preDataUtil.truncate();
        preDataUtil.station(1L, "역1", null);
        preDataUtil.station(2L, "역2", null);
        preDataUtil.station(3L, "역3", null);
        preDataUtil.station(4L, "역4", null);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다")
    @Test
    void createLine() {
        // when
        LineRequestDto lineRequest = new LineRequestDto("신분당선", "bg-red-600", 1L, 2L, 10L);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);
        checkCreateResponse(lineRequest, createResponse);

        // then
        ExtractableResponse<Response> fetchResponse = fetchLines();
        checkFetchedLines(fetchResponse, lineRequest);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다")
    @Test
    void getLines() {
        // given
        LineRequestDto lineRequest1 = new LineRequestDto("신분당선", "bg-red-600", 1L, 2L, 10L);
        createLine(lineRequest1);
        LineRequestDto lineRequest2 = new LineRequestDto("2호선", "bg-blue-600", 3L, 4L, 20L);
        createLine(lineRequest2);

        // when
        ExtractableResponse<Response> fetchResponse = fetchLines();

        // then
        checkFetchedLines(fetchResponse, lineRequest1, lineRequest2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다")
    @Test
    void getLine() {
        // given
        LineRequestDto lineRequest = new LineRequestDto("신분당선", "bg-red-600", 1L, 2L, 10L);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);
        long lineId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> fetchResponse = fetchLine(lineId);

        // then
        checkFetchedLine(fetchResponse, lineId);
    }

    /**
     * When 존재하지 않는 노선을 조회하면
     * Then 404 응답코드를 받는다
     */
    @DisplayName("존재하지 않는 노선을 조회한")
    @Test
    void notFoundLine() {
        // when
        ExtractableResponse<Response> fetchResponse = fetchLine(1L);

        // then
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(NOT_FOUND);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다")
    @Test
    void updateLine() {
        // given
        LineRequestDto lineRequest = new LineRequestDto("신분당선", "bg-red-600", 1L, 2L, 10L);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);
        long lineId = createResponse.jsonPath().getLong("id");

        // when
        String name = "다른분당선";
        String color = "bg-red-100";
        ExtractableResponse<Response> changeResponse = changeLine(lineId, name, color);
        assertThat(HttpStatus.valueOf(changeResponse.statusCode())).isEqualTo(OK);

        // then
        ExtractableResponse<Response> fetchResponse = fetchLine(lineId);
        checkFetchedLine(fetchResponse, lineId, name, color);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        LineRequestDto lineRequest = new LineRequestDto("신분당선", "bg-red-600", 1L, 2L, 10L);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);
        long lineId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> removeResponse = removeLine(lineId);
        assertThat(HttpStatus.valueOf(removeResponse.statusCode())).isEqualTo(NO_CONTENT);

        // then
        assertThat(HttpStatus.valueOf(fetchLine(lineId).statusCode())).isEqualTo(NOT_FOUND);
    }

    /**
     * When 존재하지 않는 노선을 삭제해도
     * Then 존재하는 노선 삭제와 같은 응답을 받는다
     */
    @DisplayName("존재하지 않는 노선을 삭제한다")
    @Test
    void deleteNotExistsLine() {
        // when
        long notExistsId = 33L;
        ExtractableResponse<Response> removeResponse = removeLine(notExistsId);
        assertThat(HttpStatus.valueOf(removeResponse.statusCode())).isEqualTo(NO_CONTENT);

        // then
        assertThat(HttpStatus.valueOf(fetchLine(notExistsId).statusCode())).isEqualTo(NOT_FOUND);
    }

    private void checkFetchedLines(ExtractableResponse<Response> fetchResponse, LineRequestDto... lineRequests) {
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(OK);
        JsonPath jsonPath = fetchResponse.jsonPath();
        assertThat(jsonPath.getList(".")).hasSize(lineRequests.length);
        assertThat(jsonPath.getList("id", Long.class)).hasSize(lineRequests.length);
        assertThat(jsonPath.getList("name", String.class))
            .containsAll(stream(lineRequests).map(LineRequestDto::getName).collect(Collectors.toList()));
        assertThat(jsonPath.getList("color", String.class))
            .containsAll(stream(lineRequests).map(LineRequestDto::getColor).collect(Collectors.toList()));
        for (int i = 0; i < lineRequests.length; i++) {
            assertThat(jsonPath.getList(String.format("[%d].stations.id", i))).containsExactly(
                lineRequests[i].getUpStationId().intValue(), lineRequests[i].getDownStationId().intValue());
            assertThat(jsonPath.getList(String.format("[%d].stations.name", i), String.class)).hasSize(2);
        }
    }

    private void checkFetchedLine(ExtractableResponse<Response> fetchResponse, Long id) {
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(OK);
        JsonPath jsonPath = fetchResponse.jsonPath();
        assertThat(jsonPath.getLong("id")).isEqualTo(id);
        assertThat(jsonPath.getString("name")).isNotBlank();
        assertThat(jsonPath.getString("color")).isNotBlank();
        assertThat(jsonPath.getList("stations")).isNotNull();
    }

    private void checkFetchedLine(ExtractableResponse<Response> fetchResponse, long id, String name, String color) {
        assertThat(HttpStatus.valueOf(fetchResponse.statusCode())).isEqualTo(OK);
        JsonPath jsonPath = fetchResponse.jsonPath();
        assertThat(jsonPath.getLong("id")).isEqualTo(id);
        assertThat(jsonPath.getString("name")).isEqualTo(name);
        assertThat(jsonPath.getString("color")).isEqualTo(color);
        assertThat(jsonPath.getList("stations")).isNotNull();
    }

    private void checkCreateResponse(LineRequestDto lineRequestDto, ExtractableResponse<Response> createResponse) {
        assertThat(HttpStatus.valueOf(createResponse.statusCode())).isEqualTo(CREATED);
        assertThat(createResponse.jsonPath().getLong("id")).isNotNull();
        assertThat(createResponse.jsonPath().getString("name")).isEqualTo(lineRequestDto.getName());
        assertThat(createResponse.jsonPath().getList("stations.id", Long.class))
            .containsExactly(lineRequestDto.getUpStationId(), lineRequestDto.getDownStationId());
        assertThat(createResponse.jsonPath().getList("stations.name", String.class)).hasSize(2);
    }

    private ExtractableResponse<Response> fetchLines() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> removeLine(long lineId) {
        return RestAssured.given().log().all()
            .when().delete("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> changeLine(long lineId, String name, String color) {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("color", color);
        return RestAssured.given().log().all()
            .body(map)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/" + lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> createLine(LineRequestDto lineRequestDto) {
        return RestAssured.given().log().all()
            .body(lineRequestDto)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    static class LineRequestDto {
        private String name;
        private String color;
        private Long upStationId;
        private Long downStationId;
        private Long distance;

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public Long getUpStationId() {
            return upStationId;
        }

        public Long getDownStationId() {
            return downStationId;
        }

        public Long getDistance() {
            return distance;
        }

        public LineRequestDto(String name, String color, Long upStationId, Long downStationId, Long distance) {
            this.name = name;
            this.color = color;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }
    }

    private ExtractableResponse<Response> fetchLine(Long id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + id)
            .then().log().all()
            .extract();
    }

}
