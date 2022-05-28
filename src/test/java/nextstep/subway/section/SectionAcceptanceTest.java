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

import java.util.stream.Collectors;

import static nextstep.subway.line.LineRestAssured.노선_구간_추가;
import static nextstep.subway.line.LineRestAssured.노선_등록;
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
     * When 강남역(상행) 양재역(하행)인 신분당선 노선에 신논현역(상행) 강남역(하행) 구간을 추가하면
     * Then 지하철_노선에_신논현역_등록됨
     */
    @DisplayName("노선에 새로운 역을 상행 종점으로 구간을 등록한다.")
    @Test
    void addUpStationSection() {
        // when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 논현역.getId(), 강남역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        assertThat(response.jsonPath().getList("stations", Station.class)
                .stream().map(Station::getName).collect(Collectors.toList())).contains(논현역.getName());
    }

    /**
     * When 강남역(상행) 양재역(하행)인 신분당선 노선에 양재역(상행) 양재시민의숲역(하행) 구간을 추가하면
     * Then 지하철_노선에_양재시민의숲역_등록됨
     */
    @DisplayName("노선에 새로운 역을 하행 종점으로 구간을 등록한다.")
    @Test
    void addUpDownSection() {
        // when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 판교역.getId(), 정자역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        assertThat(response.jsonPath().getList("stations", Station.class)
                .stream().map(Station::getName).collect(Collectors.toList())).contains(정자역.getName());
    }
}
