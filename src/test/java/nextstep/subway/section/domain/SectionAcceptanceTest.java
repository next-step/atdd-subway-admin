package nextstep.subway.section.domain;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineFindResponse;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static nextstep.subway.station.StationAcceptanceTest.상행역과_하행역_모두_포함되어_있지_않은_지하철역이_등록되어_있음;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 판교역;
    private StationResponse 양재역;
    private LineResponse 신분당선;
    private int distance;

    @BeforeEach
    public void setUpSubwayData() {
        강남역 = 지하철역_등록되어_있음("강남역");
        판교역 = 지하철역_등록되어_있음("판교역");
        양재역 = 지하철역_등록되어_있음("양재역");
        distance = 10;
        신분당선 = 지하철_노선_등록되어_있음("bg-red-600", "신분당선", 강남역, 판교역, distance);
    }

    @DisplayName("역 사이에 노선을 등록한다.(같은 상행역)")
    @Test
    void addSection_역_사이_상행() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(강남역, 양재역, distance - 1);

        // then
        지하철_노선에_지하철역_등록됨(response, 강남역, 양재역, 판교역);
    }

    @DisplayName("역 사이에 노선을 등록한다.(같은 상행역, 길이 초과)")
    @Test
    void addSection_역_사이_상행_실패() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(강남역, 양재역, distance + 1);

        // then
        지하철_노선_등록_실패(response);
    }

    @DisplayName("역 사이에 노선을 등록한다.(같은 하행역)")
    @Test
    void addSection_역_사이_하행() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(양재역, 판교역, distance - 1);

        // then
        지하철_노선에_지하철역_등록됨(response, 강남역, 양재역, 판교역);
    }

    @DisplayName("역 사이에 노선을 등록한다.(같은 하행역, 길이 초과)")
    @Test
    void addSection_역_사이_하행_실패() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(양재역, 판교역, distance + 1);

        // then
        지하철_노선_등록_실패(response);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSection_상행_종점() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(양재역, 강남역, distance + 1);

        // then
        지하철_노선에_지하철역_등록됨(response, 양재역, 강남역, 판교역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSection_하행_종점() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(판교역, 양재역, distance + 1);

        // then
        지하철_노선에_지하철역_등록됨(response, 강남역, 판교역, 양재역);
    }

    @DisplayName("이미 등록된 구간이 있으면 구간 추가에 실패한다.")
    @Test
    void addSection_이미_등록된_구간() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(강남역, 판교역, distance);

        // then
        지하철_노선_등록_실패(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 실패한다.")
    @Test
    void addSection_둘_중_하나도_포함되어_있지_않음() {
        // given
        StationResponse 금정역 = 상행역과_하행역_모두_포함되어_있지_않은_지하철역이_등록되어_있음("금정역");
        StationResponse 사당역 = 상행역과_하행역_모두_포함되어_있지_않은_지하철역이_등록되어_있음("사당역");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(금정역, 사당역, distance);

        // then
        지하철_노선_등록_실패(response);
    }

    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response, StationResponse station1, StationResponse station2, StationResponse station3) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineFindResponse lineFindResponse = response.jsonPath().getObject(".", LineFindResponse.class);
        List<StationResponse> stations = lineFindResponse.getStations();
        List<String> stationNames = stations.stream().map(s -> s.getName()).collect(Collectors.toList());
        assertThat(stationNames).containsExactly(station1.getName(), station2.getName(), station3.getName());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(StationResponse upStation, StationResponse downStation, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(upStation.getId()));
        params.put("downStationId", String.valueOf(downStation.getId()));
        params.put("distance", String.valueOf(distance));

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", 신분당선.getId())
                .then().log().all().extract();
        return response;
    }

    private void 지하철_노선_등록_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
