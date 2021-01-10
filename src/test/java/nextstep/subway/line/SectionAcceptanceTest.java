package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노션 구간 관련 기능 인수테스트")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static final String ADD_SECTION_URL_FORMAT = "/lines/%d/sections";

    private StationResponse A역;
    private StationResponse B역;
    private StationResponse C역;
    private LineResponse lineResponse;

    @BeforeEach
    public void setUp() {
        super.setUp();
        A역 = 지하철역_등록되어_있음("A역");
        B역 = 지하철역_등록되어_있음("B역");
        C역 = 지하철역_등록되어_있음("C역");
        LineRequest lineRequest = new LineRequest("신분당선", "br-red-600", A역.getId(), C역.getId(), 10);
        lineResponse =  지하철_노선_등록되어_있음(lineRequest);
    }

    private StationResponse 지하철역_등록되어_있음(String name) {
        return StationAcceptanceTest.지하철역_생성_요청(name).body().as(StationResponse.class);
    }

    private LineResponse 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return LineAcceptanceTest.지하철노선_생성_요청(lineRequest).as(LineResponse.class);
    }

    @DisplayName("[노선에 구간을 등록] 역 사이에 새로운 역을 등록")
    @Test
    void addSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(A역.getId(), B역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.CREATED);
    }

    @DisplayName("[노선에 구간을 등록] 새로운 상행 종점")
    @Test
    void addSection2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(B역.getId(), A역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.CREATED);
    }

    @DisplayName("[노선에 구간을 등록] 새로운 하행 종")
    @Test
    void addSection3() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(C역.getId(), B역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.CREATED);
    }

    @DisplayName("[노선에 구간을 등록 예외] 기존역 사이 거리보다 크거나 같으면 등록 불가")
    @Test
    void validateDistance() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(A역.getId(), B역.getId(), 12));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[노선에 구간을 등록 예외] 이미 노선에 모두 등록되어 있다면 등록 불가")
    @Test
    void validateExist() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(A역.getId(), C역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("[노선에 구간을 등록 예외] 상행 하행 둘 중 하나도 포함되어있지 않으면 추가 불가")
    @Test
    void validateContain() {
        // given
        StationResponse D역 = 지하철역_등록되어_있음("D역");
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(B역.getId(), D역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.BAD_REQUEST);
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(SectionRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(지하철_노선에_지하철역_등록_요청_URL())
                .then().log().all().extract();
    }

    private String 지하철_노선에_지하철역_등록_요청_URL() {
        return String.format(ADD_SECTION_URL_FORMAT, lineResponse.getId());
    }

    private void 지하철_노선에_지하철역_응답됨(ExtractableResponse<Response> response, HttpStatus badRequest) {
        Assertions.assertThat(response.statusCode()).isEqualTo(badRequest.value());
    }
}
