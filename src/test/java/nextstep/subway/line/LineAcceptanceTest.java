package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;

import static nextstep.subway.line.LineAcceptanceStep.*;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @LocalServerPort
    int port;

    private int upLastStationId;
    private int downLastStationId;


    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        // db 초기화
        databaseCleanup.execute();

        createStations();
    }

    private void createStations() {
        upLastStationId = 지하철역을_생성한다("강남역").jsonPath().get("id");
        downLastStationId = 지하철역을_생성한다("역삼역").jsonPath().get("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        //given
        노선_한개_생성한다(upLastStationId, downLastStationId);

        //when
        List<String> allLineNames = 모든_노선_이름을_조회한다();

        //then
        노선_이름이_조회된다(allLineNames, "2호선");
    }



    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void findLines() {
        //given
        노선_2개_생성(upLastStationId, downLastStationId);

        //when
        ExtractableResponse<Response> allLines = 모든_노선을_조회한다();

        //then
        노선의_수가_일치한다(allLines, 2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철노선 조회")
    @Test
    void findLine() {
        // given
        ExtractableResponse<Response> savedLine = 노선_한개_생성한다(upLastStationId, downLastStationId);;

        // when
        ExtractableResponse<Response> result = 특정_노선을_조회한다(savedLine.jsonPath().getInt("id"));

        // then
        지하철_노선_정보_확인(savedLine, result);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void modifyLine() {
        // given
        ExtractableResponse<Response> savedLine = 노선_한개_생성한다(upLastStationId, downLastStationId);;
        int id = savedLine.jsonPath().get("id");

        // when
        ExtractableResponse<Response> result = 노선_정보를_수정한다(id);

        // then
        지하철_노선_정보_수정_확인(id, result);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> savedLine = 노선_한개_생성한다(upLastStationId, downLastStationId);
        int id = savedLine.jsonPath().get("id");

        // when
        특정_노선을_제거한다(id);

        // then
        해당_노선의_정보가_삭제된다(id);
    }
}
