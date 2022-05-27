package nextstep.subway.accept;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DisplayName("지하철역 관련 기능")
@Sql(value = "classpath:truncate_station_table.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    public static final StationRequest 강남역 = new StationRequest("강남역");
    public static final StationRequest 양재역 = new StationRequest("양재역");
    public static final StationRequest 서초역 = new StationRequest("서초역");
    public static final StationRequest 양재시민의숲역 = new StationRequest("양재시민의숲역");
    public static final StationRequest 교대역 = new StationRequest("교대역");

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철역을 생성하면 Then 지하철역이 생성된다 Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // when
        ExtractableResponse<Response> 생성된_강남역_응답 = 지하철역_생성_응답(강남역);

        // then
        assertThat(생성된_강남역_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(지하철역_목록()).containsAnyOf("강남역");
    }

    /**
     * Given 지하철역을 생성하고 When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 Then 지하철역 생성이 안된다
     */
    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    void createStationWithDuplicateName() {
        // given
        지하철역_생성(강남역);

        // when
        ExtractableResponse<Response> 생성_실패된_강남역_응답 = 지하철역_생성_응답(강남역);

        // then
        assertThat(생성_실패된_강남역_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철역을 생성하고 When 지하철역 목록을 조회하면 Then 2개의 지하철역을 응답 받는다
     */
    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStations() {
        // given
        Arrays.asList(양재역, 강남역).forEach(지하철역 -> 지하철역_생성(지하철역));

        // when
        final List<String> 역목록 = 지하철역_목록();

        // then
        assertAll(
                () -> assertThat(역목록).hasSize(2),
                () -> assertThat(역목록).contains("강남역", "양재역")
        );
    }

    /**
     * Given 지하철역을 생성하고 When 그 지하철역을 삭제하면 Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        StationResponse 생성된_강남역 = 지하철역_생성(강남역);

        // when
        지하철역_삭제(생성된_강남역.getId());

        // then
        assertThat(지하철역_목록()).doesNotContain("강남역");
    }

    public static void 지하철역_삭제(Long 역아이디) {
        RestAssured.given().log().all()
                .when().delete("/stations/" + 역아이디)
                .then().log().all();
    }

    public static ExtractableResponse<Response> 지하철역_생성_응답(StationRequest 역이름) {
        return RestAssured.given().log().all()
                .body(역이름)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static StationResponse 지하철역_생성(StationRequest 역이름) {
        return RestAssured.given().log().all()
                .body(역이름)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract().jsonPath()
                .getObject("", StationResponse.class);
    }

    public static List<String> 지하철역_목록() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }
}
