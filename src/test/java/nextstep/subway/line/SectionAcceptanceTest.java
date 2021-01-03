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

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        StationResponse 강남역 = StationAcceptanceTest.지하철_생성_요청("강남역").as(StationResponse.class);
        StationResponse 광교역 = StationAcceptanceTest.지하철_생성_요청("광교역").as(StationResponse.class);

        Map<String, String> createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStationId", 강남역.getId() + "");
        createParams.put("downStationId", 광교역.getId() + "");
        createParams.put("distance", 10 + "");
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
        params.put("distance", 10);
        ExtractableResponse<Response> response = 지하철_구간_등록_요청(신분당선Id, params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).startsWith("/lines/" + 신분당선Id + "/sections/");
    }

    static ExtractableResponse<Response> 지하철_구간_등록_요청(Long lineId, Map<String, Object> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all().extract();
    }
}
