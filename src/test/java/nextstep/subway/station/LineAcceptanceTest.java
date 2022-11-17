package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_성공_id_응답;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.LOCATION;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanUp databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    /**
     * When: 지하철 노선을 생성하면
     * Then: 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    void 노선_생성() {

        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // when:
        // 노선 생성
        Map<String, Object> lineParams = 노선_생성_요청파라미터("line", "bg-red-600", 삼전역_id, 강남역_id);
        ExtractableResponse<Response> response = 노선_생성_성공(lineParams);

        //then:
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given: 지하철 노선들을 생성하고
     * When:  지하철 노선들을 조회하면
     * Then:  생성한 노선들을 찾을 수 있다
     */
    @DisplayName("지하철 노선 목록을 가져온다")
    @Test
    void 노선_목록() {
        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선, 경인선 생성
        Map<String, Object> boondangLine = 노선_생성_요청파라미터("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        Map<String, Object> kyunginLine = 노선_생성_요청파라미터("kyunginLine", "bg-green-600", 삼전역_id, 강남역_id);
        LineResponse boondangResponse = 노선_생성_성공(boondangLine).body().jsonPath().getObject("", LineResponse.class);
        LineResponse kyunginResponse = 노선_생성_성공(kyunginLine).body().jsonPath().getObject("", LineResponse.class);

        // when:
        // 노선 목록 조회
        ExtractableResponse<Response> response = 노선_조회("/lines");

        List<Long> lineIds = response.body().jsonPath().getList("id", Long.class);

        // then:
        assertThat(lineIds).containsAnyOf(boondangResponse.getId(), kyunginResponse.getId());
    }

    /**
     * Given: 지하철 노선을 생성하고
     * When:  지하철 노선을 조회하면
     * Then:  생성한 지하철 노선을 찾을 수 있다
     */
    @DisplayName("단건 지하철 노선을 가져온다")
    @Test
    void 노선_단건_조회() {

        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선 생성
        Map<String, Object> boondangLine = 노선_생성_요청파라미터("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        ExtractableResponse<Response> createResponse = 노선_생성_성공(boondangLine);
        String boondangGetUrl = createResponse.headers().get(LOCATION).getValue();

        // when:
        // 분당선 조회
        ExtractableResponse<Response> response = 노선_조회(boondangGetUrl);
        Long lineId = response.body().jsonPath().getLong("id");

        // then:
        assertThat(lineId).isEqualTo(createResponse.body().jsonPath().getLong("id"));
    }

    /**
     * Given: 지하철 노선을 생성하고
     * When:  지하철 노선을 수정하면
     * Then:  지하철 노선의 수정을 확인할 수 있다.
     */
    @DisplayName("지하철 노선을 수정한다")
    @Test
    void 지하철_노선_수정() {
        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선 생성
        Map<String, Object> boondangLine = 노선_생성_요청파라미터("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        ExtractableResponse<Response> createResponse = 노선_생성_성공(boondangLine);
        String boondangGetUrl = createResponse.headers().get(LOCATION).getValue();

        // when:
        // 분당선 수정
        Map<String, String> updateRequest = new HashMap<>();
        String updateName = "다른라인";
        String updateColor = "bg-blue-600";
        updateRequest.put("name", updateName);
        updateRequest.put("color", updateColor);
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateRequest)
                .when().put(boondangGetUrl)
                .then().log().all()
                .extract();

        // 수정 된 분당선 조회
        ExtractableResponse<Response> updatedLine = 노선_조회(boondangGetUrl);
        String updatedName = updatedLine.body().jsonPath().get("name");
        String updatedColor = updatedLine.body().jsonPath().get("color");

        assertThat(updatedName).isEqualTo(updateName);
        assertThat(updatedColor).isEqualTo(updateColor);
    }

    /**
     * Given: 지하철 노선을 생성하고
     * When:  지하철 노선을 삭제하면
     * Then:  해당 지하철 노선은 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void 지하철_노선_삭제() {
        // given:
        // 삼전역, 강남역 생성
        long 삼전역_id = 지하철역_생성("삼전역");
        long 강남역_id = 지하철역_생성("강남역");

        // 분당선 생성
        Map<String, Object> boondangLine = 노선_생성_요청파라미터("boondangLine", "bg-red-600", 삼전역_id, 강남역_id);
        ExtractableResponse<Response> createResponse = 노선_생성_성공(boondangLine);
        String boondangGetUrl = createResponse.headers().get(LOCATION).getValue();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete(boondangGetUrl)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    long 지하철역_생성(String name) {
        final Map<String, String> param = new HashMap<>();
        param.put("name", name);
        return 지하철역_생성_성공_id_응답(param);
    }

    Map<String, Object> 노선_생성_요청파라미터(String name, String color, long upStationId, long downStationId) {
        Map<String, Object> boondangLine = new HashMap<>();
        boondangLine.put("name", name);
        boondangLine.put("color", color);
        boondangLine.put("upStationId", upStationId);
        boondangLine.put("downStationId", downStationId);
        boondangLine.put("distance", 10);
        return boondangLine;
    }

    ExtractableResponse<Response> 노선_생성_성공(Map<String, Object> lineParams) {
        return RestAssured.given().log().all()
                .body(lineParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 노선_조회(String url) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(url)
                .then().log().all()
                .extract();
    }
}
