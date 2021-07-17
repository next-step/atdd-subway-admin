package nextstep.subway;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();
    }

    protected LineResponse postLine(LineRequest params) {
        return RestAssured.given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract().body().as(LineResponse.class);
    }

    protected StationResponse postStation(StationRequest request) {
        return RestAssured.given()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract().body().as(StationResponse.class);
    }

    protected void postSection(LineResponse line, SectionRequest request) {
        RestAssured.given()
            .body(request)
            .pathParam("id", line.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{id}/sections")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    protected LineResponse getLine(Long lineId) {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineId)
            .when()
            .get("/lines/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .body().as(LineResponse.class)
        ;
    }
}
