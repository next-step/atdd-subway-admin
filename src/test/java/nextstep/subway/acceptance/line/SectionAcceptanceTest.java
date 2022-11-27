package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.line.LineSteps.*;
import static nextstep.subway.acceptance.station.StationSteps.*;
import static nextstep.subway.line.LineFixture.*;
import static nextstep.subway.line.SectionTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long stationA;
    private Long stationB;
    private Long stationC;
    private Long stationD;
    private Long stationE;
    private Long lineA;

    @BeforeEach
    public void setUp() {
        super.setUp();
        stationA = 지하철역_생성(A_STATION).jsonPath().getLong("id");
        stationB = 지하철역_생성(B_STATION).jsonPath().getLong("id");
        stationC = 지하철역_생성(C_STATION).jsonPath().getLong("id");
        stationD = 지하철역_생성(D_STATION).jsonPath().getLong("id");
        stationE = 지하철역_생성(E_STATION).jsonPath().getLong("id");
        lineA = 지하철_노선_생성_요청(LINE_NAME_A, LINE_COLOR_B, stationA, stationB, DISTANCE_A_B).jsonPath().getLong("id");
    }

    /**
     * Given C-E 구간을 갖는 노선 B가 생성되어 있다.
     * When C역을 기준으로 구간을 추가한다.
     * Then 노선 B는 C-D-E 구간을 갖는다.
     * Then 노선 B의 거리는 C-D 거리를 갖는다.
     */
    @DisplayName("구간 사이에 구간을 추가한다 / 상행역을 기준으로 구간을 추가한다.")
    @Test
    void createStation() {

        ExtractableResponse<Response> createLineResponse = 지하철_노선_생성_요청(LINE_NAME_B, LINE_COLOR_B, stationC, stationD, DISTANCE_C_D);

        지하철_노선에_지하철_구간_생성_요청(createLineResponse.jsonPath().getLong("id"), addSectionCreateParams(stationD, stationE, DISTANCE_D_E));

        지하철_노선_구간_검증(createLineResponse, DISTANCE_C_E, stationC, stationD, stationE);
    }

    /**
     * given A-B 구간의 노선이 생성되어 있다.
     * given A-B 구간의 거리는 5이다.
     * When 거리가 5인 B-C 구간을 추가를 요청하면
     * Then 구간 추가에 실패한다.
     */
    @DisplayName("구간 사이에 구간을 추가한다 / 상행역을 기준으로 구간을 추가한다. / 새로운 구간의 거리가 기존 구간의 거리보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void addSection_distance() {

        ExtractableResponse<Response> createLineResponse = 지하철_노선_생성_요청(LINE_NAME_B, LINE_COLOR_B, stationA, stationC, DISTANCE_A_C);

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(createLineResponse.jsonPath().getLong("id"), addSectionCreateParams(stationA, stationB, 5));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given A역, C역이 생성되어 있다.
     * Given A-C 구간을 갖는 노선이 생성되어 있다.
     * When 하행역을 기준으로 구간을 추가한다.
     * Then A-B-C 구간이 조회 된다.
     * Then A-C 거리가 조회된다.
     */
    @DisplayName("구간 사이에 구간을 추가한다 / 하행역을 기준으로 구간을 추가한다.")
    @Test
    void addDownStationSection() {

        ExtractableResponse<Response> createLineResponse = 지하철_노선_생성_요청(LINE_NAME_B, LINE_COLOR_B, stationA, stationC, DISTANCE_A_C);

        지하철_노선에_지하철_구간_생성_요청(createLineResponse.jsonPath().getLong("id"), addSectionCreateParams(stationB, stationC, DISTANCE_B_C));

        지하철_노선_구간_검증(createLineResponse, DISTANCE_A_C, stationA, stationB, stationC);
    }

    /**
     * Given A역, C역이 생성되어 있다.
     * Given A-C 구간을 갖는 노선이 생성되어 있다.
     * When 하행역을 기준으로 기존의 구간 사이의 거리와 같은 구간을 추가한다.
     * Then 구간 추가에 실패한다.
     */
    @DisplayName("구간 사이에 구간을 추가한다 / 하행역을 기준으로 구간을 추가한다. / 새로운 구간의 거리가 기존 구간의 거리보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void addDownStationSection_fail_distance() {

        ExtractableResponse<Response> createLineResponse = 지하철_노선_생성_요청(LINE_NAME_B, LINE_COLOR_B, stationA, stationC, DISTANCE_A_C);

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(createLineResponse.jsonPath().getLong("id"), addSectionCreateParams(stationB, stationC, DISTANCE_A_C));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 생성되어 있다.
     * When 기존의 역에 존재하지 않는 구간을 추가한다.
     * Then 구간 추가에 실패한다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    void addSection_fail_none() {

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(lineA, addSectionCreateParams(stationC, stationD, DISTANCE_C_D));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선이 생성되어 있다.
     * When 이미 있는 역을 가지고 있는 구간을 추가한다.
     * Then 구간 추가에 실패한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void duplicate_fail() {

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(lineA, addSectionCreateParams(stationA, stationB, DISTANCE_A_B));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given A역, B역, C역이 생성되어 있다.
     * Given B-C 구간을 가진 A 노선이 생성되어 있다.
     * When A-B 구간을 추가한다.
     * Then A-B-C 구간을 가진 노선이 된다.
     * Then A-C 거리를 갖는다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addUpStationSection_success() {

        ExtractableResponse<Response> createLineResponse = 지하철_노선_생성_요청(LINE_NAME_B, LINE_COLOR_B, stationB, stationC, DISTANCE_B_C);

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(createLineResponse.jsonPath().getLong("id"), addSectionCreateParams(stationA, stationB, DISTANCE_A_B));

        지하철_노선_구간_검증(createLineResponse, DISTANCE_A_C, stationA, stationB, stationC);
    }

    /**
     * Given A역, B역, C역이 생성되어 있다.
     * Given A-B 구간을 가진 A 노선이 생성되어 있다.
     * When B-C 구간을 추가한다.
     * Then A-B-C 구간을 가진 노선이 된다.
     * Then A-C 거리를 갖는다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addDownStation_success() {

        ExtractableResponse<Response> createLineResponse = 지하철_노선_생성_요청(LINE_NAME_B, LINE_COLOR_B, stationA, stationB, DISTANCE_A_B);

        지하철_노선에_지하철_구간_생성_요청(createLineResponse.jsonPath().getLong("id"), addSectionCreateParams(stationB, stationC, DISTANCE_B_C));

        지하철_노선_구간_검증(createLineResponse, DISTANCE_A_C, stationA, stationB, stationC);
    }


    /**
     * Given A-B 구간을 가진 노선 A가 생성되어 있다.
     * When A역 삭제를 요청하면
     * Then 구간 삭제에 실패한다.
     */
    @DisplayName("하나의 구간만 있을 경우 구간을 제거할 수 없다.")
    @Test
    void removeSection_fail_size() {

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(lineA, stationA);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given A역, B역, C역이 생성되어 있다.
     * Given A-B 구간을 가진 노선 A가 생성되어 있다.
     * When C역 삭제를 요청하면
     * Then 구간 삭제에 실패한다.
     */
    @DisplayName("노선에 등록 되어있지 않은 역을 제거할 수 없다.")
    @Test
    void removeNotExistStation_fail() {

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_제거_요청(lineA, stationC);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given A역, B역, C역이 생성되어 있다.
     * Given A-B-C 구간을 가진 노선 A가 생성되어 있다.
     * When A역 삭제를 요청하면
     * Then B-C 구간을 가진 노선이 된다.
     * Then 노선의 거리는 B-C 구간의 거리와 같다.
     */
    @DisplayName("A-B-C 구간의 노선에서 A역을 제거한다.")
    @Test
    void removeStationA_success() {

        //Given
        지하철_노선에_지하철_구간_생성_요청(lineA, addSectionCreateParams(stationB, stationC, DISTANCE_B_C));

        //when
        지하철_노선에_지하철_구간_제거_요청(lineA, stationA);

        // then
        지하철_노선_구간_검증(lineA, DISTANCE_B_C, stationB, stationC);
    }

    /**
     * Given A역, B역, C역이 생성되어 있다.
     * Given A-B-C 구간을 가진 노선 A가 생성되어 있다.
     * When C역 삭제를 요청하면
     * Then A-B 구간을 가진 노선이 된다.
     * Then 노선의 거리는 A-B 구간의 거리와 같다.
     */
    @DisplayName("A-B-C 구간의 노선에서 C역을 제거한다.")
    @Test
    void removeStationC_success() {

        지하철_노선에_지하철_구간_생성_요청(lineA, addSectionCreateParams(stationB, stationC, DISTANCE_B_C));

        지하철_노선에_지하철_구간_제거_요청(lineA, stationC);

        지하철_노선_구간_검증(lineA, DISTANCE_A_B, stationA, stationB);
    }

    /**
     * Given A역, B역, C역이 생성되어 있다.
     * Given A-B-C 구간을 가진 노선 A가 생성되어 있다.
     * When B역 삭제를 요청하면
     * Then A-C 구간을 가진 노선이 된다.
     * Then 노선의 거리는 A-C 구간의 거리와 같다.
     */
    @DisplayName("A-B-C 구간의 노선에서 B역을 제거한다.")
    @Test
    void removeStationB_success() {

        //Given
        지하철_노선에_지하철_구간_생성_요청(lineA, addSectionCreateParams(stationB, stationC, DISTANCE_B_C));

        //when
        지하철_노선에_지하철_구간_제거_요청(lineA, stationB);

        지하철_노선_구간_검증(lineA, DISTANCE_A_C, stationA, stationC);
    }

    private Map<String, String> addSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
