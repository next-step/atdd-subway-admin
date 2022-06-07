package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static nextstep.subway.line.LineAcceptanceTest.지하철노선_조회_요청;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.line.TestLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@Sql("/truncate.sql")
@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    int port;
    public static Map<String, String> params;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        params = new HashMap<>();
    }

    //   * Given : 지하철 노선 생성하고
    //    * When : 기존 노선에 있는 상행역과 신규 하행역을 사이에 추가하면
    //    * Then : 추가된 노선을 사이에 포함하여 차례되로 조회되는 것을 볼 수 있다.
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        //Given : 지하철 노선 생성하고
        지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        지하철역_생성_요청("양재시민의숲역");

        // when
        // 기존 노선에 있는 상행역과 신규 하행역을 사이에 추가하면
        ExtractableResponse<Response> 지하철구간_추가_요청 = 지하철구간_추가_요청(1L, 3L, 5L, 1L);
        // then
        // 추가된 노선을 사이에 포함하여 차례되로 조회되는 것을 볼 수 있다.
        ExtractableResponse<Response> 지하철노선_조회_요청 = 지하철노선_조회_요청(1L);
        지하철역_순서_확인(지하철노선_조회_요청, Arrays.asList("강남역", "양재시민의숲역", "판교역"));
    }

    private void 지하철역_순서_확인(ExtractableResponse<Response> getResponse, List<String> stationNames) {
        assertThat(getResponse.jsonPath().getList("stations.name")).hasSameElementsAs(stationNames);
    }

    private static ExtractableResponse<Response> 지하철구간_추가_요청(Long upStationId,
        Long downStationId, Long distance, Long lineId) {
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
            .body(params)
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }
}
