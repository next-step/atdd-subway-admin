package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceTestMethods.지하철노선_조회;
import static nextstep.subway.line.LineAddSectionAcceptanceTestMethods.지하철_노선에_새로운_구간_추가;
import static nextstep.subway.line.LineAddSectionAcceptanceTestMethods.지하철_역_정렬됨;
import static nextstep.subway.line.LineDeleteSectionAcceptanceTestMethods.지하철_노선에_역_제거;
import static nextstep.subway.line.LineDeleteSectionAcceptanceTestMethods.지하철_노선에_역_제거됨;

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

@DisplayName("지하철 노선 구간 제거 관련 인수테스트")
public class LineDeleteSectionAcceptanceTest extends AcceptanceTest {

    private static final int DISTANCE = 10;
    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    void beforeEach() {
        광교역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("광교역")).as(StationResponse.class);
        강남역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("강남역")).as(StationResponse.class);
        신분당선 = LineAcceptanceTestMethods.지하철노선_생성(
            LineRequest.of("신분당선", "RED", 강남역.getId(), 광교역.getId(), DISTANCE)).as(LineResponse.class);
    }

    /**
     * Given : 노선에 A-B-C 역이 존재한다. 상행 종점은 A, 하행 종점은 C 이다.
     * When : 상행종점 A를 제거한다.
     * When : 지하철 노선의 역을 조회한다.
     * Then : B-C 순서로 역이 조회된다.
     */
    @DisplayName("기존 노선의 종점을 제거하면 다음으로 오던 역이 종점이 된다.")
    @Test
    void deleteStation01() {
        // given
        StationResponse 양재역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("양재역")).as(StationResponse.class);
        SectionRequest 강남역_양재역_구간 = SectionRequest.of(강남역.getId(), 양재역.getId(), 1);
        지하철_노선에_새로운_구간_추가(신분당선.getId(), 강남역_양재역_구간);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_역_제거(신분당선.getId(), 강남역.getId());
        지하철_노선에_역_제거됨(response);

        // when
        ExtractableResponse<Response> findLineResponse = 지하철노선_조회(신분당선.getId());

        // then
        지하철_역_정렬됨(findLineResponse, Arrays.asList(양재역.getId(), 광교역.getId()));
    }

    /**
     * Given : 노선에 A-B-C 역이 존재한다. 상행 종점은 A, 하행 종점은 C이다.
     * When : 중간 역인 B역을 제거한다.
     * When : 지하철 노선의 역을 조회한다.
     * Then : A-C 순서로 역이 조회된다.
     */
    @DisplayName("기존 노선의 중간역을 제거할 경우 역이 재배치된다.")
    @Test
    void deleteStation02() {
        // given
        StationResponse 양재역 = StationAcceptanceTestMethods.지하철역_생성(StationRequest.from("양재역")).as(StationResponse.class);
        SectionRequest 강남역_양재역_구간 = SectionRequest.of(강남역.getId(), 양재역.getId(), 1);
        지하철_노선에_새로운_구간_추가(신분당선.getId(), 강남역_양재역_구간);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_역_제거(신분당선.getId(), 양재역.getId());
        지하철_노선에_역_제거됨(response);

        // when
        ExtractableResponse<Response> findLineResponse = 지하철노선_조회(신분당선.getId());

        // then
        지하철_역_정렬됨(findLineResponse, Arrays.asList(강남역.getId(), 광교역.getId()));
    }
}
