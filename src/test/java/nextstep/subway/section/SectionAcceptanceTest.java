package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineRestAssured.*;
import static nextstep.subway.station.StationRestAssured.지하철역_등록;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private LineResponse 신분당선;
    private StationResponse 논현역;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 판교역;
    private StationResponse 정자역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        논현역 = 지하철역_등록("논현역").as(StationResponse.class);
        강남역 = 지하철역_등록("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록("양재역").as(StationResponse.class);
        판교역 = 지하철역_등록("판교역").as(StationResponse.class);
        정자역 = 지하철역_등록("정자역").as(StationResponse.class);

        신분당선 = 노선_등록("신분당선", "bg-red-600", 강남역.getId(), 판교역.getId(), 10).as(LineResponse.class);
    }

    /**
     * When 강남역(상행) 판교역(하행)인 신분당선 노선에 논현역(상행) 강남역(하행) 구간을 추가하면
     * Then 지하철_노선에_논현역_등록됨
     */
    @DisplayName("노선에 새로운 역을 상행 종점으로 구간을 등록한다.")
    @Test
    void 상행구간등록_성공() {
        // when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 논현역.getId(), 강남역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("stations", Station.class)
                .stream().map(Station::getName).collect(Collectors.toList());
        assertThat(stationNames).hasSize(3);
        assertThat(stationNames).contains(논현역.getName());
    }

    /**
     * When 강남역(상행) 판교역(하행)인 신분당선 노선에 판교역(상행) 정자역(하행) 구간을 추가하면
     * Then 지하철_노선에_정자역_등록됨
     */
    @DisplayName("노선에 새로운 역을 하행 종점으로 구간을 등록한다.")
    @Test
    void 하행구간등록_성공() {
        // when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 판교역.getId(), 정자역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("stations", Station.class)
                .stream().map(Station::getName).collect(Collectors.toList());
        assertThat(stationNames).hasSize(3);
        assertThat(stationNames).contains(정자역.getName());
    }

    /**
     * When 강남역(상행) 판교역(하행)인 신분당선 노선에 강남역(상행) 양재역(하행) 구간을 추가하면
     * Then 지하철_양재역_등록됨
     */
    @DisplayName("노선에 새로운 역을 중간 구간을 등록한다.")
    @Test
    void 중간구간등록_성공() {
        // when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("stations", Station.class)
                .stream().map(Station::getName).collect(Collectors.toList());
        assertThat(stationNames).hasSize(3);
        assertThat(stationNames).contains(양재역.getName());
    }

    /**
     * When 강남역(상행) 판교역(하행)인 신분당선 노선에 강남역(상행) 판교역(하행) 구간을 추가하면
     * Then 등록실패됨
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void 같은역_중간구간등록_실패() {
        // when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 강남역.getId(), 판교역.getId(), 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 강남역(상행) 판교역(하행)인 신분당선 노선에 강남역(상행) 양재역(하행) 구간 길이를 같은 값을 추가하면
     * Then 등록실패됨
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void 같은구간길이_중간구간등록_실패() {
        // when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 강남역.getId(), 양재역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 강남역(상행) 판교역(하행)인 신분당선 노선에 강남역(상행) 양재역(하행) 구간 길이를 같은 값을 추가하면
     * Then 등록실패됨
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 역이포함되어있지않는_구간등록_실패() {
        // when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 논현역.getId(), 양재역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 강남역(상행) 판교역(하행)인 신분당선 노선에 강남역(상행) 양재역(하행) 구간 길이를 같은 값을 추가하면
     * Then 구간삭제 성공
     */
    @DisplayName("노선에 구간을 제거한다.")
    @Test
    void 지하철역_구간삭제_성공() {
        // given
        노선_구간_추가(신분당선.getId(), 강남역.getId(), 양재역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 노선_구간_삭제(신분당선.getId(), 양재역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
