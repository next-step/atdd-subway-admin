package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.*;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    private StationResponse 강남역;
    private StationResponse 잠실역;
    private StationResponse 모란역;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();

        강남역 = 응답_객체_생성(지하철역_등록("강남역"), StationResponse.class);
        잠실역 = 응답_객체_생성(지하철역_등록("잠실역"), StationResponse.class);
        모란역 = 응답_객체_생성(지하철역_등록("모란역"), StationResponse.class);
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
        ValidatableResponse createResponse = 노선_등록("2호선", "초록", 10, 강남역.getId(), 잠실역.getId());

        // then
        응답_검증(createResponse, HttpStatus.CREATED);

        // then
        List<LineResponse> lines = 목록_조회("/lines", LineResponse.class);
        개수_검증(lines, 1);
    }

    /**
     * Given 노선을 생성하고
     * When 기존에 존재하는 노선 이름으로 노선을 생성하면
     * Then 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        노선_등록("2호선", "초록", 10, 강남역.getId(), 잠실역.getId());

        // when
        ValidatableResponse response = 노선_등록("2호선", "초록", 10, 강남역.getId(), 잠실역.getId());

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 2개의 노선을 생성하고
     * When 노선 목록을 조회하면
     * Then 2개의 노선을 응답 받는다
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // given
        노선_등록("2호선", "초록", 10, 강남역.getId(), 잠실역.getId());
        노선_등록("8호선", "분홍", 10, 모란역.getId(), 잠실역.getId());

        // when
        List<LineResponse> lines = 목록_조회("/lines", LineResponse.class);

        // then
        개수_검증(lines, 2);
    }

    /**
     * Given 노선을 생성하고
     * When 그 노선을 삭제하면
     * Then 그 노선 목록 조회 시 생성한 노선을 찾을 수 없다
     */
    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineResponse 지하철2호선 = 응답_객체_생성(노선_등록("2호선", "초록", 10, 강남역.getId(), 잠실역.getId()), LineResponse.class);

        // when
        삭제("/lines", 지하철2호선.getId());

        // then
        List<LineResponse> lines = 목록_조회("/lines", LineResponse.class);
        개수_검증(lines, 0);
    }

    private ValidatableResponse 노선_등록(String name, String color, Integer distance, Long upStreamId, Long downStreamId) {
        LineRequest lineRequest = new LineRequest(name, color, distance, upStreamId, downStreamId);

        return RestAssured.given().log().all()
                    .body(lineRequest)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().post("/lines")
                    .then().log().all();
    }
}
