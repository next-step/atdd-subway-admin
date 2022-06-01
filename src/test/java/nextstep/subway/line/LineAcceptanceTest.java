package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    private StationRepository stationRepository;

    private Station upStation;
    private Station downStation;
    private LineRequest lineRequest;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        upStation = stationRepository.save(new Station("상행지하철역"));
        downStation = stationRepository.save(new Station("하행지하철역"));
        lineRequest = new LineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void updateLine() {
        // given
        ExtractableResponse<Response> created = createLine(lineRequest);
        assertThat(created.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long id = created.jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> updated = updateLine(id, "신분당선 수정", "bg-red-500");
        assertThat(updated.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> actual = getOne(id);
        assertThat(actual.jsonPath().getString("name")).isEqualTo("신분당선 수정");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void deleteLine() {
        // given
        ExtractableResponse<Response> created = createLine(lineRequest);
        assertThat(created.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long id = created.jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> deleted = deleteLine(id);
        assertThat(deleted.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        ExtractableResponse<Response> actual = getOne(id);
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        //when
        ExtractableResponse<Response> response = createLine(lineRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = getAllLine().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철노선 목록을 조회한다.")
    void getAll() {
        // given
        ExtractableResponse<Response> response1 = createLine(lineRequest);
        ExtractableResponse<Response> response2 = createLine(
                new LineRequest("새로운추가노선", "bg-orange-500", upStation.getId(), downStation.getId(), 20)
        );
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        List<String> stationNames = getAllLine().jsonPath().getList("name", String.class);

        // then
        assertThat(stationNames).containsAnyOf("신분당선", "새로운추가노선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철노선을 조회한다.")
    void getOne() {
        // given
        ExtractableResponse<Response> response = createLine(lineRequest);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> actual = getOne(response.jsonPath().getLong("id"));

        // then
        assertThat(actual.jsonPath().getString("name")).isEqualTo("신분당선");
    }

    private ExtractableResponse<Response> getOne(Long id) {
        return RestAssured
                .given().log().all()
                .when().get("/lines/{id}", id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> updateLine(Long id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured
                .given().log().all().body(params).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> deleteLine(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/{id}", id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> getAllLine() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> createLine(LineRequest request) {
        return RestAssured
                .given().log().all().body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

}
