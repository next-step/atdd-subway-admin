package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.application.StationService;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.util.DatabaseCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철노선 관련 기능")
@ActiveProfiles(value = "acceptance")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner cleaner;

    @Autowired
    LineRepository lines;

    @Autowired
    StationService stations;

    StationResponse gangnam;
    StationResponse gyodae;
    StationResponse sinchon;
    StationResponse sillim;

    @BeforeEach
    void beforeEach() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        createStations();
    }

    @AfterEach
    public void afterEach() {
        cleaner.execute();
    }

    void createStations() {
        gangnam = stations.saveStation(new StationRequest("gangnam"));
        gyodae = stations.saveStation(new StationRequest("gyodae"));
        sinchon = stations.saveStation(new StationRequest("sinchon"));
        sillim = stations.saveStation(new StationRequest("sillim"));
    }

    /**
     * Given 지하철 노선 정보가 주어진다
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // - 노선명, 노선색상, 첫 출발 지하철역, 마지막 지하철역, 노선길이
        LineRequest request = new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L);

        // when
        ExtractableResponse<Response> created = createLine(request);

        // then
        // - 노선에 역을 매핑하진 않지만 조회 시 포함된 역 목록이 함께 반환된다
        assertThat(created.as(LineResponse.class).getId()).isNotNull();
    }

    /**
     * Given Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선을 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L));
        createLine(new LineRequest("2호선", "bg-blue-200", sinchon.getId(), sillim.getId(), 80L));

        // when
        List<String> lineNames = getLineNames();

        // then
        assertThat(lineNames).contains("신분당선", "2호선");
        assertThat(lineNames.size()).isEqualTo(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Long id = createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L))
                .body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = getLine(id);

        // then
        assertThat(response.as(LineResponse[].class)[0].getId()).isNotNull();
    }

    /**
     * Given 지하철역을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long id = createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L))
                .body().jsonPath().getLong("id");

        // when
        updateLine(id, new LineRequest("뉴신분당선", "bg-white-400"));

        // then
        LineResponse response = getLine(id).as(LineResponse[].class)[0];
        assertAll(
                () -> assertEquals(response.getName(), "뉴신분당선"),
                () -> assertEquals(response.getColor(), "bg-white-400")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Long id = createLine(new LineRequest("신분당선", "bg-red-600", gangnam.getId(), gyodae.getId(), 10L))
                .body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = deleteLine(id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    ExtractableResponse<Response> createLine(LineRequest request) {
        return RestAssured.given().log().all()
                          .body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines").then().log().all()
                          .extract();
    }

    ExtractableResponse<Response> getLine(Long id) {
        return RestAssured.given().log().all()
                          .when().queryParam("id", id).get("/lines")
                          .then().log().all()
                          .extract();
    }

    List<String> getLineNames() {
        return RestAssured.given().log().all()
                          .when().get("/lines").then().log().all()
                          .extract().jsonPath().getList("name", String.class);
    }

    void updateLine(Long id, LineRequest request) {
        RestAssured.given().log().all()
                   .body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                   .when().put("/lines/{id}", id)
                   .then().log().all()
                   .extract();
    }

    ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured.given().log().all()
                          .when().delete("/lines/{id}", id)
                          .then().log().all()
                          .extract();
    }
}
