package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceMethods.*;
import static nextstep.subway.section.SectionAcceptanceMethods.*;
import static nextstep.subway.station.StationAcceptanceMethods.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static final int DISTANCE = 10;

    private StationResponse pangyoStation;
    private StationResponse migeumStation;
    private LineResponse shinbundangLine;

    @BeforeEach
    public void setUp() {
        super.setUp();

        pangyoStation = 지하철_역_등록되어_있음(StationRequest.from("판교역")).as(StationResponse.class);
        migeumStation = 지하철_역_등록되어_있음(StationRequest.from("미금역")).as(StationResponse.class);

        LineRequest lineRequest = LineRequest.from("신분당선",
                                                   "RED",
                                                   pangyoStation.getId(),
                                                   migeumStation.getId(),
                                                   DISTANCE);
        shinbundangLine = 지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);
    }

    @DisplayName("노선 중간에 새로운 역을 등록한다.")
    @Test
    void addSection1() {
        // given
        StationResponse jeongjaStation = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        SectionRequest sectionRequest = SectionRequest.of(pangyoStation.getId(), jeongjaStation.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(shinbundangLine.getId(), sectionRequest);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철_노선_조회_요청(shinbundangLine.getId());

        지하철_노선에_지하철역_등록됨(response);
        지하철_역_정렬됨(findLineResponse, Arrays.asList(pangyoStation.getId(), jeongjaStation.getId(), migeumStation.getId()));
    }

    @DisplayName("노선에 새로운 상행 종점역을 등록한다.")
    @Test
    void addSection2() {
        // given
        StationResponse jeongjaStation = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        SectionRequest sectionRequest = SectionRequest.of(jeongjaStation.getId(), pangyoStation.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(shinbundangLine.getId(), sectionRequest);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철_노선_조회_요청(shinbundangLine.getId());

        지하철_노선에_지하철역_등록됨(response);
        지하철_역_정렬됨(findLineResponse, Arrays.asList(jeongjaStation.getId(), pangyoStation.getId(), migeumStation.getId()));
    }

    @DisplayName("노선에 새로운 하행 종점역을 등록한다.")
    @Test
    void addSection3() {
        // given
        StationResponse jeongjaStation = 지하철_역_생성_요청(StationRequest.from("정자역")).as(StationResponse.class);
        SectionRequest sectionRequest = SectionRequest.of(migeumStation.getId(), jeongjaStation.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(shinbundangLine.getId(), sectionRequest);

        // then
        ExtractableResponse<Response> findLineResponse = 지하철_노선_조회_요청(shinbundangLine.getId());

        지하철_노선에_지하철역_등록됨(response);
        지하철_역_정렬됨(findLineResponse, Arrays.asList(pangyoStation.getId(), migeumStation.getId(), jeongjaStation.getId()));
    }
}
