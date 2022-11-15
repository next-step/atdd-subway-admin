package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceTest.생성된_지하철_노선_ID_조회;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_조회;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    private Long 판교역_ID;
    private Long 강남역_ID;
    private Long 신분당선_구간_ID;
    private Long 신규_역_ID;
    private ExtractableResponse<Response> 지하철_구간_추가_결과;

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 구간을 추가하면
     * Then 노선에 새로운 지하철 역이 등록된다
     */
    @DisplayName("노선 중간에 지하철 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        int distance = 10;
        String lineName = "신분당선";
        판교역_ID = 생성된_지하철_역_ID_조회("판교역");
        강남역_ID = 생성된_지하철_역_ID_조회("강남역");
        신분당선_구간_ID = 생성된_지하철_노선_ID_조회(lineName, "주황색", 판교역_ID, 강남역_ID, distance);
        신규_역_ID = 생성된_지하철_역_ID_조회("양재역");

        // when
        지하철_구간_추가_결과 = 지하철_구간_추가(신분당선_구간_ID, 신규_역_ID, 강남역_ID, 5);

        // then
        지하철_구간_추가_성공_확인(지하철_구간_추가_결과);

        // then
        지하철_추가된_구간_조회_확인(지하철_구간_추가_결과, lineName, "판교역", "양재역", "강남역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행성 종점에 지하철 구간을 추가하면
     * Then 노선에 새로운 지하철 역이 등록된다
     */
    @DisplayName("상행 종점에 지하철 구간을 추가한다.")
    @Test
    void addUpSection() {
        // given
        int distance = 10;
        String lineName = "신분당선";
        판교역_ID = 생성된_지하철_역_ID_조회("판교역");
        강남역_ID = 생성된_지하철_역_ID_조회("강남역");
        신분당선_구간_ID = 생성된_지하철_노선_ID_조회(lineName, "주황색", 판교역_ID, 강남역_ID, distance);
        신규_역_ID = 생성된_지하철_역_ID_조회("양재역");

        // when
        지하철_구간_추가_결과 = 지하철_구간_추가(신분당선_구간_ID, 신규_역_ID, 판교역_ID, 5);

        // then
        지하철_구간_추가_성공_확인(지하철_구간_추가_결과);

        // then
        지하철_추가된_구간_조회_확인(지하철_구간_추가_결과, lineName, "양재역", "판교역", "강남역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 하행선 종점에 지하철 구간을 추가하면
     * Then 노선에 새로운 지하철 역이 등록된다
     */
    @DisplayName("하행 종점에 지하철 구간을 추가한다.")
    @Test
    void addDownSection() {
        // given
        int distance = 10;
        String lineName = "신분당선";
        판교역_ID = 생성된_지하철_역_ID_조회("판교역");
        강남역_ID = 생성된_지하철_역_ID_조회("강남역");
        신분당선_구간_ID = 생성된_지하철_노선_ID_조회(lineName, "주황색", 판교역_ID, 강남역_ID, distance);
        신규_역_ID = 생성된_지하철_역_ID_조회("양재역");

        // when
        지하철_구간_추가_결과 = 지하철_구간_추가(신분당선_구간_ID, 강남역_ID, 신규_역_ID, 5);

        // then
        지하철_구간_추가_성공_확인(지하철_구간_추가_결과);

        // then
        지하철_추가된_구간_조회_확인(지하철_구간_추가_결과, lineName, "판교역", "강남역", "양재역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 추가하는 지하철 구간의 거리가 기존구간과 동일하거나 클 경우
     * Then 새로운 지하철 역을 추가할 수 없다
     */
    @DisplayName("구간 거리가 동일하거나 더 클 경우 역을 추가할 수 없다")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void addSectionException(int newDistance) {
        // given
        int distance = 10;
        판교역_ID = 생성된_지하철_역_ID_조회("판교역");
        강남역_ID = 생성된_지하철_역_ID_조회("강남역");
        신분당선_구간_ID = 생성된_지하철_노선_ID_조회("신분당선", "주황색", 판교역_ID, 강남역_ID, distance);
        신규_역_ID = 생성된_지하철_역_ID_조회("양재역");

        // when
        지하철_구간_추가_결과 = 지하철_구간_추가(신분당선_구간_ID, 신규_역_ID, 강남역_ID, newDistance);

        // then
        지하철_구간_추가_실패(지하철_구간_추가_결과);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 상행역, 하행역 모두 노선에 등록되어 있다면
     * Then 새로운 지하철 역을 추가할 수 없다
     */
    @DisplayName("상행역, 하행역 모두 존재할 경우 추가할 수 없다")
    @Test
    void addExistSectionException() {
        // given
        int distance = 10;
        판교역_ID = 생성된_지하철_역_ID_조회("판교역");
        강남역_ID = 생성된_지하철_역_ID_조회("강남역");
        신분당선_구간_ID = 생성된_지하철_노선_ID_조회("신분당선", "주황색", 판교역_ID, 강남역_ID, distance);

        // when
        지하철_구간_추가_결과 = 지하철_구간_추가(신분당선_구간_ID, 판교역_ID, 강남역_ID, 4);

        // then
        지하철_구간_추가_실패(지하철_구간_추가_결과);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 추가하는 상행역, 하행역 모두 존재하지 않으면
     * Then 새로운 지하철 역을 추가할 수 없다
     */
    @DisplayName("상행역, 하행역 모두 존재하지 않는 경우 추가할 수 없다")
    @Test
    void addNotExistSectionException() {
        // given
        int distance = 10;
        판교역_ID = 생성된_지하철_역_ID_조회("판교역");
        강남역_ID = 생성된_지하철_역_ID_조회("강남역");
        신분당선_구간_ID = 생성된_지하철_노선_ID_조회("신분당선", "주황색", 판교역_ID, 강남역_ID, distance);

        신규_역_ID = 생성된_지하철_역_ID_조회("양재역");
        Long 두번째_신규_역_ID = 생성된_지하철_역_ID_조회("양재시민의숲");

        // when
        지하철_구간_추가_결과 = 지하철_구간_추가(신분당선_구간_ID, 신규_역_ID, 두번째_신규_역_ID, 4);

        // then
        지하철_구간_추가_실패(지하철_구간_추가_결과);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 종점을 삭제하면
     * Then 해당 역은 더 이상 조회되지 않는다
     */
    @DisplayName("종점을 삭제할 수 있다")
    @Test
    void deleteEndSection() {
        // given
        int distance = 10;
        String lineName = "신분당선";
        판교역_ID = 생성된_지하철_역_ID_조회("판교역");
        강남역_ID = 생성된_지하철_역_ID_조회("강남역");
        신분당선_구간_ID = 생성된_지하철_노선_ID_조회(lineName, "주황색", 판교역_ID, 강남역_ID, distance);
        신규_역_ID = 생성된_지하철_역_ID_조회("양재역");
        지하철_구간_추가(신분당선_구간_ID, 신규_역_ID, 판교역_ID, 4);

        // when
        지하철_구간_삭제(신분당선_구간_ID, 신규_역_ID);

        // then
        ExtractableResponse<Response> 지하철_노선_조회_결과 = 지하철_노선_조회(신분당선_구간_ID);
        지하철_추가된_구간_조회_확인(지하철_노선_조회_결과, lineName, "판교역", "강남역");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 가운데 역을 삭제하면
     * Then 해당 역은 더 이상 조회되지 않는다
     */
    @DisplayName("가운데 역을 삭제할 수 있다")
    @Test
    void deleteMiddleSection() {
        // given
        int distance = 10;
        String lineName = "신분당선";
        판교역_ID = 생성된_지하철_역_ID_조회("판교역");
        강남역_ID = 생성된_지하철_역_ID_조회("강남역");
        신분당선_구간_ID = 생성된_지하철_노선_ID_조회(lineName, "주황색", 판교역_ID, 강남역_ID, distance);
        신규_역_ID = 생성된_지하철_역_ID_조회("양재역");
        지하철_구간_추가(신분당선_구간_ID, 신규_역_ID, 강남역_ID, 4);

        // when
        지하철_구간_삭제(신분당선_구간_ID, 신규_역_ID);

        // then
        ExtractableResponse<Response> 지하철_노선_조회_결과 = 지하철_노선_조회(신분당선_구간_ID);
        지하철_추가된_구간_조회_확인(지하철_노선_조회_결과, lineName, "판교역", "강남역");
    }

    private ExtractableResponse<Response> 지하철_구간_추가(
            Long lineId,
            Long upStationId,
            Long downStationId,
            int distance
    ) {
        LineRequest request = new LineRequest.Builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private void 지하철_구간_추가_실패(ExtractableResponse<Response> 지하철_구간_추가_결과) {
        assertThat(지하철_구간_추가_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_구간_추가_성공_확인(ExtractableResponse<Response> 지하철_구간_추가_결과) {
        assertThat(지하철_구간_추가_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_추가된_구간_조회_확인(
            ExtractableResponse<Response> 지하철_구간_추가_결과,
            String expectLineName,
            String... expectStationNames
    ) {
        List<String> stationNames = 지하철_구간_추가_결과
                .jsonPath()
                .getList("stations.name", String.class);
        String lineName = 지하철_구간_추가_결과.jsonPath()
                        .getString("name");

        assertAll(
                () -> assertThat(lineName).isEqualTo(expectLineName),
                () -> assertThat(stationNames.size()).isEqualTo(expectStationNames.length),
                () -> assertThat(stationNames).containsAll(Arrays.asList(expectStationNames))
        );
    }

    private ExtractableResponse<Response> 지하철_구간_삭제(Long 구간_id, Long 제거_역_id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}/sections?stationId={stationId}", 구간_id, 제거_역_id)
                .then().log().all()
                .extract();
    }
}
