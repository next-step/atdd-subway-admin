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
                () -> 지하철노선_거리_검증(조회된_신분당선, 10),
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
                () -> 지하철노선_거리_검증(조회된_신분당선, 25),
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

    /**
     *  When 노선에 여러구간을 등록하면
     *  Then 지하철역 목록이 순서대로 반환된다.
     */
    @Test
    @DisplayName("노선을 등록하면 순서대로 반환된다.")
    void addStationBetweenLineExactly() {
        // when
        지하철노선에_지하철역을_등록한다(신분당선ID, 논현역ID, 신논현역ID, SAFE_DISTANCE);
        지하철노선에_지하철역을_등록한다(신분당선ID, 강남역ID, 판교역ID, SAFE_DISTANCE);
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 신사역ID, 논현역ID, SAFE_DISTANCE);

        // then
        ExtractableResponse<Response> 조회된_신분당선 = 지하철노선을_조회한다(추가된_신분당선);

        assertAll(
                () -> 노선_새로운_지하철역_등록_성공_검증(추가된_신분당선),
                () -> 지하철노선_거리_검증(조회된_신분당선, 20),
                () -> 지하철노선_저장된_지하철역_목록_순서_검증(조회된_신분당선, 신사역, 논현역, 신논현역, 강남역, 판교역)
        );
    }

    /**
     * Given 노선 내 3개 이상의 역을 등록하고
     * When 노선 내 상행 종점인 역을 제거하면
     * Then 노선 내에 제거된 역이 존재하지 않고,
     * Then 노선의 상행 종점역이 바뀐다.
     */
    @Test
    @DisplayName("노선 내 상행 종점인 역을 제거하면 해당 역이 제거되고 상행 종점이 바뀐다.")
    void deleteStationByUpStation() {
        // given
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 논현역ID, 신논현역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 삭제_응답 = 지하철노선_내_지하철역을_삭제한다(신분당선ID, 논현역ID);

        // then
        ExtractableResponse<Response> 조회된_신분당선 = 지하철노선을_조회한다(추가된_신분당선);
        assertAll(
                () -> 노선_내_지하철역_삭제_성공_검증(삭제_응답),
                () -> 지하철노선_저장된_지하철역_목록_순서_검증(조회된_신분당선, 신논현역, 강남역),
                () -> 지하철노선_포함되지_않은_지하철역_검증(조회된_신분당선, 논현역),
                () -> 지하철노선_거리_검증(조회된_신분당선, 5)
        );
    }

    /**
     * Given 노선 내 3개 이상의 역을 등록하고
     * When 노선 내 하행 종점인 역을 제거하면
     * Then 노선 내에 제거된 역이 존재하지 않고,
     * Then 노선의 하행 종점역이 바뀐다.
     */
    @Test
    @DisplayName("노선 내 하행 종점인 역을 제거하면 해당 역이 제거되고 하행 종점이 바뀐다.")
    void deleteStationByDownStation() {
        // given
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 논현역ID, 신논현역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 삭제_응답 = 지하철노선_내_지하철역을_삭제한다(신분당선ID, 강남역ID);

        // then
        ExtractableResponse<Response> 조회된_신분당선 = 지하철노선을_조회한다(추가된_신분당선);
        assertAll(
                () -> 노선_내_지하철역_삭제_성공_검증(삭제_응답),
                () -> 지하철노선_저장된_지하철역_목록_순서_검증(조회된_신분당선, 논현역, 신논현역),
                () -> 지하철노선_포함되지_않은_지하철역_검증(조회된_신분당선, 강남역),
                () -> 지하철노선_거리_검증(조회된_신분당선, 5)
        );
    }

    /**
     * Given 노선 내 3개 이상의 역을 등록하고
     * When 노선 내 상행/하행 종점이 아닌 역을 제거하면
     * Then 노선 내에 제거된 역이 존재하지 않는다.
     */
    @Test
    @DisplayName("노선 내 상행/하행 종점이 아닌 역을 제거하면 노선에서 해당역이 제거된다.")
    void deleteStationInSectionByIntermediate() {
        // given
        ExtractableResponse<Response> 추가된_신분당선 = 지하철노선에_지하철역을_등록한다(신분당선ID, 논현역ID, 신논현역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 삭제_응답 = 지하철노선_내_지하철역을_삭제한다(신분당선ID, 신논현역ID);

        // then
        ExtractableResponse<Response> 조회된_신분당선 = 지하철노선을_조회한다(추가된_신분당선);
        assertAll(
                () -> 노선_내_지하철역_삭제_성공_검증(삭제_응답),
                () -> 지하철노선_저장된_지하철역_목록_순서_검증(조회된_신분당선, 논현역, 강남역),
                () -> 지하철노선_포함되지_않은_지하철역_검증(조회된_신분당선, 신논현역),
                () -> 지하철노선_거리_검증(조회된_신분당선, 10)
        );
    }

    /**
     * Given 노선 내 단 한 구간만 등록하고
     * When 노선의 역을 제거하면
     * Then 역이 제거되지 않는다.
     */
    @Test
    @DisplayName("노선 내 구간이 하나만 존재할 경우 역을 제거할 수 없다.")
    void deleteStationInOneSection() {
        // when
        ExtractableResponse<Response> 삭제_응답 = 지하철노선_내_지하철역을_삭제한다(신분당선ID, 논현역ID);

        // then
        노선_내_지하철역_삭제_실패_검증(삭제_응답);
    }

    /**
     * Given 노선 내 역을 2개 이상 등록하고
     * When 노선 내 존재하지 않는 역을 제거하면
     * Then 역이 제거되지 않는다.
     */
    @Test
    @DisplayName("노선 내 존재하지 않는 역은 제거할 수 없다.")
    void deleteStationNotInSection() {
        // given
        지하철노선에_지하철역을_등록한다(신분당선ID, 논현역ID, 신논현역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 삭제_응답 = 지하철노선_내_지하철역을_삭제한다(신분당선ID, 판교역ID);

        // then
        노선_내_지하철역_삭제_실패_검증(삭제_응답);
    }
}
