package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
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
import java.util.Map;

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
        Long upStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("강남역"));
        Long downStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("광교역"));
        //when
        //then
        assertThat(createLineAndGetResponseCode(
                new LineRequest("신분당선","bg-red-600",upStationId,downStationId,10)))
                .isEqualTo(HttpStatus.CREATED.value());
    }

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

    int createLineAndGetResponseCode(LineRequest lineRequest) {
        return createLine(lineRequest).statusCode();
    }
}
