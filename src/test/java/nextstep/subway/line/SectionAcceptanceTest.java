package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
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
    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 광교역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = StationAcceptanceTest.지하철_생성_요청("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철_생성_요청("광교역").as(StationResponse.class);

        Map<String, String> createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStationId", String.valueOf(강남역.getId()));
        createParams.put("downStationId", String.valueOf(광교역.getId()));
        createParams.put("distance", String.valueOf(10));
        신분당선 = LineAcceptanceTest.지하철_노선_생성_요청(createParams).as(LineResponse.class);
    }

    @Test
    @DisplayName("지하철 노선 첫 구간등록")
    void addSection() {
        //given
        Long 신분당선Id = 신분당선.getId();

        // when
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 4);
        params.put("downStationId", 2);
        params.put("distance", 4);
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(신분당선Id, params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).startsWith("/lines/" + 신분당선Id + "/sections/");
    }

    @Test
    void removeLineStation() {
        //given
        Long 신분당선Id = 신분당선.getId();
        StationResponse 역삼역 = StationAcceptanceTest.지하철_생성_요청("역삼역").as(StationResponse.class);

        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 광교역.getId());
        params.put("downStationId", 역삼역.getId());
        params.put("distance", 4);
        지하철_구간_등록_요청(신분당선Id, params);

        // when
        Map<String, String> 삭제Params = new HashMap<>();
        삭제Params.put("stationId", String.valueOf(광교역.getId()));
        ExtractableResponse<Response> 삭제Response = 지하철_구간_삭제_요청(신분당선Id, 삭제Params);

        // then
        assertThat(삭제Response.statusCode()).isEqualTo(HttpStatus.OK.value());
        //then
    }

    static ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, Map<String, Object> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all().extract();
    }

    static ExtractableResponse<Response> 지하철_구간_삭제_요청(Long lineId, Map<String, String> params) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .params(params)
                .when().delete("/lines/" + lineId + "/sections")
                .then().log().all().extract();
        return response;
    }
}
