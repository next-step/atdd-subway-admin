package nextstep.subway.section;

import static nextstep.subway.station.StationAcceptanceTest.createStationWithStationName;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.BaseAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private Long 강남역_id;
    private Long 판교역_id;
    private Long 정자역_id;
    private Long 신분당선_id;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역_id = createStationWithStationName("강남역").jsonPath().getLong("id");
        판교역_id = createStationWithStationName("판교역").jsonPath().getLong("id");
        정자역_id = createStationWithStationName("정자역").jsonPath().getLong("id");

        신분당선_id = createLineWithLineName("신분당선", "bg-red-600", 1L, 3L, 10).jsonPath().getLong("id");
    }

    /**
     * Given 지하철 노선에 지하철역 등록 요청
     * When 지하철 노선에 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @Test
    @DisplayName("구간을 등록한다.")
    void addSection() {
        // given
        Long 양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 강남역_id, 양재역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * When 지하철 노선의 새로운 역을 상행 종점으로 구간을 등록하면
     * Then 새로운 구간이 등록된다.
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionAtFront() {
        // given
        Long 신사역_id = createStationWithStationName("신사역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 신사역_id, 강남역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * When 지하철 노선의 새로운 역을 하행 종점으로 구간을 등록하면
     * Then 새로운 구간이 등록된다.
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionAtEnd() {
        // given
        Long 광교역_id = createStationWithStationName("광교역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 정자역_id, 광교역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선에 지하철역 등록 요청
     * When 지하철 노선의 구간 사이에 구간 길이가 크거나 같은 새로운 구간을 등록하면
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("구간 사이에 새로운 구간을 등록할 때 기존 구간 길이보다 크거나 같으면 등록불가")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void addSectionWithInvalidDistanceException(int input) {
        // given
        Long 양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 강남역_id, 양재역_id, input);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선의 구간 사이에 새로운 구간을 등록하고
     * When 상행역과 하행역이 이미 노선에 모두 등록되어 있는 구간을 등록하면
     * Then 구간 추가에 실패한다.
     */
    @DisplayName("지하철 노선의 구간 등록시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionWithDuplication() {
        // given
        Long 양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");
        createSection(신분당선_id, 강남역_id, 양재역_id, 5);

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 양재역_id, 정자역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 포함되지 않은 역을 생성하고
     * When 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면
     * Then 구간 추가에 실패한다.
     */
    @DisplayName("지하철 노선의 구간 등록시 상행역과 하행역 둘 중 하나도 포함되어 있지 않다면 추가할 수 없음")
    @Test
    void addSectionWithStationNotExist() {
        // given
        Long 논현역_id = createStationWithStationName("논현역").jsonPath().getLong("id");
        Long 신논현역_id = createStationWithStationName("신논현역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 논현역_id, 신논현역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> createLineWithLineName(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = createLineMap(name, color, upStationId, downStationId, distance);

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    private Map<String, String> createLineMap(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        if (upStationId != null) {
            params.put("upStationId", String.valueOf(upStationId));
        }
        if (downStationId != null) {
            params.put("downStationId", String.valueOf(downStationId));
        }
        params.put("distance", String.valueOf(distance));
        return params;
    }

    public static ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post( "/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }
}
