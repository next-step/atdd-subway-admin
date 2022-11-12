package nextstep.subway.station;

import static nextstep.subway.station.CreateFactory.지하철노선_등록_요청;
import static nextstep.subway.station.CreateFactory.지하철역_등록_요청;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    private StationResponse 신사역;
    private StationResponse 강남역;
    private StationResponse 양재시민의숲;
    private StationResponse 판교역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        신사역 = 지하철역_등록_요청("신사역").as(StationResponse.class);
        강남역 = 지하철역_등록_요청("강남역").as(StationResponse.class);
        양재시민의숲 = 지하철역_등록_요청("양재시민의숲").as(StationResponse.class);
        판교역 = 지하철역_등록_요청("판교역").as(StationResponse.class);
        광교역 = 지하철역_등록_요청("광교역").as(StationResponse.class);

        신분당선 = 지하철노선_등록_요청("신분당선", "bg-red-600", 강남역.getId(), 판교역.getId(), 10L).as(LineResponse.class);
    }

    /**
     * When 노선 내 역들 사이에 새로운 역을 생성하면
     * Then 노선 중간에 새로운 구간이 생성된다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addStationBetweenLine() {
        // when

        // then
    }

    /**
     * When 노선의 상행 종점을 다음역으로 하는 새로운 역을 생성하면
     * Then 노선에 새로운 구간이 생성된다
     * Then 노선의 상행 종점역이 바뀐다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addStationInFrontOfUpStation() {
        // when

        // then
    }

    /**
     * When 노선의 하행 종점을 시작점으로 하는 새로운 역을 생성하면
     * Then 노선에 새로운 구간이 생성된다
     * Then 노선의 하행 종점역이 바뀐다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addStationAfterDownStation() {
        // when

        // then
    }

    /**
     * When 노선에 기존 역 사이 거리보다 크거나 같은 거리의 역을 등록하면
     * Then 노선에 새로운 구간이 생성이 안된다
     */
    @DisplayName("기존 역 사이 거리보다 크거나 같은 거리의 역을 노선에 등록한다.")
    @ParameterizedTest
    @ValueSource(longs = {10L, 12L, 25L})
    void addSectionWhichHasEqualOrLongerDistance(Long distnace) {
        // when

        // then
    }

    /**
     * When 노선에 기존에 등록된 역들의 구간을 등록하면
     * Then 노선에 새로운 구간이 생성이 안된다
     */
    @DisplayName("노선에 기등록된 역들의 구간을 등록한다.")
    @Test
    void addSectionDuplicateInLine() {
        // when

        // then
    }

    /**
     * When 노선에 등록되지 않은 역들의 구간을 등록하면
     * Then 노선에 새로운 구간이 생성이 안된다
     */
    @DisplayName("노선에 등록되지 않은 역들의 구간을 등록한다.")
    @Test
    void addSectionNotInLine() {

    }
}
