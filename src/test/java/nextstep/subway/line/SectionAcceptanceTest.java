package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
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


    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        // 지하철_역_생성_요청
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역");
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역");
        판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역");

        // 지하철_노선_생성_요청
        신분당선 = LineAcceptanceTest.지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", 강남역 + "");
        params.put("downStationId", 판교역 + "");
        params.put("distance", 10 + "");
        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/{lineId}/sections", 신분당선).
                then().
                log().all().
                extract();

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.지하철_노선_조회_요청(신분당선);

        assertThat(LineAcceptanceTest.isContainsStationToLineResponse(getLineResponse(lineResponse), 판교역)).isTrue();
    }

}
