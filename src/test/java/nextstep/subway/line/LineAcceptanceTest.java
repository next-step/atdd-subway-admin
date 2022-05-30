package nextstep.subway.line;

import static nextstep.subway.line.LineAssuredMethod.노선_삭제_요청;
import static nextstep.subway.line.LineAssuredMethod.노선_생성_요청;
import static nextstep.subway.line.LineAssuredMethod.노선_수정_요청;
import static nextstep.subway.line.LineAssuredMethod.노선_전체_조회_요청;
import static nextstep.subway.line.LineAssuredMethod.노선_조회_요청;
import static nextstep.subway.station.StationAssuredMethod.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends BaseAcceptanceTest {

    private Long 지하철역_id;
    private Long 새로운지하철역_id;
    @BeforeEach
    void saveStations() {
        지하철역_id = 지하철역_생성_요청("지하철역").jsonPath().getLong("id");
        새로운지하철역_id = 지하철역_생성_요청("새로운지하철역").jsonPath().getLong("id");
    }
    /***
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        //given
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 지하철역_id, 새로운지하철역_id, 10);

        //when
        ExtractableResponse<Response> 노선_생성_요청_응답 = 노선_생성_요청(신분당선);
        노선_생성_성공_확인(노선_생성_요청_응답);

        //then
        List<String> 노선_이름_목록 = 노선_이름_목록을_구한다();
        노선이름_포함_확인(노선_이름_목록, "신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void findAllLines() {
        //given
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 지하철역_id, 새로운지하철역_id, 10);
        LineRequest 분당선 = LineRequest.of("분당선", "bg-green-600", 지하철역_id, 새로운지하철역_id, 10);

        //when
        노선_생성_요청(신분당선);
        노선_생성_요청(분당선);

        //then
        List<String> 노선_이름_목록 = 노선_이름_목록을_구한다();
        노선이름_포함_확인(노선_이름_목록, "신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void findOneLine() {
        //given
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 지하철역_id, 새로운지하철역_id, 10);
        ExtractableResponse<Response> 노선_생성_요청_응답 = 노선_생성_요청(신분당선);
        long id = 노선의_id_구한다(노선_생성_요청_응답);

        //when
        ExtractableResponse<Response> 노선_조회_요청_응답 = 노선_조회_요청(id);

        //then
        조회_노선_이름_확인(노선_조회_요청_응답, "신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
        //given
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 지하철역_id, 새로운지하철역_id, 10);
        ExtractableResponse<Response> 노선_생성_요청_응답 = 노선_생성_요청(신분당선);
        long id = 노선의_id_구한다(노선_생성_요청_응답);
        LineRequest 다른_분당선 = LineRequest.of("다른분당선", "bg-red-600", 지하철역_id, 새로운지하철역_id, 10);

        //when
        ExtractableResponse<Response> 노선_수정_요청_응답 = 노선_수정_요청(id, 다른_분당선);

        //then
        수정_확인(노선_수정_요청_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
        //given
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 지하철역_id, 새로운지하철역_id, 10);
        ExtractableResponse<Response> 노선_생성_요청_응답 = 노선_생성_요청(신분당선);
        long id = 노선의_id_구한다(노선_생성_요청_응답);

        //when
        ExtractableResponse<Response> 노선_삭제_요청_응답 = 노선_삭제_요청(id);

        //then
        삭제_확인(노선_삭제_요청_응답);
    }

    private void 삭제_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 수정_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 조회_노선_이름_확인(ExtractableResponse<Response> 노선_조회_요청_응답, String name) {
        assertThat(노선_조회_요청_응답.jsonPath().getString("name")).isEqualTo(name);
    }

    private long 노선의_id_구한다(ExtractableResponse<Response> 노선_생성_요청_응답) {
        return 노선_생성_요청_응답.jsonPath().getLong("id");
    }

    private List<String> 노선_이름_목록을_구한다() {
        return 노선_전체_조회_요청().jsonPath().getList("name", String.class);
    }
    private void 노선이름_포함_확인(List<String> lineNames, String... lineName) {
        assertThat(lineNames).containsAll(Arrays.asList(lineName));
    }

    private void 노선_생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
