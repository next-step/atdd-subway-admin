package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.fixtures.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철노선 관련 기능")
class LineAcceptanceTest extends TestFixtures {

    /**
     * When 지하철 노선을 생성하면
     * <p>
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        생성(노선("신분당선", "bg-red-600", "1", "2", "10"), "/lines");

        //then
        List<String> lineNames = 목록조회("name", "/lines");

        //then
        assertThat(lineNames).contains("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * <p>
     * When 지하철 노선 목록을 조회하면
     * <p>
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        생성(노선("신분당선", "bg-red-600", "1", "2", "10"), "/lines");
        생성(노선("분당선", "bg-green-600", "1", "3", "10"), "/lines");

        //when
        List<String> lineNames = 목록조회("name", "/lines");

        //then
        assertThat(lineNames).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 조회하면
     * <p>
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLineByName() {
        //given
        생성(노선("신분당선", "bg-red-600", "1", "2", "10"), "/lines");

        //when
        String lineName = 조회("/lines/{name}", "신분당선", "name");

        //then
        assertThat(lineName).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 수정하면
     * <p>
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void modifyLine() {
        //given
        생성(노선("신분당선", "bg-red-600", "1", "2", "10"), "/lines");

        //when
        ExtractableResponse<Response> response =
                수정(노선("신분당선", "bg-green-600", "1", "2", "10"), "/lines/{name}", "신분당선");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성하고
     * <p>
     * When 생성한 지하철 노선을 삭제하면
     * <p>
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선 정보를 제거한다.")
    @Test
    void deleteLine() {
        //given
        String id = 생성_값_리턴(노선("신분당선", "bg-red-600", "1", "2", "10"), "/lines", "id");

        //when
        ExtractableResponse<Response> response = 삭제("/lines/{id}", id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> 노선(String name, String color, String upStationId, String downStationId,
                                   String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}
