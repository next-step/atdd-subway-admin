package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
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
     * When 노선을 생성하면
     * Then 노선이 생성된다
     * Then 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        StationResponse upStation = 응답_객체_생성(지하철역_등록("강남역"), StationResponse.class);
        StationResponse downStation = 응답_객체_생성(지하철역_등록("잠실역"), StationResponse.class);
        ValidatableResponse createResponse = 노선_등록("2호선", "초록", 10, upStation.getId(), downStation.getId());

        // then
        응답_검증(createResponse, HttpStatus.CREATED);

        // then
        List<LineResponse> lines = 노선_목록_조회();
        노선_등록_검증(lines, "2호선");
    }

    private ValidatableResponse 노선_등록(String name, String color, Integer distance, Long upStreamId, Long downStreamId) {
        LineRequest lineRequest = new LineRequest(name, color, distance, upStreamId, downStreamId);

        return RestAssured.given().log().all()
                    .body(lineRequest)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/lines")
                    .then().log().all();
    }

    public static List<LineResponse> 노선_목록_조회() {
        ValidatableResponse listResponse = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all();
        return getJsonPathForResponse(listResponse).getList("$", LineResponse.class);
    }

    public static void 노선_등록_검증(List<LineResponse> stations, String name) {
        assertThat(stations).containsAnyOf(new LineResponse(name));
    }
}
