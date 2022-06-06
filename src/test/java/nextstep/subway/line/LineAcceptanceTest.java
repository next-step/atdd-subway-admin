package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.util.DatabaseCleanup;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.StationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 노선 기능")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;
    @Autowired
    StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
        지하철역_추가();
    }

    /**
     * When 지하철 노선을 추가 하면
     * Then 지하철 노선이 생성 된다
     * Then 지하촐 노선을 조회 하면, 추가된 노선이 조회 된다.
     */
    @Test
    void 지하철_노선_추가() {
        // when
        LineResponse lineResponse = 노선_추가(LineTest.이호선);
        // then
        assertThat(lineResponse.getId()).isEqualTo(LineTest.이호선.getId());
    }

    /**
     * Given 2개의 지하철 노선을 추가 하고
     * When 지하철 노선 목록을 조회 하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회 할 수 있다.
     */
    @Test
    void 지하철_노선_목록_조회() {
        // given
        노선_추가(LineTest.이호선);
        노선_추가(LineTest.사호선);
        // when
        // then
        assertThat(노선_전체_조회()).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성 하고
     * When 생성한 지하철 노선을 조회 하면
     * Then 생성한 지하철 노선의 정보를 응답 받을 수 있다.
     */
    @Test
    void 지하철_노선_조회() {
        // given
        final Long id = 노선_추가(LineTest.이호선).getId();
        // when
        LineResponse expected = 노선_ID_조회(id);
        // then
        assertThat(expected.getId()).isEqualTo(LineTest.이호선.getId());
        assertThat(expected.getName()).isEqualTo(LineTest.이호선.getName());
        assertThat(expected.getColor()).isEqualTo(LineTest.이호선.getColor());
    }

    /**
     * Given 지하철 노선을 생성 하고
     * When 생성한 지하철 노선 수정 하면
     * Then 해당 지하철 노선 정보는 수정 된다
     */
    @Test
    void 지하철_노선_수정() {
        // given
        final Long id = 노선_추가(LineTest.이호선).getId();
        final LineUpdateRequest lineUpdateRequest = new LineUpdateRequest("1호선", "bg-red-600");
        // when
        ExtractableResponse<Response> response = 노선_수정(id, lineUpdateRequest);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Given 지하철 노선을 생성 하고
     * When 생성한 지하철 노선을 삭제 하면
     * Then 해당 지하철 노선 정보는 삭제된 다
     */
    @Test
    void 지하철_노선_삭제() {
        // given
        final Long id = 노선_추가(LineTest.이호선).getId();
        // when
        ExtractableResponse<Response> expected = 노선_삭제(id);
        // then
        assertThat(expected.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private LineResponse 노선_추가(final Line line) {
        return RestAssured.given().log().all()
                .body(lineAddRequest(line))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract().jsonPath().getObject(".", LineResponse.class);
    }

    private LineResponse 노선_ID_조회(final long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract().jsonPath().getObject(".", LineResponse.class);
    }

    private List<String> 노선_전체_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> 노선_수정(final long id, final LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .body(lineUpdateRequest)
                .when().put("lines/{id}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_삭제(final Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }

    private Map<String, Object> lineAddRequest(final Line line) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", line.getId());
        map.put("name", line.getName());
        map.put("color", line.getColor());
        map.put("upStationId", line.getUpStation().getId());
        map.put("downStationId", line.getDownStation().getId());
        map.put("distance", line.getDistance());

        return map;
    }

    private void 지하철역_추가() {
        stationRepository.save(StationTest.강남역);
        stationRepository.save(StationTest.사당역);
        stationRepository.save(StationTest.이수역);
    }
}
