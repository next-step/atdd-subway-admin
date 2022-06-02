package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private static final String STATIONS_URI = "/stations";
    private static final String STATION_NAME_KEY = "name";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        String 강남역 = "강남역";
        ExtractableResponse<Response> 강남역_생성_응답 = 지하철역_생성(강남역);

        지하철역_생성됨(강남역_생성_응답);

        생성된_지하철역_찾기(강남역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        String 강남역 = "강남역";
        지하철역_생성(강남역);

        ExtractableResponse<Response> 강남역_생성응답 = 지하철역_생성(강남역);

        지하철역_생성_안됨(강남역_생성응답);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        String 논현역 = "논현역";
        String 신논현역 = "신논현역";
        지하철역_생성(논현역);
        지하철역_생성(신논현역);

        생성된_지하철역_찾기(논현역, 신논현역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        String 논현역 = "논현역";
        ExtractableResponse<Response> 논현역_생성_응답 = 지하철역_생성(논현역);

        지하철역_삭제(논현역_생성_응답);

        지하철역_목록에서_찾을_수_없음(논현역);
    }

    private ExtractableResponse<Response> 지하철역_생성(String 지하철역_이름) {
        Map<String, String> params = new HashMap<>();
        params.put(STATION_NAME_KEY, 지하철역_이름);

        return post(STATIONS_URI, params);
    }

    private void 지하철역_생성됨(ExtractableResponse<Response> 지하철역_생성_응답) {
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private List<String> 지하철역_목록_조회() {
        return get(STATIONS_URI).jsonPath().getList(STATION_NAME_KEY, String.class);
    }

    private void 생성된_지하철역_찾기(String... 지하철역_이름) {
        List<String> 지하철역_목록 = 지하철역_목록_조회();
        assertThat(지하철역_목록).contains(지하철역_이름);
    }

    private void 지하철역_생성_안됨(ExtractableResponse<Response> 지하철역_생성_응답) {
        assertThat(지하철역_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철역_삭제(ExtractableResponse<Response> 지하철역_생성_응답) {
        delete(지하철역_생성_응답.header(HttpHeaders.LOCATION));
    }

    private void 지하철역_목록에서_찾을_수_없음(String... 지하철역_이름) {
        List<String> 지하철역_목록 = 지하철역_목록_조회();
        assertThat(지하철역_목록).doesNotContain(지하철역_이름);
    }
}
