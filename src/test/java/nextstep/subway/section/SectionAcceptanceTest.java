package nextstep.subway.section;

import static nextstep.subway.line.LineAssuredMethod.노선_생성_요청;
import static nextstep.subway.station.StationAssuredMethod.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class SectionAcceptanceTest extends BaseAcceptanceTest {

    private Long 강남역_id;
    private Long 광교역_id;
    private Long 신분당선_id;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        강남역_id = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        광교역_id = 지하철역_생성_요청("광교역").jsonPath().getLong("id");
        LineRequest 신분당선 = LineRequest.of("신분당선", "bg-red-600", 강남역_id, 광교역_id, 10);
        신분당선_id = 노선_생성_요청(신분당선).jsonPath().getLong("id");
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionAtBackward() {
        // given 지하철 노선에 지하철역 등록 요청
        long 양재역_id = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        // when 신분당선 지하철 노선에 새로운 구간을 등록 요청(하행 신규)
        SectionRequest 새로운_구간 = SectionRequest.of(광교역_id, 양재역_id, 5);
        ExtractableResponse<Response> 구간_생성_요청_응답 = 구간_생성_요청(새로운_구간, 신분당선_id);

        // then 지하철_노선에_구간_등록됨
        노선_생성_성공_확인(구간_생성_요청_응답);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionAtFront() {
        // given 지하철 노선에 지하철역 등록 요청
        long 양재역_id = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        // when 신분당선 지하철 노선에 새로운 구간 등록 요청(상행 신규)
        SectionRequest 새로운_구간 = SectionRequest.of(양재역_id, 강남역_id, 5);
        ExtractableResponse<Response> 구간_생성_요청_응답 = 구간_생성_요청(새로운_구간, 신분당선_id);

        // then 지하철_노선에_구간_등록됨
        노선_생성_성공_확인(구간_생성_요청_응답);
    }

    private void 노선_생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 구간_생성_요청(SectionRequest sectionRequest, Long lindId) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("lines/{lindId}/sections", lindId)
                .then().log().all()
                .extract();
    }

}
