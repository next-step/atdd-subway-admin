package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTestHelper.*;
import static nextstep.subway.section.SectionAcceptanceTestHelper.*;
import static nextstep.subway.station.StationAcceptanceTestHelper.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.domain.Station;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private Station 강남역;
    private Station 양재역;
    private Station 판교역;

    private long 지하철_신분당선_id;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철_역_등록되어_있음("강남역");
        양재역 = 지하철_역_등록되어_있음("양재역");
        판교역 = 지하철_역_등록되어_있음("판교역");

        지하철_신분당선_id = 지하철_노선_등록되어_있음("신분당선", "red", 강남역, 양재역, 10);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void 노선에_구간을_등록한다() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(양재역.getId()));
        params.put("downStationId", String.valueOf(판교역.getId()));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(지하철_신분당선_id, params);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_지하철역_포함됨(지하철_신분당선_id, 판교역);
    }
}
