package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.line.LineSteps.*;
import static nextstep.subway.acceptance.station.StationSteps.*;
import static nextstep.subway.line.LineFixture.*;
import static nextstep.subway.line.SectionTest.논현역_신논현역_거리;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private Long 논현역_ID;
    private Long 신논현역_ID;
    private Long 강남역_ID;
    private Long 역삼역_ID;

    @BeforeEach
    public void setUp() {
        super.setUp();
        논현역_ID = 지하철역_생성(NONHYUN_STATION).jsonPath().getLong("id");
        신논현역_ID = 지하철역_생성(SHINNONHYUN_STATION).jsonPath().getLong("id");
        강남역_ID = 지하철역_생성(GANGNAM_STATION).jsonPath().getLong("id");
        역삼역_ID = 지하철역_생성(YUKSAM_STATION).jsonPath().getLong("id");
    }

    /**
     * When 2개의 지하철 역이 생성되어 있다.
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다.
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createStation() {

        //when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 논현역_ID, 신논현역_ID, 논현역_신논현역_거리);

        //then
        지하철_노선_생성_검증(response);
    }

    /**
     * When 지하철 역이 생성되어 있지 않다.
     * When 지하철 노선을 생성하면
     * Then NOT_FOUND 에러 코드를 응답받는다.
     */
    @DisplayName("존재하지 않는 지하철역으로 지하철 노선을 생성한다.")
    @Test
    void notExistStation() {

        //when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 4L, 5L, 논현역_신논현역_거리);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void findLines() {

        지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 신논현역_ID, 논현역_ID, 논현역_신논현역_거리);
        지하철_노선_생성_요청(이호선_이름, 이호선_색상, 강남역_ID, 역삼역_ID, 논현역_신논현역_거리);

        // When
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // Then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name")).contains(신분당선_이름, 이호선_이름)
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
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 논현역_ID, 신논현역_ID, 논현역_신논현역_거리);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse.header("location"));

        // then
        지하철_노선_검증(response, 신분당선_이름, 신분당선_색상);
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {

        //given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 신논현역_ID, 논현역_ID, 논현역_신논현역_거리);

        //when
        지하철_노선_수정_요청(createResponse.header("location"), 이호선_이름, 이호선_색상);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse.header("location"));

        지하철_노선_검증(response, 이호선_이름, 이호선_색상);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {

        //Given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(신분당선_이름, 신분당선_색상, 논현역_ID, 신논현역_ID, 논현역_신논현역_거리);

        //when
        ExtractableResponse<Response> response = 지하철_노선_삭제_요청(createResponse.header("location"));

        //then
        지하철_노선_삭제_검증(response);
    }
}
