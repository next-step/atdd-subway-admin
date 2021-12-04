package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

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

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

        final LineRequest request = new LineRequest(
            "신분당선",
            "bg-red-600",
            강남역.getId(),
            광교역.getId(),
            10
        );
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음(request).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        final StationResponse 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역")
            .as(StationResponse.class);

        // when
        final SectionRequest request = new SectionRequest(강남역.getId(), 판교역.getId(), 4);
        final ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(request);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void add_inBetween_invalidDistance() {
        // given
        final StationResponse 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역")
            .as(StationResponse.class);

        // when
        final SectionRequest request = new SectionRequest(강남역.getId(), 판교역.getId(), 50);
        final ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void add_existingSection() {
        // when
        final SectionRequest request = new SectionRequest(강남역.getId(), 광교역.getId(), 10);
        final ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void add_noMatchingStations() {
        // given
        final StationResponse 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역")
            .as(StationResponse.class);
        final StationResponse 정자역 = StationAcceptanceTest.지하철역_등록되어_있음("정자역")
            .as(StationResponse.class);

        // when
        final SectionRequest request = new SectionRequest(판교역.getId(), 정자역.getId(), 1);
        final ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(final SectionRequest request) {
        return RestAssured.given().log().all()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/" + 신분당선.getId() + "/sections")
            .then().log().all()
            .extract();
    }

    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }
}
