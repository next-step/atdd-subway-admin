package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_조회;
import static nextstep.subway.line.LineAddSectionAcceptanceTestMethods.지하철_노선에_새로운_구간_추가;
import static nextstep.subway.line.LineAddSectionAcceptanceTestMethods.지하철_노선에_새로운_구간_추가됨;
import static nextstep.subway.line.LineAddSectionAcceptanceTestMethods.지하철_노선에_추가되지_않음;
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
     * Then : 기존 노선에 새로운 구간이 추가된다
     * When : 노선에 등록된 역을 조회하면
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
        // then
        지하철_노선에_새로운_구간_추가됨(response);

        // when
        ExtractableResponse<Response> findLineResponse = 지하철노선_조회(신분당선.getId());
        // thens
        지하철_역_정렬됨(findLineResponse, Arrays.asList(강남역.getId(), 양재역.getId(), 광교역.getId()));
    }

    /**
     * Given : 새로운 상행 종점을 위해 추가할 노선 구간을 생성하고
     * When : 기존 생성된 노선에 구간을 추가하면
     * Then : 기존 노선에 새로운 구간이 추가된다
     * When : 노선에 등록된 역을 조회하면
     * Then : 상행역이 추가된 결과로 지하철 역이 순서대로 조회된다.
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
     * Then : 기존 노선에 새로운 구간이 추가된다
     * When : 노선에 등록된 역을 조회하면
     * Then : 하행역이 추가된 결과로 지하철 역이 순서대로 조회된다.
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

    /**
     * Given : 추가할 노선 구간을 생성하고 (기존 노선 거리보다 짧게)
     * When : 기존 생성된 노선에 구간을 추가하면
     * Then : 시도한 구간이 등록되지 않는다.
     */
    @DisplayName("기존 노선에 새로운 구간을 추가 시 기존 역의 거리보다 크거나 같으면 등록할 수 없다.")
    @Test
    void addSectionException01() {
        // given
        StationResponse 양재역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("양재역")).as(StationResponse.class);
        SectionRequest 강남역_양재역_구간 = SectionRequest.of(강남역.getId(), 양재역.getId(), 100);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_추가(신분당선.getId(), 강남역_양재역_구간);

        // then
        지하철_노선에_추가되지_않음(response);
    }

    /**
     * Given : 기존에 존재하는 역으로 구성된 추가 노선을 생성하고
     *  기존 : 강남역 - 광교역 (2개만 존재)
     * When : 기존 생성된 노선에 구간을 추가하면
     *  추가하려는 구간 : 강남역 - 광교역
     * Then : 추가를 시도한 구간이 등록되지 않는다.
     */
    @DisplayName("기존 노선에 존재하는 역으로 구성된 새로운 구간 추가 시 구간 등록이 되지 않는다.")
    @Test
    void addDuplicateStations01() {
        // given & when
        SectionRequest 강남역_광교역_구간 = SectionRequest.of(강남역.getId(), 광교역.getId(), 1);
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_추가(신분당선.getId(), 강남역_광교역_구간);

        // then
        지하철_노선에_추가되지_않음(response);
    }

    /**
     * Given : 기존에 존재하는 역으로 구성된 추가 노선을 생성하고
     *  기존 : 강남역 - 양재역 - 광교역 (3개 역 존재)
     * When : 기존 생성된 노선에 구간을 추가하면
     *  추가하려는 구간 : 양재역 - 광교역
     * Then : 추가를 시도한 구간이 등록되지 않는다.
     */
    @DisplayName("기존 노선에 존재하는 역으로 구성된 새로운 구간 추가 시 구간 등록이 되지 않는다.")
    @Test
    void addDuplicateStations02() {
        // given
        StationResponse 양재역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("양재역")).as(StationResponse.class);
        SectionRequest 강남역_양재역_구간 = SectionRequest.of(강남역.getId(), 양재역.getId(), 1);
        지하철_노선에_새로운_구간_추가(신분당선.getId(), 강남역_양재역_구간);

        // when
        SectionRequest 양재역_광교역_구간 = SectionRequest.of(양재역.getId(), 광교역.getId(), 1);
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_추가(신분당선.getId(), 양재역_광교역_구간);

        // then
        지하철_노선에_추가되지_않음(response);
    }

    /**
     * Given : 기존 노선에 없는 역들로 구성된 구간을 생성한다.
     * When : 기존 노선에 생성한 구간을 추가한다.
     * Then : 추가를 시도한 구간이 등록되지 않는다.
     */
    @DisplayName("기존 노선에 존재하지 않는 상/하행 역 구간을 등록하는 경우 등록되지 않는다.")
    @Test
    void addNotExistStations01() {
        // given
        StationResponse 양재역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("양재역")).as(StationResponse.class);
        StationResponse 양재시민의숲역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("양재시민의숲역")).as(StationResponse.class);
        SectionRequest 양재역_양재시민의숲역_구간 = SectionRequest.of(양재역.getId(), 양재시민의숲역.getId(), 1);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_새로운_구간_추가(신분당선.getId(), 양재역_양재시민의숲역_구간);

        // then
        지하철_노선에_추가되지_않음(response);
    }
}
