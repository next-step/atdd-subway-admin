package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Section;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.util.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    private final LineAcceptanceTest lineAcceptanceTest = new LineAcceptanceTest();
    private final StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();

    private ExtractableResponse<Response> upStation;
    private ExtractableResponse<Response> downStation;
    private ExtractableResponse<Response> newStation;
    private ExtractableResponse<Response> line;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.cleanUp();

        // given
        upStation = stationAcceptanceTest.createStation("강남역");
        downStation = stationAcceptanceTest.createStation("판교역");
        newStation = stationAcceptanceTest.createStation("새로운역");
        line = lineAcceptanceTest.createLine("신분당선", "bg-red-600", 10, upStation.jsonPath().getLong("id"), downStation.jsonPath().getLong("id"));
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 상행역이 기존 생성한 노선의 상행역과 동일하고 구간 길이가 더 짧은 새로운 지하철 구간을 생성하면
     * Then 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정된 지하철 구간이 생성된다.
     */
    @Test
    void 역_사이에_새로운_역을_기존_상행역과_같도록_지하철구간_생성() {
        // when
        ExtractableResponse<Response> response = createSection(line.jsonPath().getLong("id"), upStation.jsonPath().getLong("id"), newStation.jsonPath().getLong("id"), 7);
        List<Section> sections = response.jsonPath().getList("sections", Section.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(sections.get(0).getDistance()).isEqualTo(7),
                () -> assertThat(sections.get(1).getDistance()).isEqualTo(3)
        );
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 하행역이 기존 생성한 노선의 하행역과 동일하고 구간 길이가 더 짧은 새로운 지하철 구간을 생성하면
     * Then 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정된 지하철 구간이 생성된다.
     */
    @Test
    void 역_사이에_새로운_역을_기존_하행역과_같도록_지하철구간_생성() {
        // when
        ExtractableResponse<Response> response = createSection(line.jsonPath().getLong("id"), newStation.jsonPath().getLong("id"), downStation.jsonPath().getLong("id"), 7);
        List<Section> sections = response.jsonPath().getList("sections", Section.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(sections.get(0).getDistance()).isEqualTo(3),
                () -> assertThat(sections.get(1).getDistance()).isEqualTo(7)
        );
    }

    /*
     * Given  지하철 노선을 생성하고
     * When 하행역이 기존 생성한 노선의 상행역과 동일한 새로운 지하철 구간을 생성하면
     * Then 지하철 구간이 생성된다.
     * Then 새로 생성한 구간이 기존 노선보다 앞에 정렬된다.
     */
    @Test
    void 새로운_역을_상행_종점_지하철구간_생성() {
        // when
        ExtractableResponse<Response> response = createSection(line.jsonPath().getLong("id"), newStation.jsonPath().getLong("id"), upStation.jsonPath().getLong("id"), 7);
        List<Section> sections = response.jsonPath().getList("sections", Section.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(sections.get(0).getUpStation().getName()).isEqualTo("새로운역"),
                () -> assertThat(sections.get(0).getDownStation().getName()).isEqualTo("강남역"),
                () -> assertThat(sections.get(1).getUpStation().getName()).isEqualTo("강남역"),
                () -> assertThat(sections.get(1).getDownStation().getName()).isEqualTo("판교역")
        );

    }

    /*
     * Given 지하철 노선을 생성하고
     * When 상행역이 기존 생성한 노선의 하행역과 동일한 새로운 지하철 구간을 생성하면
     * Then 지하철 구간이 생성된다.
     * Then 새로 생성한 구간이 기존 노선보다 뒤에 정렬된다.
     */
    @Test
    void 새로운_역을_하행_종점으로_지하철구간_생성() {
        // when
        ExtractableResponse<Response> response = createSection(line.jsonPath().getLong("id"), downStation.jsonPath().getLong("id"), newStation.jsonPath().getLong("id"), 7);
        List<Section> sections = response.jsonPath().getList("sections", Section.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(sections.get(0).getUpStation().getName()).isEqualTo("강남역"),
                () -> assertThat(sections.get(0).getDownStation().getName()).isEqualTo("판교역"),
                () -> assertThat(sections.get(1).getUpStation().getName()).isEqualTo("판교역"),
                () -> assertThat(sections.get(1).getDownStation().getName()).isEqualTo("새로운역")
        );
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 상행역이 기존 생성한 노선의 상행역과 동일하고 구간 길이가 같은 새로운 지하철 구간을 생성하면
     * Then 예외가 발생한다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    void 지하철구간_생성_예외_1() {
        // when
        ExtractableResponse<Response> response = createSection(line.jsonPath().getLong("id"), downStation.jsonPath().getLong("id"), newStation.jsonPath().getLong("id"), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 기존 생성한 노선의 상행역과 하행역이 모두 동일한 새로운 지하철 구간을 생성하면
     * Then 예외가 발생한다.
     */
    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    void 지하철구간_생성_예외_2() {

    }

    /*
     * When 상행역 또는 하행역이 null인 지하철 구간을 생성하면
     * Then 예외가 발생한다.
     */
    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    void 지하철구간_생성_예외_3() {

    }

    private ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> param = new HashMap<>();
        param.put("upStationId", upStationId);
        param.put("downStationId", downStationId);
        param.put("distance", distance);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
