package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

import static nextstep.subway.line.LineTestMethods.*;
import static nextstep.subway.line.LineTestMethods.노선_전체_조회;
import static nextstep.subway.station.StationTestMethods.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("acceptance")
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 노선_생성(
                generateLineRequest("1호선", "blue", "소요산역", "신창역", 10)
        );

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(lineNames(노선_전체_조회())).containsAnyOf("1호선")
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        노선_생성(
                generateLineRequest("1호선", "blue", "소요산역", "신창역", 10)
        );
        노선_생성(
                generateLineRequest("2호선", "green", "합정역", "잠실역", 10)
        );

        //when
        ExtractableResponse<Response> response = 노선_전체_조회();
        List<String> lineNames = lineNames(response);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineNames.size()).isEqualTo(2),
                () -> assertThat(lineNames).contains("1호선", "2호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        ExtractableResponse<Response> createdResponse = 노선_생성(
                generateLineRequest("1호선", "blue", "소요산역", "신창역", 10)
        );
        Long id_1호선 = getId(createdResponse);

        //when
        ExtractableResponse<Response> findResponse = 노선_단건_조회(id_1호선);

        //then
        assertAll(
                () -> assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineName(findResponse)).isEqualTo("1호선"),
                () -> assertThat(stations(findResponse).size()).isEqualTo(2)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        ExtractableResponse<Response> createdResponse = 노선_생성(
                generateLineRequest("1호선", "blue", "소요산역", "신창역", 10)
        );
        Long id_1호선 = getId(createdResponse);

        //when
        LineUpdateRequest newLine = LineUpdateRequest.of("1호선_new", "skyblue");
        노선_수정(id_1호선, newLine);

        //then
        ExtractableResponse<Response> response = 노선_단건_조회(id_1호선);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineName(response)).isEqualTo("1호선_new"),
                () -> assertThat(color(response)).isEqualTo("skyblue")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        ExtractableResponse<Response> createdResponse = 노선_생성(
                generateLineRequest("1호선", "blue", "소요산역", "신창역", 10)
        );
        Long id_1호선 = getId(createdResponse);

        //when
        노선_삭제(id_1호선);

        //then
        ExtractableResponse<Response> response = 노선_전체_조회();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineNames(response)).doesNotContain("1호선")
        );
    }

    private LineRequest generateLineRequest
            (String name, String color, String upStationName, String downStationName, int distance) {
        ExtractableResponse<Response> upStationCreated = 지하철역_생성(upStationName);
        ExtractableResponse<Response> downStationCreated = 지하철역_생성(downStationName);
        return LineRequest.of(name, color,
                upStationCreated.jsonPath().getLong("id"),
                downStationCreated.jsonPath().getLong("id"),
                distance);
    }

    private List<String> lineNames(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    private String lineName(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("name");
    }

    private List<Station> stations(ExtractableResponse<Response> response) {
        return response.jsonPath().getList("stations");
    }

    private String color(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("color");
    }

    private Long getId(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }
}
