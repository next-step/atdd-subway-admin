package nextstep.subway.line;

import static nextstep.subway.utils.CommonTestFixture.응답_ID_추출;
import static nextstep.subway.utils.LineAcceptanceTestUtils.*;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.*;
import static nextstep.subway.utils.StationAcceptanceTestUtils.지하철역을_생성한다;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선ID;
    private Long 신사역ID;
    private Long 논현역ID;
    private Long 신논현역ID;
    private Long 강남역ID;
    private Long 판교역ID;

    @BeforeEach
    public void setUp() {
        super.setUp();

        신사역ID = 응답_ID_추출(지하철역을_생성한다(신사역));
        논현역ID = 응답_ID_추출(지하철역을_생성한다(논현역));
        신논현역ID = 응답_ID_추출(지하철역을_생성한다(신논현역));
        강남역ID = 응답_ID_추출(지하철역을_생성한다(강남역));
        판교역ID = 응답_ID_추출(지하철역을_생성한다(판교역));

        LineRequest 신분당선_생성_요청 = new LineRequest(신분당선, "bg-red-600", 논현역ID, 강남역ID, BASE_DISTANCE);
        신분당선ID = 응답_ID_추출(지하철노선을_생성한다(신분당선_생성_요청));
    }

    /**
     * When 생성된 노선 역들 사이에 새로운 역을 추가하면
     * Then 노선 조회시 3개의 역을 찾을 수 있다.
     */
    @Test
    @DisplayName("역과 역 사이에 새로운 역을 등록한다.")
    void addStationBetweenLine() {
        // when
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 논현역ID, 신논현역ID, SAFE_DISTANCE);

        // then
        ExtractableResponse<Response> 조회된_신분당선 = 지하철노선을_조회한다(추가된_신분당선);

        assertAll(
                () -> 노선_새로운_지하철역_등록_성공_검증(추가된_신분당선),
                () -> 지하철노선_거리_검증(조회된_신분당선, 15),
                () -> 지하철노선_저장된_지하철역_목록_검증(조회된_신분당선, 논현역, 신논현역, 강남역)
        );
    }

    /**
     * When 새로운 역을 상행 종점으로 등록하면
     * Then 노선 조회시 새로운 역이 상행 종점과 일치한다.
     */
    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    void addStationByUpStation() {
        // given
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 신사역ID, 논현역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 조회된_신분당선 = 지하철노선을_조회한다(추가된_신분당선);

        // then
        assertAll(
                () -> 노선_새로운_지하철역_등록_성공_검증(추가된_신분당선),
                () -> 지하철노선_거리_검증(조회된_신분당선, 15),
                () -> 지하철노선_저장된_지하철역_목록_검증(조회된_신분당선, 신사역, 논현역, 강남역)
        );
    }

    /**
     * When 새로운 역을 하행 종점으로 등록하면
     * Then 노선 조회시 새로운 역이 하행 종점과 일치한다.
     */
    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    void addStationByDownStation() {
        // given
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 강남역ID, 판교역ID, OVER_DISTANCE);

        // when
        ExtractableResponse<Response> 조회된_신분당선 = 지하철노선을_조회한다(추가된_신분당선);

        // then
        assertAll(
                () -> 노선_새로운_지하철역_등록_성공_검증(추가된_신분당선),
                () -> 지하철노선_거리_검증(조회된_신분당선, 15),
                () -> 지하철노선_저장된_지하철역_목록_검증(조회된_신분당선, 논현역, 강남역, 판교역)
        );
    }

    /**
     * When 새로운 역 등록시 기존 역 사이 길이 보다 크거나 같은 역을 등록하면
     * Then 등록되지 않는다.
     */
    @ParameterizedTest
    @ValueSource(ints = {BASE_DISTANCE, OVER_DISTANCE})
    @DisplayName("새로운 역 등록시 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    void addStationBySameAndGraterThenDistance(int distance) {
        //when
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 논현역ID, 신논현역ID, distance);

        // then
        노선_새로운_지하철역_등록_실패_검증(추가된_신분당선);
    }

    /**
     * When 이미 등록된 상행역과 하행역을 등록하면
     * Then 등록되지 않는다.
     */
    @Test
    @DisplayName("이미 등록된 상행역과 하행역은 등록할 수 없다.")
    void addStationByAlreadyAddUpAndDownStation() {
        //when
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 논현역ID, 강남역ID, SAFE_DISTANCE);

        // then
        노선_새로운_지하철역_등록_실패_검증(추가된_신분당선);
    }

    /**
     * When 상행역과 하행역이 포함되지 않은 구간을 등록하면
     * Then 등록되지 않는다.
     */
    @Test
    @DisplayName("상행역과 하행역이 하나라도 포함되어 있지 않으면 구간을 등록할 수 없다.")
    void addStationByDoesNotContainUpStationAndDownStation() {
        //when
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 신논현역ID, 판교역ID, SAFE_DISTANCE);

        // then
        노선_새로운_지하철역_등록_실패_검증(추가된_신분당선);
    }
}
