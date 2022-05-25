package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_조회;
import static nextstep.subway.line.LineAddSectionAcceptanceTestMethods.지하철_노선에_새로운_구간_추가;
import static nextstep.subway.line.LineAddSectionAcceptanceTestMethods.지하철_노선에_새로운_구간_추가됨;
import static nextstep.subway.line.LineAddSectionAcceptanceTestMethods.지하철_역_정렬됨;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTestMethods;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 구간 추가 관련 인수테스트")
public class LineAddSectionAcceptanceTest extends AcceptanceTest {

    private static final int DISTANCE = 10;
    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    void beforeEach() {
        광교역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("광교역")).as(StationResponse.class);
        강남역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("강남역")).as(StationResponse.class);
        신분당선 = LineAcceptanceTestMethods.지하철노선_생성(LineRequest.of("신분당선", "RED", 강남역.getId(), 광교역.getId(), DISTANCE)).as(LineResponse.class);
    }

    /**
     * Given : 추가할 노선 구간을 생성하고
     * When : 기존 생성된 노선에 구간을 추가하면
     * Then : 노선이 추가된다.
     */
    @DisplayName("기존 노선 중간에 새로운 구간을 추가할 수 있다.")
    @Test
    void addStation() {
        // given
        StationResponse 양재역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("양재역")).as(StationResponse.class);
        SectionRequest 강남역_양재역_구간 = SectionRequest.of(강남역.getId(), 양재역.getId(), 1);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_추가(신분당선.getId(), 강남역_양재역_구간);

        // then
        지하철_노선에_새로운_구간_추가됨(response);
    }

    /**
     * Given : 추가할 노선 구간을 생성하고
     * When : 기존 생성된 노선에 구간을 추가하면
     * Then : 노선에 등록된 역들이 순서대로 조회된다.
     */
    @DisplayName("기존 노선에 새로운 구간을 추가하면 등록된 역 들을 순서대로 조회할 수 있다.")
    @Test
    void showStations() {
        // given
        StationResponse 양재역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("양재역")).as(StationResponse.class);
        SectionRequest 강남역_양재역_구간 = SectionRequest.of(강남역.getId(), 양재역.getId(), 1);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_추가(신분당선.getId(), 강남역_양재역_구간);
        지하철_노선에_새로운_구간_추가됨(response);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철노선_조회(신분당선.getId());
        지하철_역_정렬됨(findLineResponse, Arrays.asList(강남역.getId(), 양재역.getId(), 광교역.getId()));
    }

    /**
     * Given : 새로운 상행 종점을 위해 추가할 노선 구간을 생성하고
     * When : 기존 생성된 노선에 구간을 추가하면
     * Then : 새로운 상행 종점이 추가된다.
     */
    @DisplayName("기존 노선에 새로운 역을 상행 종점으로 추가할 수 있다.")
    @Test
    void addFinalUpStation() {
        // given
        StationResponse 신사역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("신사역")).as(StationResponse.class);
        SectionRequest 신사역_강남역_구간 = SectionRequest.of(신사역.getId(), 강남역.getId(), 1);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_추가(신분당선.getId(), 신사역_강남역_구간);
        지하철_노선에_새로운_구간_추가됨(response);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철노선_조회(신분당선.getId());
        지하철_역_정렬됨(findLineResponse, Arrays.asList(신사역.getId(), 강남역.getId(), 광교역.getId()));
    }

    /**
     * Given : 새로운 하행 종점을 위해 추가할 노선 구간을 생성하고
     * When : 기존 생성된 노선에 구간을 추가하면
     * Then : 새로운 하행 종점이 추가된다.
     */
    @DisplayName("기존 노선에 새로운 역을 하행 종점으로 추가할 수 있다.")
    @Test
    void addFinalDownStation() {
        // given
        StationResponse 부산역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("부산역")).as(StationResponse.class);
        SectionRequest 광교역_부산역_구간 = SectionRequest.of(광교역.getId(), 부산역.getId(), 100);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_추가(신분당선.getId(), 광교역_부산역_구간);
        지하철_노선에_새로운_구간_추가됨(response);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철노선_조회(신분당선.getId());
        지하철_역_정렬됨(findLineResponse, Arrays.asList(강남역.getId(), 광교역.getId(), 부산역.getId()));
    }
}
