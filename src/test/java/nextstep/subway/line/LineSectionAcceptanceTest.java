package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineAcceptanceTest.*;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private StationResponse 신사역;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 정자역;
    private StationResponse 광교역;
    private StationResponse 수원역;

    @BeforeEach
    public void setUp() {
        super.setUp();
        신사역 = 지하철역_등록되어_있음("신사역").as(StationResponse.class);
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        정자역 = 지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);
        수원역 = 지하철역_등록되어_있음("수원역").as(StationResponse.class);
        신분당선 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10)).as(LineResponse.class);
    }

    @DisplayName("지하철 구간 등록 - 새로운 역이 상행 종점")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철_구간_등록_요청(신분당선, 신사역, 강남역, 4);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        // then
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(신사역, 강남역, 광교역));
    }

    @DisplayName("지하철 구간 등록 - 새로운 역이 하행 종점")
    @Test
    void addLineSection2() {
        // when
        지하철_노선에_지하철_구간_등록_요청(신분당선, 광교역, 수원역, 10);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 광교역, 수원역));
    }

    @DisplayName("지하철 구간 등록 - 역 사이에 새로운 역을 등록")
    @Test
    void addLineSection3() {
        // when
        지하철_노선에_지하철_구간_등록_요청(신분당선, 강남역, 양재역, 3);
        지하철_노선에_지하철_구간_등록_요청(신분당선, 정자역, 광교역, 3);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        // then
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역, 정자역, 광교역));
    }

    @DisplayName("기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addLineSectionException() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(신분당선, 강남역, 양재역, 12);

        // then
        지하철_노선에_지하철역_등록_안됨(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addLineSectionException2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(신분당선, 강남역, 광교역, 10);

        // then
        지하철_노선에_지하철역_등록_안됨(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addLineSectionException3() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_등록_요청(신분당선, 신사역, 정자역, 10);

        // then
        지하철_노선에_지하철역_등록_안됨(response);
    }

    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 됨")
    @Test
    void removeLineSection() {
        //given
        지하철_노선에_지하철_구간_등록_요청(신분당선, 신사역, 강남역, 4);

        //when
        ExtractableResponse<Response> response1 = 지하철_구간_삭제_요청(신분당선, 신사역);

        // then
        지하철_구간_삭제_성공(response1);

        //when
        ExtractableResponse<Response> response2 = 지하철_노선_조회_요청(신분당선);

        // then
        지하철_노선에_지하철역_등록됨(response2);
        지하철_노선에_지하철역_순서_정렬됨(response2, Arrays.asList(강남역, 광교역));
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철_구간_등록_요청(LineResponse lineResponse, StationResponse upStation, StationResponse downStation, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), distance);
        return RestAssured
                .given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", lineResponse.getId())
                .then().log().all().extract();
    }

    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선에_지하철역_등록_안됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선에_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<StationResponse> stationResponses) {
        LineResponse line = response.as(LineResponse.class);
        List<Long> result = line.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationsId = stationResponses.stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(result).isEqualTo(expectedStationsId);
    }

    private ExtractableResponse<Response> 지하철_구간_삭제_요청(LineResponse line, StationResponse station) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", line.getId(), station.getId())
                .then().log().all().extract();
        return response;
    }

    private void 지하철_구간_삭제_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
