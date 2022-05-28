package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.station.StationApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        지하철노선_생성_성공("2호선", "yellow", "강남역", "교대역", 10);

        // then
        List<String> findNames = 지하철노선_목록_조회_성공();
        assertThat(findNames).contains("2호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        지하철노선_생성_성공("1호선", "blue", "영등포역", "신길역", 5);
        지하철노선_생성_성공("2호선", "yellow", "강남역", "교대역", 10);

        // when
        List<String> findNames = 지하철노선_목록_조회_성공();

        // then
        assertThat(findNames).hasSize(2);
        assertThat(findNames).contains("1호선", "2호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findLine() {
        // given
        String location = 지하철노선_생성_성공("1호선", "blue", "영등포역", "신길역", 5);

        // when
        LineResponse findLine = 지하철노선_조회_성공(location);

        // then
        assertThat(findLine.getName()).isEqualTo("1호선");
        assertThat(findLine.getColor()).isEqualTo("blue");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String location = 지하철노선_생성_성공("1호선", "blue", "영등포역", "신길역", 5);

        // when
        지하철노선_수정_성공(location, "신분당선", "red");

        // then
        LineResponse findLine = 지하철노선_조회_성공(location);
        assertThat(findLine.getName()).isEqualTo("신분당선");
        assertThat(findLine.getColor()).isEqualTo("red");
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
        String location = 지하철노선_생성_성공("1호선", "blue", "영등포역", "신길역", 5);

        // when
        지하철노선_삭제_성공(location);

        // then
        지하철노선_조회_실패(location);
    }

    String 지하철노선_생성_성공(String name, String color, String upStationName, String downStationName, long distance) {
        long upStationId = StationApi.create(upStationName).jsonPath().getLong("id");
        long downStationId = StationApi.create(downStationName).jsonPath().getLong("id");
        ExtractableResponse<Response> response = LineApi.create(new LineRequest(name, color, upStationId, downStationId, distance));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response.header("location");
    }

    List<String> 지하철노선_목록_조회_성공() {
        ExtractableResponse<Response> response = LineApi.findAll();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response.jsonPath().getList("name", String.class);
    }

    LineResponse 지하철노선_조회_성공(String location) {
        ExtractableResponse<Response> response = LineApi.find(location);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return response.jsonPath().getObject("", LineResponse.class);
    }

    void 지하철노선_수정_성공(String location, String name, String color) {
        ExtractableResponse<Response> response = LineApi.update(location, new LineUpdateRequest(name, color));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    void 지하철노선_삭제_성공(String location) {
        ExtractableResponse<Response> response = LineApi.delete(location);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    void 지하철노선_조회_실패(String location) {
        ExtractableResponse<Response> response = LineApi.find(location);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
