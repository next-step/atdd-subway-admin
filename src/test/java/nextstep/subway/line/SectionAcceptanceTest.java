package nextstep.subway.line;

import static nextstep.subway.utils.CommonRequest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

public class SectionAcceptanceTest extends AcceptanceTest {

    private Map<String, Long> stationMap = new HashMap<>();

    @BeforeEach
    public void setUp() {
        super.setUp();
        List<StationRequest> stationRequests = new ArrayList<>();

        stationRequests.add(new StationRequest("강남역"));
        stationRequests.add(new StationRequest("양재역"));
        stationRequests.add(new StationRequest("양재시민의 숲"));
        stationRequests.add(new StationRequest("청계산 입구"));

        for (StationRequest stationRequest : stationRequests) {
            ExtractableResponse<Response> response = post(stationRequest, "/stations");
            StationResponse stationResponse = response.jsonPath().getObject(".", StationResponse.class);
            stationMap.put(stationResponse.getName(), stationResponse.getId());
        }
    }

    @DisplayName("노선 구간 등록 : 중간(상행역이 일치하는 경우)")
    @Test
    void addSectionInMiddle() {
        //given
        LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);
        Long createId = response.jsonPath().getObject(".", LineResponse.class).getId();

        SectionRequest sectionRequest = new SectionRequest(stationMap.get("강남역"),  stationMap.get("양재역"), 4);

        // when
        response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("노선 구간 등록 : 중간(하행역이 일치하는 경우)")
    @Test
    void addSectionInMiddle2() {
        //given
        LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);
        Long createId = response.jsonPath().getObject(".", LineResponse.class).getId();

        SectionRequest sectionRequest = new SectionRequest(stationMap.get("양재역"),  stationMap.get("청계산 입구"), 4);

        // when
        response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("노선 구간 등록 : 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionInFirst() {
        //given
        LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", stationMap.get("양재역"), stationMap.get("청계산 입구"), 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);
        Long createId = response.jsonPath().getObject(".", LineResponse.class).getId();

        SectionRequest sectionRequest = new SectionRequest(stationMap.get("강남역"),  stationMap.get("양재역"), 4);

        // when
        response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("노선 구간 등록 : 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionInLast() {
        //given
        LineRequest lineRequest = createLineRequest("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("양재시민의 숲"), 10);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);
        Long createId = response.jsonPath().getObject(".", LineResponse.class).getId();

        SectionRequest sectionRequest = new SectionRequest(stationMap.get("양재시민의 숲"),  stationMap.get("청계산 입구"), 4);

        // when
        response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    private LineRequest createLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }
    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return post(request, "/lines");
    }
    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long id, SectionRequest request) {
        return post(request, "/lines/" + id + "/sections");
    }
    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
