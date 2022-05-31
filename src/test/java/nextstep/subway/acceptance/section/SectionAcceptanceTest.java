package nextstep.subway.acceptance.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.acceptance.base.BaseAcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {
    StationResponse 양재역;
    StationResponse 청계산입구역;
    StationResponse 판교역;
    LineResponse 신분당선;
    @BeforeEach
    protected void setUp() {
        super.setUp();
        // Given
        양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
        청계산입구역 = 지하철역_생성_요청("청계산입구역").as(StationResponse.class);
        판교역 = 지하철역_생성_요청("판교역").as(StationResponse.class);
        신분당선 = 지하철노선_생성_요청("신분당선", "bg-red-600", 양재역.getId(), 판교역.getId(), 10).as(LineResponse.class);
    }

    /**
     * When 지하철 노선에 사이에 새로운 구간을 등록하면
     * Then 지하철 노선에 사이에 새로운 역이 등록된다.
     */
    @DisplayName("지하철노선 생성")
    @Test
    void addSection() {
        // when
        ExtractableResponse<Response> response = 지하철구간_추가_요청(신분당선.getId(), 청계산입구역.getId(), 판교역.getId(), 7);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).hasSize(2);
        assertThat(stationNames).containsExactly(양재역.getName(), 청계산입구역.getName(), 판교역.getName());
    }

    ExtractableResponse<Response> 지하철구간_추가_요청(long lineId, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post( "/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }
}
