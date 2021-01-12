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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노션 구간 관련 기능 인수테스트")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static final String ADD_SECTION_URL_FORMAT = "/lines/%d/sections";
    private static final String REMOVE_SECTION_URL_FORMAT = "/lines/%d/sections?stationId=%d";
    private static final String SHOW_LINE_URL_FORMAT = "/lines/%d";

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
        lineResponse = 지하철_노선_등록되어_있음(lineRequest);
    }

    private StationResponse 지하철역_등록되어_있음(String name) {
        return StationAcceptanceTest.지하철역_생성_요청(name).body().as(StationResponse.class);
    }

    private LineResponse 지하철_노선_등록되어_있음(LineRequest lineRequest) {
        return LineAcceptanceTest.지하철노선_생성_요청(lineRequest).as(LineResponse.class);
    }

    @DisplayName("[노선에 구간을 등록] 기존 상행역 기준으로 역 사이에 새로운 역을 등록")
    @Test
    void addSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(A역.getId(), B역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.CREATED);
        지하철_노선_전체_구간_검증됨(Arrays.asList(A역.getId(), B역.getId(), C역.getId()));
    }

    @DisplayName("[노선에 구간을 등록] 기존 하행역 기준으로 역 사이에 새로운 역을 등록")
    @Test
    void addSection1() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(B역.getId(), C역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.CREATED);
        지하철_노선_전체_구간_검증됨(Arrays.asList(A역.getId(), B역.getId(), C역.getId()));
    }

    @DisplayName("[노선에 구간을 등록] 새로운 상행 종점")
    @Test
    void addSection2() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(B역.getId(), A역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.CREATED);
        지하철_노선_전체_구간_검증됨(Arrays.asList(B역.getId(), A역.getId(), C역.getId()));
    }

    @DisplayName("[노선에 구간을 등록] 새로운 하행 종점")
    @Test
    void addSection3() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(new SectionRequest(C역.getId(), B역.getId(), 8));
        // then
        지하철_노선에_지하철역_응답됨(response, HttpStatus.CREATED);
        지하철_노선_전체_구간_검증됨(Arrays.asList(A역.getId(), C역.getId(), B역.getId()));
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

    @DisplayName("노선 구간 제거 예외 하나의 구간 삭제 불가")
    @Test
    void validateMinSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(C역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("[노선 구간 제거] 역 사이의 구간 삭제")
    @Test
    void removeSection1() {
        // given
        ExtractableResponse<Response> createdResponse = 지하철_노선에_지하철역_등록_요청(new SectionRequest(B역.getId(), A역.getId(), 8));
        지하철_노선에_지하철역_응답됨(createdResponse, HttpStatus.CREATED);
        지하철_노선_전체_구간_검증됨(Arrays.asList(B역.getId(), A역.getId(), C역.getId()));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(A역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        지하철_노선_전체_구간_검증됨(Arrays.asList(B역.getId(), C역.getId()));
    }

    @DisplayName("[노선 구간 제거] 상행 역 구간 삭제")
    @Test
    void removeSection2() {
        // given
        지하철_노선에_지하철역_등록_요청(new SectionRequest(B역.getId(), A역.getId(), 8));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(B역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        지하철_노선_전체_구간_검증됨(Arrays.asList(A역.getId(), C역.getId()));
    }

    @DisplayName("[노선 구간 제거] 하행 역 구간 삭제")
    @Test
    void removeSection3() {
        // given
        지하철_노선에_지하철역_등록_요청(new SectionRequest(B역.getId(), A역.getId(), 8));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_삭제_요청(C역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        지하철_노선_전체_구간_검증됨(Arrays.asList(B역.getId(), A역.getId()));
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_삭제_요청(Long id) {
        // when
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete(지하철_노선에_지하철역_삭제_요청_URL(id))
                .then().log().all().extract();
    }

    private String 지하철_노선에_지하철역_삭제_요청_URL(Long id) {
        return String.format(REMOVE_SECTION_URL_FORMAT, lineResponse.getId(), id);
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(SectionRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(지하철_노선에_지하철역_요청_URL())
                .then().log().all().extract();
    }

    private void 지하철_노선_전체_구간_검증됨(List<Long> expectedIds) {
        String url = String.format(SHOW_LINE_URL_FORMAT, lineResponse.getId());
        ExtractableResponse<Response> response = LineAcceptanceTest.지하철노선_조회_요청(url);
        LineResponse line = response.body().as(LineResponse.class);
        List<Long> actualIds = line.getStationsResponses().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(actualIds).containsAll(expectedIds);
    }

    private String 지하철_노선에_지하철역_요청_URL() {
        return String.format(ADD_SECTION_URL_FORMAT, lineResponse.getId());
    }

    private void 지하철_노선에_지하철역_응답됨(ExtractableResponse<Response> response, HttpStatus badRequest) {
        assertThat(response.statusCode()).isEqualTo(badRequest.value());
    }
}
