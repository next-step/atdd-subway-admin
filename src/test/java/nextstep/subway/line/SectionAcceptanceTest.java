package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static nextstep.subway.line.LineScenarioMethod.지하철_노선_등록되어_있음;
import static nextstep.subway.line.LineScenarioMethod.지하철_노선_정보;
import static nextstep.subway.line.SectionScenarioMethod.*;
import static nextstep.subway.line.domain.Distance.MIN_DISTANCE;
import static nextstep.subway.station.StationScenarioMethod.등록되지_않은_지하철_역;
import static nextstep.subway.station.StationScenarioMethod.지하철_역_여러개_등록되어_있음;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@DisplayName("지하철 구간 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    private StationRequest 강남 = new StationRequest("강남");
    private StationRequest 양재 = new StationRequest("양재");
    private StationRequest 양재시민의숲 = new StationRequest("양재시민의숲");
    private StationRequest 청계산입구 = new StationRequest("청계산입구");

    @DisplayName("지하철 구간의 종점으로 지하철 구간을 생성한다.")
    @Test
    void createSectionTerminus() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재, 양재시민의숲, 청계산입구);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("양재"), stations.get("양재시민의숲"), 5);
        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request1 = 지하철_구간_정보(stations, "강남", "양재", 5);
        SectionRequest request2 = 지하철_구간_정보(stations, "양재시민의숲", "청계산입구", 5);

        // when
        ExtractableResponse<Response> response1 = 지하철_구간_생성_요청(createdLocationUri, request1);
        ExtractableResponse<Response> response2 = 지하철_구간_생성_요청(createdLocationUri, request2);

        // then
        지하철_구간_생성됨(response1);
        지하철_구간_생성됨(response2);
        지하철_노선에_등록한_구간_포함됨(createdLocationUri, Arrays.asList("강남", "양재", "양재시민의숲", "청계산입구"));
    }

    @DisplayName("지하철 역 사이에 지하철 구간을 생성한다.")
    @Test
    void createSectionBetweenStations() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재, 양재시민의숲, 청계산입구);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("청계산입구"), 15);
        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request1 = 지하철_구간_정보(stations, "강남", "양재", 5);
        SectionRequest request2 = 지하철_구간_정보(stations, "양재시민의숲", "청계산입구", 5);

        // when
        ExtractableResponse<Response> response1 = 지하철_구간_생성_요청(createdLocationUri, request1);
        ExtractableResponse<Response> response2 = 지하철_구간_생성_요청(createdLocationUri, request2);

        // then
        지하철_구간_생성됨(response1);
        지하철_구간_생성됨(response2);
        지하철_노선에_등록한_구간_포함됨(createdLocationUri, Arrays.asList("강남", "양재", "양재시민의숲", "청계산입구"));
    }

    @DisplayName("지하철 역이 등록되어 있지 않은 경우 구간 생성에 실패한다.")
    @Test
    void createSectionWithoutTerminus() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("청계산입구"), 5);
        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);

        Map<String, Long> emptyStations = 등록되지_않은_지하철_역();
        SectionRequest request = 지하철_구간_정보(emptyStations, "상행", "하행", 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(createdLocationUri, request);

        // then
        지하철_구간_생성_실패됨(response, NOT_FOUND.value());
    }

    @DisplayName("같은 지하철 구간이 이미 등록되어 있는 경우 구간 생성에 실패한다.")
    @Test
    void createSectionWithSameSection() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("양재"), 5);
        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request = 지하철_구간_정보(stations, "강남", "양재", 5);

        // when
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(createdLocationUri, request);

        // then
        지하철_구간_생성_실패됨(response, BAD_REQUEST.value());
    }

    @DisplayName("지하철 구간의 거리가 최소 거리 이하인 경우 구간 생성에 실패한다.")
    @Test
    void createSectionShorterThenMinDistance() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("양재"), 10);
        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request = 지하철_구간_정보(stations, "강남", "양재시민의숲", MIN_DISTANCE);

        // when
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(createdLocationUri, request);

        // then
        지하철_구간_생성_실패됨(response, BAD_REQUEST.value());
    }

    @DisplayName("기존 역 사이의 길이보다 크거나 같은 경우 구간 생성에 실패한다.")
    @Test
    void createSectionLongerDistanceBetweenStations() {
        // given
        int distance = 5;
        TreeMap<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재, 양재시민의숲);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("양재시민의숲"), distance);
        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request = 지하철_구간_정보(stations, "강남", "양재", distance);

        // when
        ExtractableResponse<Response> response = 지하철_구간_생성_요청(createdLocationUri, request);

        // then
        지하철_구간_생성_실패됨(response, BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철 노선의 구간을 조회한다.")
    void findSections() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재, 양재시민의숲, 청계산입구);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("청계산입구"), 15);
        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request1 = 지하철_구간_정보(stations, "강남", "양재", 5);
        SectionRequest request2 = 지하철_구간_정보(stations, "양재시민의숲", "청계산입구", 5);

        지하철_구간_생성됨(지하철_구간_생성_요청(createdLocationUri, request1));
        지하철_구간_생성됨(지하철_구간_생성_요청(createdLocationUri, request2));

        // when
        ExtractableResponse<Response> response = 지하철_구간_조회_요청(createdLocationUri);

        // then
        지하철_구간_개수_일치됨(response, 3);
        지하철_구간_상행역_일치됨(response, Arrays.asList("강남", "양재", "양재시민의숲"));
    }

    @Test
    @DisplayName("지하철 노선의 상행 종점 구간을 삭제한다.")
    void deleteSectionUpTerminus() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재, 양재시민의숲);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("양재"), 5);
        String createdLineUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request = 지하철_구간_정보(stations, "양재", "양재시민의숲", 5);

        지하철_구간_생성됨(지하철_구간_생성_요청(createdLineUri, request));
        Map<String, Long> downStations = 등록된_구간_지하철역(createdLineUri);

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(createdLineUri, downStations.get("강남"));

        // then
        지하철_구간_삭제됨(response);
        지하철_구간_일치됨(createdLineUri, "양재", "양재시민의숲", 5);
    }

    @Test
    @DisplayName("지하철 노선의 하행 종점 구간을 삭제한다.")
    void deleteSectionDownTerminus() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재, 양재시민의숲);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("양재"), 5);
        String createdLineUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request1 = 지하철_구간_정보(stations, "양재", "양재시민의숲", 5);

        지하철_구간_생성됨(지하철_구간_생성_요청(createdLineUri, request1));
        Map<String, Long> downStations = 등록된_구간_지하철역(createdLineUri);

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(createdLineUri, downStations.get("양재시민의숲"));

        // then
        지하철_구간_삭제됨(response);
        지하철_구간_일치됨(createdLineUri, "강남", "양재", 5);
    }

    @Test
    @DisplayName("지하철 노선 사이의 구간을 삭제한다.")
    void deleteSectionBetweenStations() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재, 양재시민의숲, 청계산입구);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("양재"), 5);
        String createdLineUri = 지하철_노선_등록되어_있음(신분당선);

        SectionRequest request1 = 지하철_구간_정보(stations, "양재", "양재시민의숲", 5);
        SectionRequest request2 = 지하철_구간_정보(stations, "양재시민의숲", "청계산입구", 5);

        지하철_구간_생성됨(지하철_구간_생성_요청(createdLineUri, request1));
        지하철_구간_생성됨(지하철_구간_생성_요청(createdLineUri, request2));

        Map<String, Long> downStations = 등록된_구간_지하철역(createdLineUri);

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(createdLineUri, downStations.get("양재"));

        // then
        지하철_구간_삭제됨(response);
    }

    @Test
    @DisplayName("지하철 구간이 등록되지 않은 경우 구간 삭제가 실패한다.")
    void deleteSectionValidateEmptyResult() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("양재"), 5);
        String createdLineUri = 지하철_노선_등록되어_있음(신분당선);
        Long emptyId = 100L;

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(createdLineUri, emptyId);

        // then
        지하철_구간_삭제_실패됨(response, NOT_FOUND.value());
    }

    @Test
    @DisplayName("지하철 구간이 1개인 경우 구간 삭제가 실패한다.")
    void deleteSectionValidateSize() {
        // given
        Map<String, Long> stations = 지하철_역_여러개_등록되어_있음(강남, 양재);
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("양재"), 5);
        String createdLineUri = 지하철_노선_등록되어_있음(신분당선);

        Map<String, Long> downStations = 등록된_구간_지하철역(createdLineUri);

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(createdLineUri, downStations.get("강남"));

        // then
        지하철_구간_삭제_실패됨(response, BAD_REQUEST.value());
    }
}