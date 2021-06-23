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
import nextstep.subway.common.dto.ErrorResponse;
import nextstep.subway.exception.CanNotAddSectionException;
import nextstep.subway.exception.CanNotDeleteStateException;
import nextstep.subway.exception.LimitDistanceException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.RegisteredSectionException;
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
        Long createId = 지하철_노선_생성("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10);
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("강남역"),  stationMap.get("양재역"), 4);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        요청_처리_성공(response);
    }

    @DisplayName("노선 구간 등록 : 중간(하행역이 일치하는 경우)")
    @Test
    void addSectionInMiddle2() {
        //given
        Long createId = 지하철_노선_생성("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10);
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("양재역"),  stationMap.get("청계산 입구"), 4);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        요청_처리_성공(response);
    }

    @DisplayName("노선 구간 등록 : 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionInFirst() {
        //given
        Long createId = 지하철_노선_생성("신분당선", "bg-red-600", stationMap.get("양재역"), stationMap.get("청계산 입구"), 10);
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("강남역"),  stationMap.get("양재역"), 4);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        요청_처리_성공(response);
    }

    @DisplayName("노선 구간 등록 : 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionInLast() {
        //given
        Long createId = 지하철_노선_생성("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("양재시민의 숲"), 10);
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("양재시민의 숲"),  stationMap.get("청계산 입구"), 4);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        요청_처리_성공(response);
    }

    @DisplayName("노선 구간 등록 에러 : 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionThrowLimitDistanceException() {
        //given
        Long createId = 지하철_노선_생성("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10);
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("강남역"),  stationMap.get("양재역"), 10);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        요청_처리_실패(response, new LimitDistanceException(Section.MIN_DISTANCE));
    }

    @DisplayName("노선 구간 등록 에러 : 이미 등록된 구간을 등록할 경우")
    @Test
    void addSectionThrowRegisteredSectionException() {
        //given
        Long createId = 지하철_노선_생성("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10);
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("강남역"),  stationMap.get("청계산 입구"), 4);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        요청_처리_실패(response, new RegisteredSectionException());
    }

    @DisplayName("노선 구간 등록 에러 : 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionThrowCanNotAddSectionException() {
        //given
        Long createId = 지하철_노선_생성("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10);
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("양재역"),  stationMap.get("양재시민의 숲"), 4);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(createId, sectionRequest);

        // then
        요청_처리_실패(response, new CanNotAddSectionException());
    }

    @DisplayName("구간 제거 요청 처리")
    @Test
    void deleteSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("강남역"),  stationMap.get("양재역"), 4);
        Long createId = 지하철_노선_생성_그리고_구간_추가("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10, sectionRequest);

        // when
        ExtractableResponse<Response> response = 지하철_구간_제거_요청(createId, stationMap.get("양재역"));

        // then
        요청_처리_성공(response);
    }

    @DisplayName("구간 제거 요청 에러 : 구간에 없는 역 삭제 시")
    @Test
    void deleteSectionNotFoundException() {
        // given
        SectionRequest sectionRequest = new SectionRequest(stationMap.get("강남역"),  stationMap.get("양재역"), 4);
        Long createId = 지하철_노선_생성_그리고_구간_추가("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10, sectionRequest);

        // when
        ExtractableResponse<Response> response = 지하철_구간_제거_요청(createId, stationMap.get("양재시민의 숲"));

        // then
        요청_처리_실패(response, new NotFoundException());
    }

    @DisplayName("구간 제거 요청 에러 : 구간이 하나라서 삭제가 불가능 한 경우")
    @Test
    void deleteSectionCanNotDeleteStateException() {
        // given
        Long createId = 지하철_노선_생성_그리고_구간_추가("신분당선", "bg-red-600", stationMap.get("강남역"), stationMap.get("청계산 입구"), 10);

        // when
        ExtractableResponse<Response> response = 지하철_구간_제거_요청(createId, stationMap.get("강남역"));

        // then
        요청_처리_실패(response, new CanNotDeleteStateException());
    }

    private ExtractableResponse<Response> 지하철_구간_제거_요청(Long createId, Long stationId) {
        Map<String, String> params = new HashMap<>();
        params.put("stationId", String.valueOf(stationId));
        return delete("/lines/" + createId + "/sections", params);
    }

    private LineRequest createLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }

    private Long 지하철_노선_생성(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest lineRequest = createLineRequest(name, color, upStationId, downStationId, distance);
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);
        return response.jsonPath().getObject(".", LineResponse.class).getId();
    }

    private Long 지하철_노선_생성_그리고_구간_추가(String name, String color, Long upStationId, Long downStationId, int distance, SectionRequest ... sectionRequests) {
        Long id = 지하철_노선_생성(name, color, upStationId, downStationId, distance);
        for (SectionRequest sectionRequest : sectionRequests) {
            지하철_노선에_지하철역_등록_요청(id, sectionRequest);
        }
        return id;
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return post(request, "/lines");
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long id, SectionRequest request) {
        return post(request, "/lines/" + id + "/sections");
    }

    private void 요청_처리_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
    
    private void 요청_처리_실패(ExtractableResponse<Response> response, Exception e) {
        ErrorResponse errorResponse = response.jsonPath().getObject(".", ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).contains(e.getMessage());
    }
}
