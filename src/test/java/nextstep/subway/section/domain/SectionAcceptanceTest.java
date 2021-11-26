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
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 판교역;
    private StationResponse 양재역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUpSubwayData() {
        강남역 = 지하철역_등록되어_있음("강남역");
        판교역 = 지하철역_등록되어_있음("판교역");
        양재역 = 지하철역_등록되어_있음("양재역");
        신분당선 = 지하철_노선_등록되어_있음("bg-red-600", "신분당선", 강남역, 판교역, "10");
    }

    @DisplayName("역 사이에 노선을 등록한다.(같은 상행역)")
    @Test
    void addSection_역_사이_상행() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(강남역, 양재역, "7");

        // then
        지하철_노선에_지하철역_등록됨(response, 강남역, 양재역, 판교역);
    }

    @DisplayName("역 사이에 노선을 등록한다.(같은 하행역)")
    @Test
    void addSection_역_사이_하행() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(양재역, 강남역, "3");

        // then
        지하철_노선에_지하철역_등록됨(response, 양재역, 강남역, 판교역);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSection_상행_종점() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(양재역, 판교역, "3");

        // then
        지하철_노선에_지하철역_등록됨(response, 강남역, 양재역, 판교역);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSection_하행_종점() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(판교역, 양재역, "3");

        // then
        지하철_노선에_지하철역_등록됨(response, 강남역, 판교역, 양재역);
    }

    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response, StationResponse station1, StationResponse station2, StationResponse station3) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineFindResponse lineFindResponse = response.jsonPath().getObject(".", LineFindResponse.class);
        List<StationResponse> stations = lineFindResponse.getStations();
        List<String> stationNames = stations.stream().map(s -> s.getName()).collect(Collectors.toList());
        assertThat(stationNames).containsExactly(station1.getName(), station2.getName(), station3.getName());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(StationResponse upStation, StationResponse downStation, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(upStation.getId()));
        params.put("downStationId", String.valueOf(downStation.getId()));
        params.put("distance", distance);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", 신분당선.getId())
                .then().log().all().extract();
        return response;
    }
}
