package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private LineResponse lineNumber8;
    private Long 천호역;
    private Long 문정역;
    private Long 잠실역;

    @BeforeEach
    void init() {
        천호역 = StationAcceptanceTest.지하철역_등록되어_있음("천호역");
        문정역 = StationAcceptanceTest.지하철역_등록되어_있음("문정역");
        잠실역 = StationAcceptanceTest.지하철역_등록되어_있음("잠실역");

        LineRequest lineRequest = LineAcceptanceTest.getLineRequestById("8호선", "pink", 천호역, 문정역, 10);
        lineNumber8 = LineAcceptanceTest.지하철_노선_생성_요청(lineRequest).as(LineResponse.class);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 : 기존 상행역 - 새로운 하행역 관계")
    @Test
    void addSectionSameUpStation() {
        // given
        SectionRequest sectionRequest = getSectionRequest(천호역, 잠실역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("천호역", "잠실역", "문정역"));
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 : 새로운 상행역 - 기존 하행역 관계")
    @Test
    void addSectionSameDownStation() {
        // given
        SectionRequest sectionRequest = getSectionRequest(잠실역, 문정역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("천호역", "잠실역", "문정역"));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionNewUpStation() {
        // given
        StationResponse 암사역 = StationAcceptanceTest.지하철역_생성_요청("암사역").as(StationResponse.class);
        SectionRequest sectionRequest = getSectionRequest(암사역.getId(), 천호역, 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("암사역", "천호역", "문정역"));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionNewDownStation() {
        // given
        StationResponse 모란역 = StationAcceptanceTest.지하철역_생성_요청("모란역").as(StationResponse.class);
        SectionRequest sectionRequest = getSectionRequest(천호역, 모란역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(lineNumber8.getId(), sectionRequest);

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선에_등록한_구간_포함됨(response, Arrays.asList("천호역", "문정역", "모란역"));
    }

    public ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all().extract();
    }

    public SectionRequest getSectionRequest(Long upStationId, Long downStationId, int distance) {
        return SectionRequest.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
    }

    public void 지하철_노선에_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public void 지하철_노선에_등록한_구간_포함됨(ExtractableResponse<Response> response, List<String> expectedStations) {
        List<String> resultStations = response.jsonPath().getList("stations", SectionResponse.class).stream()
                .map(SectionResponse::getName)
                .collect(Collectors.toList());
        assertThat(resultStations).containsAll(expectedStations);
    }
}
