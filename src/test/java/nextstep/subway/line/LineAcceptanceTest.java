package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.domain.Line;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철호선 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    @AfterEach
    public void cleanUp() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("지하철호선 생성 테스트")
    void createLineTest() {
        //given
        String lineName = "신분당선";
        Long upStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("지하철역"));
        Long downStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("새로운지하철역"));

        //when
        assertThat(createLineAndGetResponseCode(
                new LineRequest(lineName,"bg-red-600",upStationId,downStationId,10)))
                .isEqualTo(HttpStatus.CREATED.value());

        //then
        assertThat(retrieveLineNames().stream().anyMatch(lineName::equals))
                .isTrue();
    }

    @Test
    @DisplayName("지하철호선 두개 생성 후 목록조회 테스트")
    void getLinesTest() {
        //given
        String lineName1 = "신분당선";
        String lineName2 = "분당선";
        Long stationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("지하철역"));
        Long newStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("새로운지하철역"));
        Long anotherStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("또다른지하철역"));
        createLine(new LineRequest(lineName1, "bg-red-600", stationId, newStationId, 10));
        createLine(new LineRequest(lineName2, "bg-red-600", stationId, anotherStationId, 10));

        //when
        List<String> lineNames = retrieveLineNames();

        //then
        assertThat(lineNames.size()).isEqualTo(2);
        assertThat(lineNames.stream()
                .allMatch(lineName -> lineName1.equals(lineName) || lineName2.equals(lineName)))
                .isTrue();
    }

    /**
     * 지하철호선 생성
     */
    ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        final Map param = new HashMap();
        param.put("name", lineRequest.getName());
        param.put("color", lineRequest.getColor());
        param.put("upStationId", lineRequest.getUpStationId());
        param.put("downStationId", lineRequest.getDownStationId());
        param.put("distance", lineRequest.getDistance());

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    /**
     * 지하철역 생성 후 http상태코드 리턴
     */
    int createLineAndGetResponseCode(LineRequest lineRequest) {
        return createLine(lineRequest).statusCode();
    }

    /**
     * 지하철호선 목록조회
     */
    List<Line> retrieveLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract().jsonPath()
                .getList("",Line.class);
    }

    /**
     * 지하철호선 이름 목록조회
     */
    List<String> retrieveLineNames() {
        return retrieveLines().stream().map(Line::getName).collect(Collectors.toList());
    }


}
