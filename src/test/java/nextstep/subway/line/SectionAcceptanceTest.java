package nextstep.subway.line;

import static nextstep.subway.utils.LineAcceptanceTestUtil.지하철노선_생성;
import static nextstep.subway.utils.LineAcceptanceTestUtil.지하철노선을_조회;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.BASE_DISTANCE;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.OVER_DISTANCE;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.SAFE_DISTANCE;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.*;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.지하철노선에_지하철역을_등록한다;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.utils.StationAcceptanceTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private LineRequest 구호선_생성;
    private Long 구호선ID;
    private Long 노량진역ID;
    private Long 동작역ID;
    private Long 고속터미널역ID;
    private Long 언주역ID;
    private Long 봉은사역ID;

    @BeforeEach
    void set_up() {
        super.setUp();

        StationResponse 저장된_노량진역 = StationAcceptanceTestUtil.지하철역_생성("노량진역").as(StationResponse.class);
        StationResponse 저장된_동작역 = StationAcceptanceTestUtil.지하철역_생성("동작역").as(StationResponse.class);
        StationResponse 저장된_고속터미널역 = StationAcceptanceTestUtil.지하철역_생성("고속터미널역").as(StationResponse.class);
        StationResponse 저장된_언주역 = StationAcceptanceTestUtil.지하철역_생성("언주역").as(StationResponse.class);
        StationResponse 저장된_봉은사역 = StationAcceptanceTestUtil.지하철역_생성("봉은사역").as(StationResponse.class);


        노량진역ID = 저장된_노량진역.getId();
        동작역ID = 저장된_동작역.getId();
        고속터미널역ID = 저장된_고속터미널역.getId();
        언주역ID = 저장된_언주역.getId();
        봉은사역ID = 저장된_봉은사역.getId();


        구호선_생성 = new LineRequest("구호선", "bg-yellow-600", 동작역ID, 언주역ID, BASE_DISTANCE);
        ExtractableResponse<Response> 저장된_구호선 = 지하철노선_생성(구호선_생성);
        구호선ID = 저장된_구호선.jsonPath().getLong("id");
    }
    /**
     * When 생성된 노선 역들 사이에 새로운 역을 추가하면
     * Then 노선 조회시 3개의 역을 찾을 수 있다.
     */
    @Test
    @DisplayName("역과 역 사이에 새로운 역을 등록한다.")
    void addStationBetweenLine() {
        // when
        ExtractableResponse<Response> 추가된_구호선 = 지하철노선에_지하철역을_등록한다(구호선ID, 동작역ID, 고속터미널역ID, SAFE_DISTANCE);

        // then
        ExtractableResponse<Response> 조회된_구호선 = 지하철노선을_조회(추가된_구호선);

        assertAll(
            () -> 노선_새로운_지하철역_등록_성공_검증(추가된_구호선),
            () -> 지하철노선_거리_검증(조회된_구호선, 15),
            () -> 지하철노선_저장된_지하철역_목록_검증(조회된_구호선, "동작역", "고속터미널역", "언주역")
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
        ExtractableResponse<Response> 추가된_구호선 = 지하철노선에_지하철역을_등록한다(구호선ID, 노량진역ID, 동작역ID, SAFE_DISTANCE);

        // when
        ExtractableResponse<Response> 조회된_구호선 = 지하철노선을_조회(추가된_구호선);

        // then
        assertAll(
            () -> 노선_새로운_지하철역_등록_성공_검증(추가된_구호선),
            () -> 지하철노선_거리_검증(조회된_구호선, 15),
            () -> 지하철노선_저장된_지하철역_목록_검증(조회된_구호선, "노량진역", "동작역", "언주역")
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
        ExtractableResponse<Response> 추가된_구호선 = 지하철노선에_지하철역을_등록한다(구호선ID, 언주역ID, 봉은사역ID, OVER_DISTANCE);

        // when
        ExtractableResponse<Response> 조회된_구호선 = 지하철노선을_조회(추가된_구호선);

        // then
        assertAll(
            () -> 노선_새로운_지하철역_등록_성공_검증(추가된_구호선),
            () -> 지하철노선_거리_검증(조회된_구호선, 25),
            () -> 지하철노선_저장된_지하철역_목록_검증(조회된_구호선, "동작역", "언주역","봉은사역")
        );
    }

    /**
     *  When 노선에 여러구간을 등록하면
     *  Then 지하철역 목록이 순서대로 반환된다.
     */
    @Test
    @DisplayName("노선을 등록하면 순서대로 반환된다.")
    void addStationBetweenLineExactly() {
        // when
        지하철노선에_지하철역을_등록한다(구호선ID, 동작역ID, 고속터미널역ID, SAFE_DISTANCE);
        지하철노선에_지하철역을_등록한다(구호선ID, 언주역ID, 봉은사역ID, SAFE_DISTANCE);
        ExtractableResponse<Response> 추가된_구호선 = 지하철노선에_지하철역을_등록한다(구호선ID, 노량진역ID, 동작역ID, SAFE_DISTANCE);

        // then
        ExtractableResponse<Response> 조회된_구호선 = 지하철노선을_조회(추가된_구호선);

        assertAll(
            () -> 노선_새로운_지하철역_등록_성공_검증(추가된_구호선),
            () -> 지하철노선_거리_검증(조회된_구호선, 20),
            () -> 지하철노선_저장된_지하철역_목록_순서_검증(조회된_구호선, "노량진역", "동작역", "고속터미널역", "언주역", "봉은사역")
        );
    }
}