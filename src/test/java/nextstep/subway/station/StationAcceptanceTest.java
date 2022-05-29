package nextstep.subway.station;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.dto.StationResponses;
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

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();
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
        ValidatableResponse createResponse = 지하철역_등록("강남역");

        // then
        응답_검증(createResponse, HttpStatus.CREATED);

        // then
        StationResponses responses = new StationResponses(목록_조회("/stations", StationResponse.class));
        개수_검증(responses.getList(), 1);
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
        지하철역_등록("강남역");

        // when
        ValidatableResponse response = 지하철역_등록("강남역");

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
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
        지하철역_등록("잠실역");
        지하철역_등록("강남역");

        // when
        StationResponses responses = new StationResponses(목록_조회("/stations", StationResponse.class));

        // then
        개수_검증(responses.getList(), 2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ValidatableResponse response = 지하철역_등록("강남역");
        StationResponse createdStation = 응답_객체_생성(response, StationResponse.class);

        // when
        삭제("/stations", createdStation.getId());

        // then
        StationResponses responses = new StationResponses(목록_조회("/stations", StationResponse.class));
        개수_검증(responses.getList(), 0);
    }

    public static ValidatableResponse 지하철역_등록(String name) {
        StationRequest request = new StationRequest(name);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all();
    }

    public static void 응답_검증(ValidatableResponse response, HttpStatus status) {
        assertThat(response.extract().statusCode()).isEqualTo(status.value());
    }

    public static <T> List<T> 목록_조회(String path, Class<T> clazz) {
        ValidatableResponse listResponse = RestAssured.given().log().all()
                .when().get(path)
                .then().log().all();
        return getJsonPathForResponse(listResponse).getList("$", clazz);
    }

    public static <T> T 응답_객체_생성(ValidatableResponse createResponse, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL);

        String body = createResponse.extract().body().asString();
        try {
            return mapper.readValue(body, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> void 개수_검증(List<T> list, int size) {
        assertThat(list).hasSize(size);
    }

    public static void 삭제(String path, long deleteId) {
        RestAssured.given().log().all()
                .pathParam("id", deleteId)
                .when().delete(path + "/{id}")
                .then().log().all();
    }

    public static JsonPath getJsonPathForResponse(ValidatableResponse response) {
        return response.extract().jsonPath();
    }
}
