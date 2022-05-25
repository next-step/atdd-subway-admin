package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.Arrays;

import nextstep.subway.SubwayTestFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    private void 지하철_역_생성(String name) {
        SubwayTestFactory.generateStation(name);
    }

    private ExtractableResponse<Response> 지하철_역_생성_요청(String name) {
        return SubwayTestFactory.generateStationToResponse(name);
    }

    private void 지하철_역_생성_실패_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_역_생성_성공_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
    
    private void 지하철_역_조회_성공_확인(List<String> names, String... name) {
        assertThat(names).contains(name);
    }

    private void 지하철_역_조회_실패_확인(List<String> names, String... name) {
        assertThat(names).doesNotContain(name);
    }

    private List<String> 지하철_역_목록_조회() {
        return SubwayTestFactory.findStations("name", String.class);
    }

    private Long 지하철_역_생성_ID_추출(String name) {
        return 지하철_역_생성_요청(name)
                .jsonPath().getObject("id", Long.class);
    }

    private void 지하철_역_삭제_요청(Long id) {
        SubwayTestFactory.deleteStationById(id);
    }

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
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
        ExtractableResponse<Response> 지하철_역_생성_응답_결과 = 지하철_역_생성_요청("강남역");
        // then
        지하철_역_생성_성공_확인(지하철_역_생성_응답_결과);
        // then
        List<String> 지하철_역_이름_목록 = 지하철_역_목록_조회();
        지하철_역_조회_성공_확인(지하철_역_이름_목록, "강남역");
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
        지하철_역_생성("강남역");
        // when
        ExtractableResponse<Response> 지하철_역_생성_요청_결과 = 지하철_역_생성_요청("강남역");
        // then
        지하철_역_생성_실패_확인(지하철_역_생성_요청_결과);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        //given
        지하철_역_생성("공덕역");
        지하철_역_생성("애오개역");
        //when
        List<String> 지하철_역_이름_목록 = 지하철_역_목록_조회();
        //then
        지하철_역_조회_성공_확인(지하철_역_이름_목록, "공덕역", "애오개역");
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
        Long 지하철_역_ID = 지하철_역_생성_ID_추출("공덕역");
        //when
        지하철_역_삭제_요청(지하철_역_ID);
        //then
        List<String> 지하철_역_이름_목록 = 지하철_역_목록_조회();
        지하철_역_조회_실패_확인(지하철_역_이름_목록, "공덕역");
    }
}
