package nextstep.subway.station;

import static nextstep.subway.station.LineAcceptanceTest.지하철_노선을_생성한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.common.RestAssuredTemplate;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("acceptance")
public class SectionAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    public void setUp() {
        super.setUp();

        지하철역을_생성한다("강남역");
        지하철역을_생성한다("잠실역");
        지하철역을_생성한다("삼성역");
        지하철역을_생성한다("사당역");
        지하철역을_생성한다("건대입구역");
        지하철_노선을_생성한다("2호선", "green lighten-3");
    }

    /**
     * When 지하철역 사이에 새로운 역을 등록
     * Then 생성한 지하철 노선의 지하철역 정보를 응답받을 수 있다.
     */
    @DisplayName("강남역과 잠실역 사이에 유효한 강남역-삼성역 구간을 등록한다")
    @Test
    void addMidSection_upStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(1, 3, 5);

        // then
        지하철_노선에_지하철_구간_등록됨(response);
    }

    /**
     * When 지하철역 사이에 기존 역 사이 길이보다 크거나 같은 구간을 등록한다
     * Then 등록에 실패했다는 응답이 반환된다.
     */
    @DisplayName("강남역과 잠실역 사이에 유효하지 않은 강남역-삼성역 구간을 등록한다")
    @Test
    void addMidSection_upStation_error() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(1, 3, 10);

        // then
        지하철_노선에_지하철_구간_등록안됨(response);
    }

    /**
     * When 지하철역 사이에 새로운 역을 등록
     * Then 생성한 지하철 노선의 지하철역 정보를 응답받을 수 있다.
     */
    @DisplayName("강남역과 잠실역 사이에 유효한 삼성역-잠실역 구간을 등록한다")
    @Test
    void addMidSection_downStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(3, 2, 5);

        // then
        지하철_노선에_지하철_구간_등록됨(response);
    }

    /**
     * When 지하철역 사이에 새로운 역을 등록
     * Then 등록에 실패했다는 응답이 반환된다.
     */
    @DisplayName("강남역과 잠실역 사이에 유효하지 않은 삼성역-잠실역 구간을 등록한다")
    @Test
    void addMidSection_downStation_error() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(3, 2, 10);

        // then
        지하철_노선에_지하철_구간_등록안됨(response);
    }

    /**
     * When 새로운 역을 상행 종점으로 등록
     * Then 생성한 지하철 노선의 지하철역 정보를 응답받을 수 있다.
     */
    @DisplayName("강남역과 연결되는 사당역-강남역 구간을 등록한다")
    @Test
    void addSection_upStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(4, 1, 5);

        // then
        지하철_노선에_지하철_구간_등록됨(response);
    }

    /**
     * When 새로운 역을 하행 종점으로 등록
     * Then 생성한 지하철 노선의 지하철역 정보를 응답받을 수 있다.
     */
    @DisplayName("잠실역과 연결되는 잠실역-건대입구역 구간을 등록한다")
    @Test
    void addSection_downStation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(2, 5, 5);

        // then
        지하철_노선에_지하철_구간_등록됨(response);
    }

    /**
     * When 이미 노선에 모두 등록되어 있는 상행역과 하행역을 등록하면
     * Then 등록에 실패했다는 응답이 반환된다.
     */
    @DisplayName("노선에 이미 등록되어 있는 강남역-잠실역 구간을 등록한다")
    @Test
    void addSection_alreadyExistSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(1, 2, 5);

        // then
        지하철_노선에_지하철_구간_등록안됨(response);
    }

    /**
     * When 노선에 등록되지 않은 상행역과 하행역을 등록하면
     * Then 등록에 실패했다는 응답이 반환된다.
     */
    @DisplayName("노선에 등록되어 있지 않은 사당역-건대입구역 구간을 등록한다")
    @Test
    void addSection_nonRegisterSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(4, 5, 5);

        // then
        지하철_노선에_지하철_구간_등록안됨(response);
    }

    public static ExtractableResponse<Response> 지하철_노선에_지하철_구간_등록_요청(long upStationId, long downStationId,
                                                                     long distance) {
        return RestAssuredTemplate.post("/lines/{lineId}/sections", 1,
                new SectionRequest(upStationId, downStationId, distance));
    }

    void 지하철_노선에_지하철_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    void 지하철_노선에_지하철_구간_등록안됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
