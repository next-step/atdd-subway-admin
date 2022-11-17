package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.line.LineSteps.*;
import static nextstep.subway.acceptance.station.StationSteps.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 2개의 지하철 역이 생성되어 있다.
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createStation() {

        Long 신논현역_ID = 지하철역_생성(SHINNONHYUN_STATION).jsonPath().getLong("id");
        Long 논현역_ID = 지하철역_생성(NONHYUN_STATION).jsonPath().getLong("id");
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red", 신논현역_ID, 논현역_ID, 4);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void findLines() {

        // Given
        Long 신논현역_ID = 지하철역_생성(SHINNONHYUN_STATION).jsonPath().getLong("id");
        Long 논현역_ID = 지하철역_생성(NONHYUN_STATION).jsonPath().getLong("id");
        지하철_노선_생성_요청("신분당선", "red", 신논현역_ID, 논현역_ID, 4);

        Long 강남역_ID = 지하철역_생성(GANGNAM_STATION).jsonPath().getLong("id");
        Long 역삼역_ID = 지하철역_생성(YUKSAM_STATION).jsonPath().getLong("id");
        지하철_노선_생성_요청("2호선", "green", 강남역_ID, 역삼역_ID, 4);

        // When
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name")).contains("신분당선", "2호선")
        );
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void findLine() {
        // Given
        Long 신논현역_ID = 지하철역_생성(SHINNONHYUN_STATION).jsonPath().getLong("id");
        Long 논현역_ID = 지하철역_생성(NONHYUN_STATION).jsonPath().getLong("id");
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "red", 신논현역_ID, 논현역_ID, 4);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선")
        );
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // Given
        Long 신논현역_ID = 지하철역_생성(SHINNONHYUN_STATION).jsonPath().getLong("id");
        Long 논현역_ID = 지하철역_생성(NONHYUN_STATION).jsonPath().getLong("id");
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("신분당선", "red", 신논현역_ID, 논현역_ID, 4);

        // when
        지하철_노선_수정_요청(createResponse.header("location"), "분당선", "blue");

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("blue"),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("분당선")
        );
    }
}
