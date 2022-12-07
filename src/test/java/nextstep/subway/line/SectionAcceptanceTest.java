package nextstep.subway.line;


import static nextstep.subway.utils.LineAcceptanceTestUtil.지하철노선_생성;
import static nextstep.subway.utils.LineAcceptanceTestUtil.지하철노선을_조회;
import static nextstep.subway.utils.SectionAcceptanceTestUtils.*;
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
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
            () -> 지하철노선_거리_검증(조회된_구호선, 10),
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
     * When 새로운 역 등록시 기존 역 사이 길이 보다 크거나 같은 역을 등록하면
     * Then 등록되지 않는다.
     */
    @ParameterizedTest
    @ValueSource(ints = {BASE_DISTANCE, OVER_DISTANCE})
    @DisplayName("새로운 역 등록시 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    void addStationBySameAndGraterThenDistance(int distance) {
        //when
        ExtractableResponse<Response> 추가된_구호선 = 지하철노선에_지하철역을_등록한다(구호선ID, 동작역ID, 고속터미널역ID, distance);

        // then
        노선_새로운_지하철역_등록_실패_검증(추가된_구호선);
    }

    /**
     * When 이미 등록된 상행역과 하행역을 등록하면
     * Then 등록되지 않는다.
     */
    @Test
    @DisplayName("이미 등록된 상행역과 하행역은 등록할 수 없다.")
    void addStationByAlreadyAddUpAndDownStation() {
        //when
        ExtractableResponse<Response> 추가된_구호선 = 지하철노선에_지하철역을_등록한다(구호선ID, 동작역ID, 언주역ID, SAFE_DISTANCE);

        // then
        노선_새로운_지하철역_등록_실패_검증(추가된_구호선);
    }

    /**
     * When 상행역과 하행역이 포함되지 않은 구간을 등록하면
     * Then 등록되지 않는다.
     */
    @Test
    @DisplayName("상행역과 하행역이 하나라도 포함되어 있지 않으면 구간을 등록할 수 없다.")
    void addStationByDoesNotContainUpStationAndDownStation() {
        //when
        ExtractableResponse<Response> 추가된_구호선 = 지하철노선에_지하철역을_등록한다(구호선ID, 고속터미널역ID, 봉은사역ID, SAFE_DISTANCE);

        // then
        노선_새로운_지하철역_등록_실패_검증(추가된_구호선);
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

    /**
     * Given 노선 내 3개 이상의 역을 등록하고
     * When 노선 내 상행 종점인 역을 제거하면
     * Then 노선 내에 제거된 역이 존재하지 않고,
     * Then 노선의 상행 종점역이 바뀐다.
     */
    @Test
    @DisplayName("노선 내 상행 종점인 역을 제거하면 해당 역이 제거되고 상행 종점이 바뀐다.")
    void deleteStationByUpStation() {

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

    }

    /**
     * Given 노선 내 3개 이상의 역을 등록하고
     * When 노선 내 상행/하행 종점이 아닌 역을 제거하면
     * Then 노선 내에 제거된 역이 존재하지 않는다.
     */
    @Test
    @DisplayName("노선 내 상행/하행 종점이 아닌 역을 제거하면 노선에서 해당역이 제거된다.")
    void deleteStationInSectionByIntermediate() {

    }

    /**
     * Given 노선 내 단 한 구간만 등록하고
     * When 노선의 역을 제거하면
     * Then 역이 제거되지 않는다.
     */
    @Test
    @DisplayName("노선 내 구간이 하나만 존재할 경우 역을 제거할 수 없다.")
    void deleteStationInOneSection() {

    }

    /**
     * Given 노선 내 역을 2개 이상 등록하고
     * When 노선 내 존재하지 않는 역을 제거하면
     * Then 역이 제거되지 않는다.
     */
    @Test
    @DisplayName("노선 내 존재하지 않는 역은 제거할 수 없다.")
    void deleteStationNotInSection() {

    }




}