package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.section.dto.SectionAddRequest;
import nextstep.subway.section.dto.SectionAddResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {


    private StationResponse 강남역;
    private StationResponse 광교역;
    private StationResponse 정자역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = StationAcceptanceTest.지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철_역_등록되어_있음("광교역").as(StationResponse.class);
        정자역 = StationAcceptanceTest.지하철_역_등록되어_있음("정자역").as(StationResponse.class);

        LineCreateRequest lineCreateRequest = new LineCreateRequest("신분당선", "bg-red-600",
                강남역.getId(),
                광교역.getId(),
                10);

        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(lineCreateRequest).as(LineResponse.class);
    }

    @DisplayName("지하철 구간을 사이에 등록한다.(상행을 기준으로 새로운 하행을 추가)")
    @Test
    public void addSectionThroughUp() {
        // given
        StationResponse 새로운역 = StationAcceptanceTest.지하철_역_등록되어_있음("새로운역").as(StationResponse.class);

        // when
        SectionAddRequest createRequest = new SectionAddRequest(강남역.getId(), 새로운역.getId(), 1);
        SectionAddResponse response = 지하철_구간_등록_요청(신분당선.getId(), createRequest).as(SectionAddResponse.class);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getDistance()).isEqualTo(9);
        assertThat(response.getUpStationId()).isEqualTo(강남역.getId());
        assertThat(response.getDownStationId()).isEqualTo(새로운역.getId());
    }

    @DisplayName("지하철 구간을 사이에 등록한다.(하행을 기준으로 새로운 상행을 추가)")
    @Test
    public void addSectionToTop() {
        // given
        StationResponse 새로운역 = StationAcceptanceTest.지하철_역_등록되어_있음("새로운역").as(StationResponse.class);

        // when
        SectionAddRequest createRequest = new SectionAddRequest(새로운역.getId(), 광교역.getId(), 1);
        SectionAddResponse response = 지하철_구간_등록_요청(신분당선.getId(), createRequest).as(SectionAddResponse.class);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getDistance()).isEqualTo(9);
        assertThat(response.getUpStationId()).isEqualTo(새로운역.getId());
        assertThat(response.getDownStationId()).isEqualTo(광교역.getId());
    }

    @DisplayName("지하철 등록한다.(상행을 기준으로 새로운 상행종점을 추가)")
    @Test
    public void addSectionToTopEnd() {
        // given
        StationResponse 새로운역 = StationAcceptanceTest.지하철_역_등록되어_있음("새로운역").as(StationResponse.class);

        // when
        SectionAddRequest createRequest = new SectionAddRequest(새로운역.getId(), 강남역.getId(), 100);
        SectionAddResponse response = 지하철_구간_등록_요청(신분당선.getId(), createRequest).as(SectionAddResponse.class);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getDistance()).isEqualTo(100);
        assertThat(response.getUpStationId()).isEqualTo(새로운역.getId());
        assertThat(response.getDownStationId()).isEqualTo(강남역.getId());
    }

    @DisplayName("지하철 등록한다.(하행을 기준으로 새로운 하행종점을 추가)")
    @Test
    public void addSectionToDownEnd() {
        // given
        StationResponse 새로운역 = StationAcceptanceTest.지하철_역_등록되어_있음("새로운역").as(StationResponse.class);

        // when
        SectionAddRequest createRequest = new SectionAddRequest(광교역.getId(), 새로운역.getId(), 100);
        SectionAddResponse response = 지하철_구간_등록_요청(신분당선.getId(), createRequest).as(SectionAddResponse.class);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getDistance()).isEqualTo(100);
        assertThat(response.getUpStationId()).isEqualTo(광교역.getId());
        assertThat(response.getDownStationId()).isEqualTo(새로운역.getId());
    }

    /**
     * 아래의 지하철 노선을 추가하고 오더링을 확인한다
     * 잠원 -> 남부터미널
     * 잠원 -> 남부터미널 -> 양재
     * 잠원 -> 고속터미널 -> 남부터미널 -> 양재
     * 잠원 -> 고속터미널 -> 교대 -> 남부터미널 -> 양재
     * 신사 -> 잠원 -> 고속터미널 -> 교대 -> 남부터미널 -> 양재
     */
    @DisplayName("지하철 추가후 오더링 확인")
    @Test
    public void addMultiSectionsAndVerify() {
        // given
        StationResponse 신사역 = StationAcceptanceTest.지하철_역_등록되어_있음("신사역").as(StationResponse.class);
        StationResponse 잠원역 = StationAcceptanceTest.지하철_역_등록되어_있음("잠원역").as(StationResponse.class);
        StationResponse 고속터미널역 = StationAcceptanceTest.지하철_역_등록되어_있음("고속터미널역").as(StationResponse.class);
        StationResponse 교대역 = StationAcceptanceTest.지하철_역_등록되어_있음("교대역").as(StationResponse.class);
        StationResponse 남부터미널역 = StationAcceptanceTest.지하철_역_등록되어_있음("남부터미널역").as(StationResponse.class);
        StationResponse 양재역 = StationAcceptanceTest.지하철_역_등록되어_있음("양재역").as(StationResponse.class);
        LineCreateRequest lineCreateRequest = new LineCreateRequest("3호선", "orange",
                잠원역.getId(),
                남부터미널역.getId(),
                100);
        LineResponse 삼호선 = LineAcceptanceTest.지하철_노선_등록되어_있음(lineCreateRequest).as(LineResponse.class);

        // when
        지하철_구간_등록_요청(삼호선.getId(), new SectionAddRequest(남부터미널역.getId(), 양재역.getId(), 1)).as(SectionAddResponse.class);
        지하철_구간_등록_요청(삼호선.getId(), new SectionAddRequest(잠원역.getId(), 고속터미널역.getId(), 1)).as(SectionAddResponse.class);
        지하철_구간_등록_요청(삼호선.getId(), new SectionAddRequest(고속터미널역.getId(), 교대역.getId(), 1)).as(SectionAddResponse.class);
        지하철_구간_등록_요청(삼호선.getId(), new SectionAddRequest(신사역.getId(), 잠원역.getId(), 1)).as(SectionAddResponse.class);

        // then
        LineStationResponse lineResponse = LineAcceptanceTest.지하철_노선_조회(삼호선.getId()).as(LineStationResponse.class);
        assertThat(lineResponse.getStations().get(0)).isEqualTo(신사역);
        assertThat(lineResponse.getStations().get(1)).isEqualTo(잠원역);
        assertThat(lineResponse.getStations().get(2)).isEqualTo(고속터미널역);
        assertThat(lineResponse.getStations().get(3)).isEqualTo(교대역);
        assertThat(lineResponse.getStations().get(4)).isEqualTo(남부터미널역);
        assertThat(lineResponse.getStations().get(5)).isEqualTo(양재역);
    }

    @DisplayName("지하철 구간을 등록 할 수 없다.(거리가 잘못됨).")
    @Test
    public void invalidCase1() {
        // given
        StationResponse 새로운역 = StationAcceptanceTest.지하철_역_등록되어_있음("새로운역").as(StationResponse.class);

        // when
        SectionAddRequest createRequest = new SectionAddRequest(새로운역.getId(), 광교역.getId(), 100);
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(신분당선.getId(), createRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    @DisplayName("지하철 구간을 등록 할 수 없다.(전혀 관계 없는역).")
    @Test
    public void invalidCase2() {
        // given
        StationResponse 새로운역1 = StationAcceptanceTest.지하철_역_등록되어_있음("새로운역1").as(StationResponse.class);
        StationResponse 새로운역2 = StationAcceptanceTest.지하철_역_등록되어_있음("새로운역2").as(StationResponse.class);

        // when
        SectionAddRequest createRequest = new SectionAddRequest(새로운역1.getId(), 새로운역2.getId(), 100);
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(신분당선.getId(), createRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 구간을 제거(가운데의 역을 제거)")
    @Test
    public void deleteTest1() {
        강남_광교_정자_순으로_세팅();

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(신분당선.getId(), 광교역.getId());

        // then
        지하철역_삭제됨(response);

        // when
        LineStationResponse lineResponse2 = LineAcceptanceTest.지하철_노선_조회(신분당선.getId()).as(LineStationResponse.class);

        // then
        assertThat(lineResponse2.getStations().get(0)).isEqualTo(강남역);
        assertThat(lineResponse2.getStations().get(1)).isEqualTo(정자역);
    }



    @DisplayName("지하철 구간을 제거(상행 종점을 제거)")
    @Test
    public void deleteTest2() {
        강남_광교_정자_순으로_세팅();

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(신분당선.getId(), 강남역.getId());

        // then
        지하철역_삭제됨(response);

        // when
        LineStationResponse lineResponse2 = LineAcceptanceTest.지하철_노선_조회(신분당선.getId()).as(LineStationResponse.class);

        // then
        assertThat(lineResponse2.getStations().get(0)).isEqualTo(광교역);
        assertThat(lineResponse2.getStations().get(1)).isEqualTo(정자역);

    }

    @DisplayName("지하철 구간을 제거(하행 종점을 제거)")
    @Test
    public void deleteTest3() {
        강남_광교_정자_순으로_세팅();

        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(신분당선.getId(), 정자역.getId());

        // then
        지하철역_삭제됨(response);

        // when
        LineStationResponse lineResponse2 = LineAcceptanceTest.지하철_노선_조회(신분당선.getId()).as(LineStationResponse.class);

        // then
        assertThat(lineResponse2.getStations().get(0)).isEqualTo(강남역);
        assertThat(lineResponse2.getStations().get(1)).isEqualTo(광교역);
    }

    @DisplayName("지하철 구간 삭제 불가 테이스 - 노선에 없는 역")
    @Test
    public void invalidDeleteTest1() {
        강남_광교_정자_순으로_세팅();

        // when
        StationResponse 새로운역1 = StationAcceptanceTest.지하철_역_등록되어_있음("새로운역1").as(StationResponse.class);
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(신분당선.getId(), 새로운역1.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 구간 삭제 불가 케이스 - 노선에 구간이 하나")
    @Test
    public void invalidDeleteTest2() {
        // when
        ExtractableResponse<Response> response = 지하철_구간_삭제_요청(신분당선.getId(), 정자역.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    static ExtractableResponse<Response> 지하철_구간_삭제_요청(Long lineId, Long stationId) {
        Map<String, String> params = new HashMap();
        params.put("stationId", stationId + "");
        return RestAssured.given().log().all()
                .params(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    static void 지하철역_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    static ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, SectionAddRequest sectionCreateRequest) {
        return RestAssured.given().log().all()
                .body(sectionCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private void 강남_광교_정자_순으로_세팅() {
        // given
        지하철_구간_등록_요청(신분당선.getId(), new SectionAddRequest(광교역.getId(), 정자역.getId(), 10));
        LineStationResponse lineResponse = LineAcceptanceTest.지하철_노선_조회(신분당선.getId()).as(LineStationResponse.class);

        // then
        assertThat(lineResponse.getStations().get(0)).isEqualTo(강남역);
        assertThat(lineResponse.getStations().get(1)).isEqualTo(광교역);
        assertThat(lineResponse.getStations().get(2)).isEqualTo(정자역);
    }
}
