package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.getLineResponse;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private long 신분당선;
    private long 강남역;
    private long 양재역;
    private long 판교역;
    private long 양재시민의숲;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        // 지하철_역_생성_요청
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역");
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역");
        판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역");
        양재시민의숲 = StationAcceptanceTest.지하철역_등록되어_있음("양재시민의숲");

        // 지하철_노선_생성_요청
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 7);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 판교역, 5);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선에 구간을 등록하고 순서가 맞는지 확인한다.")
    @Test
    void addSectionThanEqualsOrder() {
        // when
        // 지하철_노선에_구간_등록_요청
        지하철_노선에_구간_등록_요청(신분당선, 강남역, 판교역, 5);

        // then
        // 지하철_노선에_지하철역_등록됨
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철_노선_조회_요청(신분당선);
        LineResponse lineResponse = getLineResponse(response);

        assertThat(lineResponse.getStations().get(0).getId()).isEqualTo(강남역);
        assertThat(lineResponse.getStations().get(1).getId()).isEqualTo(판교역);
        assertThat(lineResponse.getStations().get(2).getId()).isEqualTo(양재역);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addBetweenSectionExpectedException() {
        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 판교역, 7);

        // then
        // 지하철_노선에_지하철역_등록_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addExistSectionExpectedException() {
        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 양재역, 강남역, 6);

        // then
        // 지하철_노선에_지하철역_등록_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addNotIncludeSectionExpectedException() {
        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 판교역, 양재시민의숲, 10);

        // then
        // 지하철_노선에_지하철역_등록_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("거리가 0이면 추가할 수 없음")
    @Test
    void distanceZeroExpectedException() {
        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 판교역, 0);

        // then
        // 지하철_노선에_지하철역_등록_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_구간_등록_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/{lineId}/sections", lineId).
                then().
                log().all().
                extract();
    }

}
