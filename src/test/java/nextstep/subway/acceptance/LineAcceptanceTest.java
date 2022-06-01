package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {
    public static final String path = "/lines";

    private Long 지하철역_ID;
    private Long 새로운지하철역_ID;
    private Long 또다른지하철역_ID;

    @BeforeEach
    public void setUp() {
        super.setUp();
        지하철역_ID = StationAcceptanceTest.지하철역_생성_id_반환("지하철역");
        새로운지하철역_ID = StationAcceptanceTest.지하철역_생성_id_반환("새로운지하철역");
        또다른지하철역_ID = StationAcceptanceTest.지하철역_생성_id_반환("또다른지하철역");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철노선_생성("신분당선", "bg-red-600", 지하철역_ID, 새로운지하철역_ID, 10);

        // then
        응답결과_확인(response, HttpStatus.CREATED);
        지하철노선_존재함(전체_지하철노선_이름_조회(), "신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철노선_생성("신분당선", "bg-red-600", 지하철역_ID, 새로운지하철역_ID, 10);
        지하철노선_생성("분당선", "bg-green-600", 지하철역_ID, 또다른지하철역_ID, 5);

        //when
        ExtractableResponse<Response> response = 전체_지하철노선_조회();

        //then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_개수_같음(response, 2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long expected = 지하철노선_생성_id_반환("신분당선", "bg-red-600", 지하철역_ID, 새로운지하철역_ID, 10);

        //when
        ExtractableResponse<Response> response = 지하철노선_조회(expected);

        //then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_id_같음(response, expected);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long id = 지하철노선_생성_id_반환("신분당선", "bg-red-600", 지하철역_ID, 새로운지하철역_ID, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_수정(id, "다른분당선", "bg-red-600");

        // then
        응답결과_확인(response, HttpStatus.OK);
        지하철노선_이름_같음(지하철노선_조회(id), "다른분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        long id = 지하철노선_생성_id_반환("신분당선", "bg-red-600", 지하철역_ID, 새로운지하철역_ID, 10);

        // when
        ExtractableResponse<Response> response = 지하철노선_삭제(id);

        // then
        응답결과_확인(response, HttpStatus.NO_CONTENT);
    }

    public static ExtractableResponse<Response> 지하철노선_생성(String name, String color, long upStationId, long downStationId,
                                                   int distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);
        return post(path, lineRequest);
    }

    public static Long 지하철노선_생성_id_반환(String name, String color, long upStationId, long downStationId, int distance) {
        return 지하철노선_생성(name, color, upStationId, downStationId, distance)
                .jsonPath()
                .getLong("id");
    }

    public static ExtractableResponse<Response> 지하철노선_수정(long id, String name, String color) {
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(name, color);
        return put(path, id, lineUpdateRequest);
    }

    public static ExtractableResponse<Response> 전체_지하철노선_조회() {
        return get(path);
    }

    public static ExtractableResponse<Response> 지하철노선_조회(long id) {
        return get(path, id);
    }

    public static List<String> 전체_지하철노선_이름_조회() {
        return 전체_지하철노선_조회()
                .jsonPath()
                .getList("name", String.class);
    }

    private ExtractableResponse<Response> 지하철노선_삭제(long id) {
        return delete(path, id);
    }

    private void 지하철노선_개수_같음(ExtractableResponse<Response> response, int size) {
        assertThat(response.jsonPath().getList(".").size()).isEqualTo(size);
    }

    private void 지하철노선_id_같음(ExtractableResponse<Response> response, long expected) {
        assertThat(response.jsonPath().getLong("id")).isEqualTo(expected);
    }

    private void 지하철노선_이름_같음(ExtractableResponse<Response> response, String expected) {
        assertThat(response.jsonPath().getString("name")).isEqualTo(expected);
    }

    private void 지하철노선_존재함(List<String> lineNames, String searchName) {
        assertThat(lineNames).containsAnyOf(searchName);
    }

}
