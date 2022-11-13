package nextstep.subway.Acceptance;

import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철구간 관련 기능")
public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    StationResponse 강남역;
    StationResponse 정자역;
    LineResponse 신분당선;


    /**
     * Given 역 두개를 등록한다.
     * Given 강남역을 상행, 광교역을 하행으로 하는 신분당선을 등록한다.
     */
    @BeforeEach
    public void setUpData() {
        강남역 = 지하철역_신규_생성_요청("강남역").as(StationResponse.class);
        정자역 = 지하철역_신규_생성_요청("광교역").as(StationResponse.class);
        신분당선 = 지하철_노선_신규_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 정자역.getId()).as(LineResponse.class);
    }

    /**
     * Given 판교역을 새로 생성하고
     * When 강남역의 하행으로 판교역을 4거리로 추가하면,
     * Then 강남역의 하행 역은 판교역이고 거리는 4이다.
     * Then 정자역의 상행 역은 판교역이고 거리는 6이다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 수 있다.")
    @Test
    void section_add() {
        //given

        //when

        //then

    }

    /**
     * Given 판교역을 새로 생성하고
     * When 강남역의 하행으로 판교역을 10거리로 추가하면,
     * Then 길이 제한으로 등록할 수 없다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 때 기존 역 사이의 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void section_distance() {
        //given

        //when

        //then

    }

    /**
     * Given 신논현역을 새로 생성하고
     * When 강남역의 상행역으로 신논현역을 1거리고 추가하면
     * Then 신분당선의 상행역은 신논현역이 된다.
     * Then 신논현역과 강남역의 거리는 1이다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 수 있다.")
    @Test
    void section_add_as_up_station() {
        //given

        //when

        //then

    }

    /**
     * Given 광교역을 새로 생성하고
     * When 정자역의 하행역으로 광교역을 5 거리로 추가하면
     * Then 신분당의 하행역은 광교역이다.
     * Then 광교역이 상행역은 정자역이다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 수 있다.")
    @Test
    void section_add_as_down_station() {
        //given

        //when

        //then

    }

    /**
     * When 강남역과 정자역 구간을 등록하면
     * Then 이미 모든 역이 등록되어 있어 에러가 발생한다.
     */
    @DisplayName("추가하는 역이 모두 구간에 포함되어 있으면 등록 할 수 없다.")
    @Test
    void section_add_but_already_registered() {
        //given

        //when

        //then

    }

    /**
     * Given 서초역과 교대역을 생성고
     * When 신분당선에 서초역과 교대역을 등록하면
     * Then 두 역이 신분당선에 포함되어 있지 않기 때문에 등록할 수 없다.
     */
    @DisplayName("추가하는 역이 모두 구간에 포함되어 있지 않으면 등록 할 수 없다.")
    @Test
    void section_add_but_none_intersection() {
        //given

        //when

        //then

    }
}
