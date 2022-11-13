package nextstep.subway.line;

import static nextstep.subway.line.LineFixture.지하철_노선_목록_조회;
import static nextstep.subway.line.LineFixture.지하철_노선_삭제;
import static nextstep.subway.line.LineFixture.지하철_노선_생성;
import static nextstep.subway.line.LineFixture.지하철_노선_생성후_아이디_반환;
import static nextstep.subway.line.LineFixture.지하철_노선_조회;
import static nextstep.subway.station.StationFixture.지하철역_생성후_아이디_반환;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선 관리 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    Long 지하철역;
    Long 새로운지하철역;
    Long 또다른지하철역;

    @BeforeEach
    void setUpStations() {
        지하철역 = 지하철역_생성후_아이디_반환("지하철역");
        새로운지하철역 = 지하철역_생성후_아이디_반환("새로운지하철역");
        또다른지하철역 = 지하철역_생성후_아이디_반환("또다른지하철역");
    }

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 생성")
    void createLine() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_생성("신분당선", "bg-red-600", 10, 지하철역, 새로운지하철역);

        //then
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 목록 조회")
    void getLines() {
        //given
        지하철_노선_생성("신분당선", "bg-red-600", 10, 지하철역, 새로운지하철역);
        지하철_노선_생성("분당선", "bg-red-600", 10, 지하철역, 또다른지하철역);

        //when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        //then
        assertThat(response.jsonPath().getList("name"))
            .hasSize(2)
            .contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철노선 조회")
    void getLine() {
        //given
        Long 신분당선 = 지하철_노선_생성후_아이디_반환("신분당선", "bg-red-600", 10, 지하철역, 새로운지하철역);

        //when
        ExtractableResponse<Response> response = 지하철_노선_조회(신분당선);

        //then
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다.
     */
    @Test
    @DisplayName("지하철노선 수정")
    void updateLine() {
        //given
        Long 신분당선 = 지하철_노선_생성후_아이디_반환("신분당선", "bg-red-600", 10, 지하철역, 새로운지하철역);

        //when
        LineFixture.지하철_노선_수정(신분당선, "다른분당선", "bg-red-600");

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회(신분당선);
        assertThat(response.jsonPath().getString("name")).isEqualTo("다른분당선");
    }

    /**
     * Given 지하철 노선을 생성하면 When 생성한 지하철 노선을 삭제하면 Then  지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철노선 삭제")
    void deleteLine() {
        //given
        Long 신분당선 = 지하철_노선_생성후_아이디_반환("신분당선", "bg-red-600", 10, 지하철역, 새로운지하철역);

        //when
        지하철_노선_삭제(신분당선);

        //then
        assertThat(지하철_노선_목록_조회().jsonPath().getList("")).isEmpty();
    }

}
