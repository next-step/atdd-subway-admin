package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleaner;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.execute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createStationLine() {
        // when
        String LINE_2 = "2호선";
        지하철_노선_생성_강남_잠실(LINE_2);

        // then
        ExtractableResponse<Response> response = LineAcceptanceTestFixture.지하철_노선_목록_조회();
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.body().jsonPath().getList("name", String.class)).contains(LINE_2)
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void getStationLines() {
        // given
        String LINE_2 = "2호선";
        지하철_노선_생성_강남_잠실(LINE_2);

        String LINE_BUNDANG = "분당선";
        지하철_노선_생성_왕십리_죽전(LINE_BUNDANG);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestFixture.지하철_노선_목록_조회();

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.body().jsonPath().getList("name", String.class)).contains(LINE_2, LINE_BUNDANG)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void getStationLine() {
        // given
        String LINE_2 = "2호선";
        Long id = 지하철_노선_생성_강남_잠실(LINE_2).body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestFixture.지하철_노선_조회(id);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_2)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateStationLine() {
        // given
        Long id = 지하철_노선_생성_강남_잠실("2호선").body().jsonPath().getLong("id");

        // when
        String CHANGED_NAME = "8호선";
        String CHANGED_COLOR = "bg-red-600";
        ExtractableResponse<Response> response = LineAcceptanceTestFixture.지하철_노선_수정(id, CHANGED_NAME, CHANGED_COLOR);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo(CHANGED_NAME),
            () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo(CHANGED_COLOR)
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteStationLine() {
        // given
        Long id = 지하철_노선_생성_강남_잠실("2호선").body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestFixture.지하철_노선_삭제(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
