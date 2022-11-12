package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.LineRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.subway.station.StationAcceptanceFixture.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceFixture.지하철역_아이디_조회;


@DisplayName("지하철 노선 관련 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * - When 지하철 노선을 생성하면
     * - Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void create_line() {
        // given
        ValidatableResponse 강남역 = 지하철역_생성("강남역");
        ValidatableResponse 잠실역 = 지하철역_생성("잠실역");

        // when 지하철 노선 생성
        LineRequest request = new LineRequest("강남잠실노선", "red", 지하철역_아이디_조회(강남역), 지하철역_아이디_조회(잠실역), 10);
        ValidatableResponse response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all();
        // then 생성한 노선을 찾을 수 있다.
        Assertions.assertThat(response.extract().statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("지하철노선 목록 조회")
    @Test
    void get_line_list() {

    }

    @DisplayName("지하철노선 조회")
    @Test
    void get_line() {

    }

    @DisplayName("지하철노선 수정")
    @Test
    void update_line() {

    }

    @DisplayName("지하철노선 삭제")
    @Test
    void delete_line() {

    }

}
