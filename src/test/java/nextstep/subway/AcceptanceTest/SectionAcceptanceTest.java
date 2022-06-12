package nextstep.subway.AcceptanceTest;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.StationApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선에 지하철역이 등록된다.
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void create() {
        // when

        // then

        // then
    }

    /**
     * Given 하행역을 상행 종점역과 동일하게 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선에 새로운 상행 종점역이 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createNewUpStation() {
        // when

        // then

        // then
    }

    /**
     * Given 상행역을 하행 종점역과 동일하게 입력한다.
     * When 지하철 노선에 지하철역 등록을 요청한다.
     * Then 지하철 노선에 새로운 하행 종점역이 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void createNewDownStation() {
        // when

        // then

        // then
    }
}
