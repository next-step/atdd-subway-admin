package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationStep;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class LineSectionAcceptanceTest extends AcceptanceTest {

    private static final StationRequest 교대역_요청 = new StationRequest("교대역");
    private static final StationRequest 강남역_요청 = new StationRequest("강남역");
    private static final StationRequest 역삼역_요청 = new StationRequest("역삼역");
    private static final StationRequest 삼성역_요청 = new StationRequest("삼성역");
    private static final StationRequest 잠실역_요청 = new StationRequest("잠실역");

    private StationResponse 교대역;
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 삼성역;
    private StationResponse 잠실역;
    private LineResponse 이호선;

    @BeforeEach
    void beforeEach() {
        교대역 = StationStep.지하철역_생성되어_있음(교대역_요청);
        강남역 = StationStep.지하철역_생성되어_있음(강남역_요청);
        역삼역 = StationStep.지하철역_생성되어_있음(역삼역_요청);
        삼성역 = StationStep.지하철역_생성되어_있음(삼성역_요청);
        잠실역 = StationStep.지하철역_생성되어_있음(잠실역_요청);

        이호선 = LineStep.지하철_노선_등록되어_있음(new LineRequest("2호선", "bg-green-600", 강남역.getId(), 삼성역.getId(), 10));
    }

    @Test
    void addLineSection_역_사이에_새로운_역을_등록한다() {
        ExtractableResponse<Response> response = RestAssured
                                                    .given().log().all()
                                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                    .body(new SectionRequest(강남역.getId(), 역삼역.getId(), 3))
                                                    .when().post("/lines/{lineId}/sections", 이호선.getId())
                                                    .then().log().all()
                                                    .extract();

        LineResponse lineResponse = LineStep.지하철_노선_조회되어_있음(이호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getStations()).containsExactly(강남역, 역삼역, 삼성역);
    }
}
