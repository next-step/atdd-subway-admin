package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

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
    void requestCreateStation() {
        // when
        String[] result = Stream.of("강남역")
                .map(stationName -> requestCreateStation(stationName, HttpStatus.CREATED).getName())
                .toArray(String[]::new);

        assertThat(result).contains("강남역");
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
        final String creatStationName = "강남역";
        Stream.of(creatStationName).forEach(stationName -> requestCreateStation(stationName, HttpStatus.CREATED));
        Stream.of(creatStationName).forEach(stationName -> requestCreateStation(stationName, HttpStatus.BAD_REQUEST));

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
        Station[] createdStation = Stream.of("지하철역이름", "새로운지하철역이름", "또다른지하철역이름")
                .map(stationName -> requestCreateStation(stationName, HttpStatus.CREATED))
                .distinct()
                .toArray(Station[]::new);

        // when
        Station[] stations = RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().as(Station[].class);


        //then
        assertThat(stations).containsExactly(createdStation);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
    }

    private  Station requestCreateStation(final String stationName, final HttpStatus httpStatus) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all().extract();

        assertThat(HttpStatus.valueOf(response.statusCode())).isEqualTo(httpStatus);

        return httpStatus == HttpStatus.CREATED ? response.as(Station.class) : new Station();
    }
}
