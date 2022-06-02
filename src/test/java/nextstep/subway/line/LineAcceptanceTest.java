package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.dto.line.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static nextstep.subway.SubwayApi.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.cleanUp();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철역_노선_이름_목록조회();
        assertThat(lineNames).containsAnyOf("4호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        지하철_노선_생성("1호선", "하늘색", 20, "소요산역", "인천역");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선 색갈로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철 노선 색갈로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        지하철_노선_생성("1호선", "하늘색", 20, "소요산역", "인천역");

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("생성한 모든 노선 목록을 조회한다.")
    @Test
    void findAllLines() {
        // given
        지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역");
        지하철_노선_생성("1호선", "파란색", 10, "소요산역", "인천역");

        // when
        List<String> lineNames = 지하철역_노선_이름_목록조회();

        // then
        assertAll(
                () -> assertThat(lineNames).hasSize(2),
                () -> assertThat(lineNames).containsExactly("4호선", "1호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("생성한 노선을 조회한다.")
    @Test
    void findLineById() {
        // given
        long lineId = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역").jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철역_노선_정보_조회(lineId);
        LineResponse lineResponse = convertLineResponse(response);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("4호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("하늘색")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선 이름과 색갈을 수정한다.")
    @Test
    void modifyLineById() {
        // given
        long lineId = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역").jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> lineModifyResponse = 지하철역_노선_수정(lineId, "1호선", "파란색");

        // then
        assertThat(lineModifyResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        ExtractableResponse<Response> response = 지하철역_노선_정보_조회(lineId);
        LineResponse lineResponse = convertLineResponse(response);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("1호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("파란색")
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 기존에 존재하는 노선이름으로 수정하면
     * Then 해당 지하철 노선 이름은 수정되지 않는다.
     */
    @DisplayName("기존에 존해하는 지하철 노선의 이름으로 수정한다.")
    @Test
    void modifyLineDuplicateName() {
        // given
        지하철_노선_생성("1호선", "파란색", 10, "소요산역", "인천역");
        long lineId = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역").jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철역_노선_수정(lineId, "1호선", "하늘색");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 기존에 존재하는 노선이름으로 수정하면
     * Then 해당 지하철 노선 색갈은 수정되지 않는다.
     */
    @DisplayName("기존에 존해하는 지하철 노선의 색갈로 수정한다.")
    @Test
    void modifyLineDuplicateColor() {
        // given
        지하철_노선_생성("1호선", "파란색", 10, "소요산역", "인천역");
        long lineId = 지하철_노선_생성("4호선", "하늘색", 20, "당고개역", "오이도역").jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철역_노선_수정(lineId, "4호선", "파란색");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        String lineName = "4호선";
        long lineId = 지하철_노선_생성(lineName, "하늘색", 20, "당고개역", "오이도역").jsonPath()
                .getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_삭제(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        List<String> lineNames = 지하철역_노선_이름_목록조회();
        assertThat(lineNames.contains(lineName)).isFalse();
    }
}
