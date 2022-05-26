package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        지하철역_생성_성공("강남역");

        // then
        지하철역_조회_성공("강남역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_성공("강남역");

        // when
        // then
        지하철역_생성_실패("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        지하철역_생성_성공("강남역");
        지하철역_생성_성공("서초역");

        // when
        // then
        지하철역_조회_성공("강남역", "서초역");
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        //given
        String location = 지하철역_생성_성공("강남역");

        //when
        지하철역_삭제_성공(location);

        //then
        지하철역_조회_실패("강남역");
    }

    String 지하철역_생성_성공(String name) {
        ExtractableResponse<Response> response = StationApi.create(name);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return response.header("location");
    }

    void 지하철역_생성_실패(String name) {
        ExtractableResponse<Response> response = StationApi.create(name);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    void 지하철역_조회_성공(String... names) {
        ExtractableResponse<Response> response = StationApi.findAll();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> findNames = response.jsonPath().getList("name", String.class);
        assertThat(findNames).containsAnyOf(names);
    }

    void 지하철역_조회_실패(String name) {
        ExtractableResponse<Response> response = StationApi.findAll();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> findNames = response.jsonPath().getList("name", String.class);
        assertThat(findNames.stream()
                .filter(name::equals)
                .findAny())
                .isNotPresent();
    }

    void 지하철역_삭제_성공(String location) {
        ExtractableResponse<Response> response = StationApi.delete(location);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
