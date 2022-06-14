package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록_요청하기;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    ObjectMapper objectMapper;

    ExtractableResponse<Response> 신분당선_생성_응답_결과;
    StationResponse 강남역;
    StationResponse 정자역;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        super.setUp();

        // given
        강남역 = 지하철역_등록_요청하기("강남역").as(StationResponse.class);
        정자역 = 지하철역_등록_요청하기("정자역").as(StationResponse.class);

        신분당선_생성_응답_결과 = 지하철노선_생성_요청("신분당선", "bg-red-600", 강남역.getId(), 정자역.getId(), 10);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    void 지하철노선_생성후_조회() {
        // then
        assertThat(신분당선_생성_응답_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> 지하철노선_목록_조회_응답결과 = 지하철노선_목록_조회_요청();
        정상_응답_확인(지하철노선_목록_조회_응답결과);
    }


    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철노선_목록_조회() {
        // given
        StationResponse 왕십리역 = 지하철역_등록_요청하기("왕십리역").as(StationResponse.class);
        StationResponse 복정역 = 지하철역_등록_요청하기("복정역").as(StationResponse.class);
        지하철노선_생성_요청("분당선", "bg-green-600", 왕십리역.getId(), 복정역.getId(), 10);

        // when
        ExtractableResponse<Response> 지하철노선_목록_조회_결과 = 지하철노선_목록_조회_요청();

        // then
        정상_응답_확인(지하철노선_목록_조회_결과);
        assertThat(지하철노선_목록_조회_결과.jsonPath().getList("name")).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철노선_조회() {
        // when
        ExtractableResponse<Response> 지하철노선_조회_응답_결과 = 지하철노선_조회_요청(신분당선_생성_응답_결과.jsonPath().getLong("id"));

        // then
        정상_응답_확인(지하철노선_조회_응답_결과);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철노선_수정() throws JsonProcessingException {
        // given
        StationResponse 미금역 = 지하철역_등록_요청하기("미금역").as(StationResponse.class);
        long id = 신분당선_생성_응답_결과.body().jsonPath().getLong("id");

        // when
        String 지하철노선_수정_요청값 = objectMapper.writeValueAsString(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 미금역.getId(), 11));
        ExtractableResponse<Response> 지하철노선_수정_응답_결과 = 지하철노선_수정_요청(id, 지하철노선_수정_요청값);

        // then
        정상_응답_확인(지하철노선_수정_응답_결과);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철노선_삭제() {
        // when
        ExtractableResponse<Response> 지하철노선_삭제_응답_결과 = 지하철노선_삭제_요청(신분당선_생성_응답_결과.jsonPath().getLong("id"));
        // then
        결과_없음_응답_확인(지하철노선_삭제_응답_결과);
    }

    public static ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, long upStationId,
                                                            long downStationId,
                                                            int distance) {
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

    private ExtractableResponse<Response> 지하철노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_삭제_요청(Long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(Long id, String requestBody) {
        return RestAssured.given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private void 정상_응답_확인(ExtractableResponse<Response> 응답결과) {
        assertThat(응답결과.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 결과_없음_응답_확인(ExtractableResponse<Response> 응답결과) {
        assertThat(응답결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
