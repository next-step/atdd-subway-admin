package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DatabaseCleaner;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.SectionRequest;
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

@DisplayName("구간 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static String lineId;
    private static Long upStationId;
    private static Long downStationId;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        upStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("강남역"));
        downStationId = Long.parseLong(StationAcceptanceTest.createStationAndGetId("광교역"));

        lineId = LineAcceptanceTest.createLineAndGetId(
                new LineRequest("신분당선","bg-red-600",upStationId,downStationId,10));
    }

    @AfterEach
    public void cleanUp() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("노선에 구간을 등록한다.")
    void addSection() {
        //given
        //when
        final ExtractableResponse<Response> apiResponse = createSection(lineId,
                new SectionRequest(upStationId, downStationId, 1));

        //then
        assertThat(apiResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(apiResponse.jsonPath().getObject("downStation", Station.class)
                .getId())
                .isEqualTo(downStationId);
    }

    ExtractableResponse<Response> createSection(String lineId, SectionRequest sectionRequest) {
        final Map param = new HashMap();
        param.put("upStationId", sectionRequest.getUpStationId());
        param.put("downStationId", sectionRequest.getDownStationId());
        param.put("distance", sectionRequest.getDistance());

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all().extract();
    }
}
