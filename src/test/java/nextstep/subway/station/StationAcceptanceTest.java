package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;


import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    private static final String INVALID_KEY = "name";
    private static final Function<RequestSpecification, Response> CREATE = requestSpecification -> requestSpecification.post("/stations");
    private static final Function<RequestSpecification, Response> SEARCH_ALL = requestSpecification -> requestSpecification.get("/stations");

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
    void request() {
        // when
        final String creatStationName = "강남역";

        String[] result = Stream.of(creatStationName)
                .map(stationName -> request(()->makeBody(INVALID_KEY, stationName), CREATE, HttpStatus.CREATED)
                        .as(Station.class).getName())
                .toArray(String[]::new);

        //Then
        assertThat(result).contains(creatStationName);
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
        //when
        Stream.of(creatStationName).forEach(stationName -> request(()->makeBody(INVALID_KEY, stationName), CREATE, HttpStatus.CREATED));
        //Then
        Stream.of(creatStationName).forEach(stationName -> request(()->makeBody(INVALID_KEY, stationName), CREATE, HttpStatus.BAD_REQUEST));

    }

    /**
     * When 잘못된 Key 값을 전달하면
     * Then 400 Bad Request 를 전달 받는다.
     */
    @DisplayName("잘못된 입력 값을 전달 하면 Bad Request 를 받는다")
    @Test
    void invalidParameter() {
        // given
        final String creatStationName = "강남역";

        //Then
        Stream.of(creatStationName).forEach(stationName -> request(()->makeBody("bad", stationName), CREATE, HttpStatus.BAD_REQUEST));
        Stream.of("").forEach(stationName -> request(()->makeBody(INVALID_KEY, stationName), CREATE, HttpStatus.BAD_REQUEST));
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
                .map(stationName -> request(() -> makeBody(INVALID_KEY, stationName), CREATE, HttpStatus.CREATED).as(Station.class))
                .toArray(Station[]::new);

        // when
        Station[] stations = request(HashMap::new, SEARCH_ALL, HttpStatus.OK).as(Station[].class);
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
        final String creatStationName = "강남역";
        Station createStation = Stream.of(creatStationName)
                .map(stationName -> request(() -> makeBody(INVALID_KEY, stationName), CREATE, HttpStatus.CREATED).as(Station.class))
                .findAny().get();

        final Function<RequestSpecification, Response> DELETE = requestSpecification -> requestSpecification.delete(String.format("/stations/%d",createStation.getId()));
        request(HashMap::new, DELETE, HttpStatus.NO_CONTENT);
    }

    /**
     * When 없는 지하철역을 삭제하면
     * Then 400 Bad Request 를 전달 받는다.
     */
    @DisplayName("지하철 역 정보가 없는 경우 삭제 요청 시 400 Bad Request 를 반환 한다.")
    @Test
    void noContentDeleteStation() {
        final Function<RequestSpecification, Response> DELETE = requestSpecification -> requestSpecification.delete(String.format("/stations/%d",1));
        request(HashMap::new, DELETE, HttpStatus.BAD_REQUEST);
    }


    private Map<String, String> makeBody(final String key, final String value) {
        HashMap<String, String> input = new HashMap<>();
        input.put(key, value);
        return input;
    }

    private ExtractableResponse<Response> request(Supplier<Map<String,String>> supplier, Function<RequestSpecification, Response> function, final HttpStatus expectedStatus) {
        ExtractableResponse<Response> response = function.apply(
                RestAssured.given().log().all()
                        .body(supplier.get())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
        ).then().log().all().extract();
        assertThat(HttpStatus.valueOf(response.statusCode())).isEqualTo(expectedStatus);
        return response;
    }
}
