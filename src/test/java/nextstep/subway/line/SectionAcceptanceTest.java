package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.line.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.station.StationSteps.지하철역_등록_되어있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private StationResponse 판교역;
    private StationResponse 정자역;
    private LineRequest requestParams;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록_되어있음("강남역").as(StationResponse.class);
        광교역 = 지하철역_등록_되어있음("광교역").as(StationResponse.class);
        판교역 = 지하철역_등록_되어있음("판교역").as(StationResponse.class);
        정자역 = 지하철역_등록_되어있음("정자역").as(StationResponse.class);

        requestParams = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
        신분당선 = 지하철_노선_등록되어_있음(requestParams).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 판교역, 정자역, 10);

        // then
        지하철_노선에_지하철역_등록됨(response);
    }

    private void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(LineResponse line, StationResponse upStation,
                                                             StationResponse downStation, int distance) {
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), distance);
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .when()
                .post("/lines/"+line.getId()+"/sections")
                .then().log().all()
                .extract();
    }
}
