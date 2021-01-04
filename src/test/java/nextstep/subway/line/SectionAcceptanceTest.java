package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import nextstep.subway.line.dto.SectionCreateResponse;
import nextstep.subway.station.StationRestHelper;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

/**
 * @author : leesangbae
 * @project : subway
 * @since : 2021-01-01
 */
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Station 양재역;
    private Station 판교역;

    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        양재역 = StationRestHelper.지하철역_생성("양재역").as(Station.class);
        판교역 = StationRestHelper.지하철역_생성("판교역").as(Station.class);

        신분당선 = LineRestHelper
                .지하철_라인_생성("bg-red-600", "신분당선", 양재역, 판교역, 100)
                .as(LineResponse.class);
    }

    @DisplayName("지하철 구간 사이 새로운 구간 생성")
    @Test
    void createLineStation01() {
        Station 청계산입구 = StationRestHelper.지하철역_생성("청계산입구").as(Station.class);

        SectionCreateResponse response = SectionRestHelper.지하철_구간_생성(신분당선.getId(), 양재역, 청계산입구, 50)
                .as(SectionCreateResponse.class);

        List<StationResponse> stations = response.getStations();
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(response.getColor()).isEqualTo("bg-red-600");
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).getName()).isEqualTo("양재역");
        assertThat(stations.get(1).getName()).isEqualTo("청계산입구");
        assertThat(stations.get(2).getName()).isEqualTo("판교역");

    }

    @DisplayName("지하철 구간 이전 새로운 구간 생성")
    @Test
    void createLineStation02() {
        Station 강남역 = StationRestHelper.지하철역_생성("강남역").as(Station.class);

        SectionCreateResponse response = SectionRestHelper.지하철_구간_생성(신분당선.getId(), 강남역, 양재역, 50)
                .as(SectionCreateResponse.class);

        List<StationResponse> stations = response.getStations();
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(response.getColor()).isEqualTo("bg-red-600");
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).getName()).isEqualTo("강남역");
        assertThat(stations.get(1).getName()).isEqualTo("양재역");
        assertThat(stations.get(2).getName()).isEqualTo("판교역");

    }

    @DisplayName("지하철 구간 마지막 새로운 구간 생성")
    @Test
    void createLineStation03() {
        Station 광교역 = StationRestHelper.지하철역_생성("광교역").as(Station.class);

        SectionCreateResponse response = SectionRestHelper.지하철_구간_생성(신분당선.getId(), 판교역, 광교역, 50)
                .as(SectionCreateResponse.class);

        List<StationResponse> stations = response.getStations();
        assertThat(response.getName()).isEqualTo("신분당선");
        assertThat(response.getColor()).isEqualTo("bg-red-600");
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).getName()).isEqualTo("양재역");
        assertThat(stations.get(1).getName()).isEqualTo("판교역");
        assertThat(stations.get(2).getName()).isEqualTo("광교역");

    }

    @DisplayName("지하철 구간 생성 - 기존의 길이보다 같거나 큰 값이 들어왔을때")
    @ParameterizedTest(name = "{displayName}[{index}] - \"{arguments}\"")
    @ValueSource(ints = {100, 200})
    void createLineStationBadRequestTest01() {
        Station 광교역 = StationRestHelper.지하철역_생성("광교역").as(Station.class);

        ExtractableResponse<Response> response = SectionRestHelper.지하철_구간_생성(신분당선.getId(), 양재역, 광교역, 100);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 구간 생성 - 등록되지 않은 지하철역")
    @Test
    void createLineStationBadRequestTest02() {
        Station 광교역 = StationRestHelper.지하철역_생성("광교역").as(Station.class);
        Station 정자역 = StationRestHelper.지하철역_생성("정자역").as(Station.class);

        ExtractableResponse<Response> response = SectionRestHelper.지하철_구간_생성(신분당선.getId(), 광교역, 정자역, 100);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 구간 생성 - 이미 등록된 구간 등록")
    @Test
    void createLineStationBadRequestTest03() {
        ExtractableResponse<Response> response = SectionRestHelper.지하철_구간_생성(신분당선.getId(), 양재역, 판교역, 100);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 구간 삭제")
    @Test
    void deleteLineStation01() {
        Station 청계산입구 = StationRestHelper.지하철역_생성("청계산입구").as(Station.class);
        SectionRestHelper.지하철_구간_생성(신분당선.getId(), 양재역, 청계산입구, 50).as(SectionCreateResponse.class);

        ExtractableResponse<Response> deleteResponse = SectionRestHelper.지하철_구간_삭제(신분당선.getId(), 청계산입구.getId());
        LineResponse lineResponse = LineRestHelper.지하철_라인_조회(신분당선.getId()).as(LineResponse.class);

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("양재역");
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("판교역");
    }

    @DisplayName("지하철 마지막 구간 삭제")
    @Test
    void deleteLineStation02() {
        Station 청계산입구 = StationRestHelper.지하철역_생성("청계산입구").as(Station.class);
        SectionRestHelper.지하철_구간_생성(신분당선.getId(), 양재역, 청계산입구, 50).as(SectionCreateResponse.class);

        ExtractableResponse<Response> deleteResponse = SectionRestHelper.지하철_구간_삭제(신분당선.getId(), 판교역.getId());

        LineResponse lineResponse = LineRestHelper.지하철_라인_조회(신분당선.getId()).as(LineResponse.class);

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("양재역");
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("청계산입구");
    }

    @DisplayName("지하철 첫번째 구간 삭제")
    @Test
    void deleteLineStation03() {
        Station 청계산입구 = StationRestHelper.지하철역_생성("청계산입구").as(Station.class);
        SectionRestHelper.지하철_구간_생성(신분당선.getId(), 양재역, 청계산입구, 50).as(SectionCreateResponse.class);

        ExtractableResponse<Response> deleteResponse = SectionRestHelper.지하철_구간_삭제(신분당선.getId(), 양재역.getId());

        LineResponse lineResponse = LineRestHelper.지하철_라인_조회(신분당선.getId()).as(LineResponse.class);

        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("청계산입구");
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("판교역");
    }

    @DisplayName("지하철 첫번째 잘못된 요청")
    @Test
    void deleteLineStationBadRequestTest() {

        ExtractableResponse<Response> response = SectionRestHelper.지하철_구간_삭제(신분당선.getId(), null);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 삭제 가능한 최소 사이즈 상황에서 삭제")
    @Test
    void deleteLineStationLessThenMinSize() {

        ExtractableResponse<Response> response = SectionRestHelper.지하철_구간_삭제(신분당선.getId(), null);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
