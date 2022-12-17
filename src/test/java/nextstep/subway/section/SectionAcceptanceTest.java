package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private Map<String, String> lineCreateParams;
    private StationResponse 선릉역;
    private StationResponse 도곡역;
    private LineResponse 분당선;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        선릉역 = StationAcceptanceTest.지하철역_생성_요청("선릉역").as(StationResponse.class);
        도곡역 = StationAcceptanceTest.지하철역_생성_요청("도곡역").as(StationResponse.class);

        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", 선릉역.getId() + "");
        lineCreateParams.put("downStationId", 도곡역.getId() + "");
        lineCreateParams.put("distance", 10 + "");
        분당선 = LineAcceptanceTest.지하철_노선_생성_요청(lineCreateParams).as(LineResponse.class);
    }

    /**
     * When 지하철 구간을 생성하면
     * Then 지하철 노선에 등록할 수 있다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSection() {
        // when
        StationResponse 한티역 = StationAcceptanceTest.지하철역_생성_요청("한티역").as(StationResponse.class);
        ExtractableResponse<Response> response = 구간_추가_요청(분당선.getId(), new SectionRequest(선릉역.getId(), 한티역.getId(), 3));

        // then
        구간_생성_확인_정상(response);
    }

    /**
     * When 지하철 구간을 상행 종점 구간으로 생성하면
     * Then 지하철 노선에 등록할 수 있다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionUpStation() {
        // when
        StationResponse 왕십리역 = StationAcceptanceTest.지하철역_생성_요청("왕십리역").as(StationResponse.class);
        ExtractableResponse<Response> response = 구간_추가_요청(분당선.getId(), new SectionRequest(왕십리역.getId(), 선릉역.getId(), 3));

        // then
        구간_생성_확인_정상(response);
    }

    /**
     * When 지하철 구간을 하행 종점 구간으로 생성하면
     * Then 지하철 노선에 등록할 수 있다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionDownStation() {
        // when
        StationResponse 수원역 = StationAcceptanceTest.지하철역_생성_요청("수원역").as(StationResponse.class);
        ExtractableResponse<Response> response = 구간_추가_요청(분당선.getId(), new SectionRequest(도곡역.getId(), 수원역.getId(), 3));

        // then
        구간_생성_확인_정상(response);
    }

    /**
     * When 기존 구간보다 크거나 같은 지하철 구간을 생성하면
     * Then 지하철 노선에 등록할 수 없다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addSectionInvalidSizeException() {
        // when
        StationResponse 한티역 = StationAcceptanceTest.지하철역_생성_요청("한티역").as(StationResponse.class);
        ExtractableResponse<Response> response1 = 구간_추가_요청(분당선.getId(), new SectionRequest(선릉역.getId(), 한티역.getId(), 10));
        ExtractableResponse<Response> response2 = 구간_추가_요청(분당선.getId(), new SectionRequest(선릉역.getId(), 한티역.getId(), 12));

        // then
        구간_생성_확인_실패(response1);
        구간_생성_확인_실패(response2);
    }

    /**
     * When 구간에 해당하는 역이 모두 등록되어 있다면
     * Then 지하철 노선에 등록할 수 없다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionDuplicateException() {
        // when
        StationResponse 선릉역 = StationAcceptanceTest.지하철역_생성_요청("선릉역").as(StationResponse.class);
        StationResponse 도곡역 = StationAcceptanceTest.지하철역_생성_요청("도곡역").as(StationResponse.class);
        ExtractableResponse<Response> response = 구간_추가_요청(분당선.getId(), new SectionRequest(선릉역.getId(), 도곡역.getId(), 10));

        // then
        구간_생성_확인_실패(response);
    }

    /**
     * When 구간에 해당하는 역이 상행역과 하행역 둘 중 하나도 포함되어있지 않으면
     * Then 지하철 노선에 등록할 수 없다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSectionNoConnectedSectionException() {
        // when
        StationResponse 가능역 = StationAcceptanceTest.지하철역_생성_요청("가능역").as(StationResponse.class);
        StationResponse 의정부역 = StationAcceptanceTest.지하철역_생성_요청("의정부역").as(StationResponse.class);

        // then
        ExtractableResponse<Response> response = 구간_추가_요청(분당선.getId(), new SectionRequest(가능역.getId(), 의정부역.getId(), 10));

        // then
        구간_생성_확인_실패(response);
    }

    /**
     * Given 3개의 역이 있는 상황에서(두 구간)
     * When 종점을 제거하면
     * Then 그 구간은 없어진다.
     */
    @DisplayName("종점을 제거하는 경우")
    @Test
    void deleteSectionEndStation() {
        // given (선릉-한티-도곡)
        StationResponse 한티역 = StationAcceptanceTest.지하철역_생성_요청("한티역").as(StationResponse.class);
        구간_추가_요청(분당선.getId(), new SectionRequest(선릉역.getId(), 한티역.getId(), 3));

        // when (도곡역 제거)
        ExtractableResponse<Response> response = 지하철역_제거_요청(분당선.getId(), 도곡역.getId());

        // then
        지하철역_제거_확인_정상(response);
        ExtractableResponse<Response> retrievedResponse = LineAcceptanceTest.지하철_노선_조회_요청(분당선);
        지하철역_제거_확인_리스트(retrievedResponse, Arrays.asList(선릉역, 한티역));
    }

    /**
     * When 구간이 하나인 노선에서 마지막 구간을 제거할 때
     * Then 제거할 수 없다는 예외 발생
     */
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 때 예외")
    @Test
    void deleteLastSectionException() {
        // when
        ExtractableResponse<Response> response = 지하철역_제거_요청(분당선.getId(), 도곡역.getId());

        // then
        지하철역_제거_확인_실패(response);
    }

    @Test
    void delete() {

    }

    private void 구간_생성_확인_실패(ExtractableResponse<Response> response1) {
        assertThat(response1.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }

    private void 구간_생성_확인_정상(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();
    }

    private void 지하철역_제거_확인_정상(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철역_제거_확인_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
    }

    private void 지하철역_제거_확인_리스트(ExtractableResponse<Response> retrievedResponse, List<StationResponse> expectedStations) {
        LineResponse line = retrievedResponse.as(LineResponse.class);
        List<Long> stationIds = line.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsAll(expectedStationIds);
    }

    private ExtractableResponse<Response> 구간_추가_요청(Long lineId, SectionRequest sectionRequest) {
        return RestAssured
                .given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철역_제거_요청(Long lineId, Long stationId) {
        return RestAssured
                .given().log().all()
                .param("stationId", stationId)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
