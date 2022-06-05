package nextstep.subway.section;

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
import org.springframework.http.HttpStatus;

import static nextstep.subway.common.Messages.DUPLICATE_SECTION_ERROR;
import static nextstep.subway.common.Messages.NOT_MATCH_STATION_ERROR;
import static nextstep.subway.domain.Section.DISTANCE_LENGTH_ERROR;
import static nextstep.subway.utils.AcceptanceApiFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노션 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private int 강남역_ID;
    private int 교대역_ID;
    private int 서초역_ID;
    private int 역삼역_ID;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.clean();

        강남역_ID = 지하철역_생성("강남역").jsonPath().getInt("id");
        교대역_ID = 지하철역_생성("교대역").jsonPath().getInt("id");
        서초역_ID = 지하철역_생성("서초역").jsonPath().getInt("id");
        역삼역_ID = 지하철역_생성("역삼역").jsonPath().getInt("id");
    }


    @Test
    @DisplayName("역사이에 역을 신규로 상행으로 등록하는 경우 테스트")
    void newStationBetweenTheStations() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 교대역_ID, 역삼역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long 지하철노선_ID = 지하철노선_생성_결과.jsonPath().getLong("id");

        ExtractableResponse<Response> 지하철노선_구간_등록_결과 = 지하철노선_구간_등록(지하철노선_ID, 교대역_ID, 강남역_ID, 5);

        assertAll(
                () -> assertThat(지하철노선_구간_등록_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(지하철노선_구간_등록_결과.jsonPath().getList("stations.name", String.class))
                        .containsExactly("교대역", "강남역", "역삼역")
        );
    }

    @Test
    @DisplayName("역사이에 역을 신규로 하행으로 등록하는 경우 테스트")
    void newStationBetweenTheStations2() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 교대역_ID, 역삼역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long 지하철노선_ID = 지하철노선_생성_결과.jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철노선_구간_등록_결과 = 지하철노선_구간_등록(지하철노선_ID, 강남역_ID, 역삼역_ID, 5);

        assertAll(
                () -> assertThat(지하철노선_구간_등록_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(지하철노선_구간_등록_결과.jsonPath().getList("stations.name", String.class))
                        .containsExactly("교대역", "강남역", "역삼역")
        );
    }

    @Test
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우 테스트")
    void newStationUpLastStation() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 교대역_ID, 강남역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long 지하철노선_ID = 지하철노선_생성_결과.jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철노선_구간_등록_결과 = 지하철노선_구간_등록(지하철노선_ID, 서초역_ID, 교대역_ID, 3);

        assertAll(
                () -> assertThat(지하철노선_구간_등록_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(지하철노선_구간_등록_결과.jsonPath().getList("stations.name", String.class))
                        .containsExactly("서초역", "교대역", "강남역")
        );
    }

    @Test
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우 테스트")
    void newStationDownLastStation() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 교대역_ID, 강남역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long 지하철노선_ID = 지하철노선_생성_결과.jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철노선_구간_등록_결과 = 지하철노선_구간_등록(지하철노선_ID, 강남역_ID, 역삼역_ID, 15);

        assertAll(
                () -> assertThat(지하철노선_구간_등록_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(지하철노선_구간_등록_결과.jsonPath().getList("stations.name", String.class))
                        .containsExactly("교대역", "강남역", "역삼역")
        );
    }

    @Test
    @DisplayName("새로운 역을 등록시 기존역보다 길이가 긴 경우 테스트")
    void newStationOverDistance() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 교대역_ID, 역삼역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long 지하철노선_ID = 지하철노선_생성_결과.jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철노선_구간_등록_결과 = 지하철노선_구간_등록(지하철노선_ID, 교대역_ID, 강남역_ID, 100);

        assertAll(
                () -> assertThat(지하철노선_구간_등록_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(지하철노선_구간_등록_결과.jsonPath().getString("message")).isEqualTo(DISTANCE_LENGTH_ERROR)
        );
    }

    @Test
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록된 경우 테스트")
    void newStationDuplicate() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 교대역_ID, 역삼역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Long 지하철노선_ID = 지하철노선_생성_결과.jsonPath().getLong("id");
        ExtractableResponse<Response> 지하철노선_구간_등록_결과 = 지하철노선_구간_등록(지하철노선_ID, 교대역_ID, 역삼역_ID, 5);

        assertAll(
                () -> assertThat(지하철노선_구간_등록_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(지하철노선_구간_등록_결과.jsonPath().getString("message")).isEqualTo(DUPLICATE_SECTION_ERROR)
        );
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가 불가 테스트")
    void notMatchStation() {
        ExtractableResponse<Response> 지하철노선_생성_결과 = 지하철노선_생성("2호선", "bg-green-600", 교대역_ID, 강남역_ID);
        assertThat(지하철노선_생성_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long 지하철노선_ID = 지하철노선_생성_결과.jsonPath().getLong("id");

        ExtractableResponse<Response> 지하철노선_구간_등록_결과 = 지하철노선_구간_등록(지하철노선_ID, 서초역_ID, 교대역_ID, 3);
        assertThat(지하철노선_구간_등록_결과.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        int 남부터미널역_ID = 지하철역_생성("남부터미널역").jsonPath().getInt("id");
        int 양재역_ID = 지하철역_생성("양재역").jsonPath().getInt("id");

        ExtractableResponse<Response> 지하철노선_미포함_등록_결과 = 지하철노선_구간_등록(지하철노선_ID, 남부터미널역_ID, 양재역_ID, 3);

        assertAll(
                () -> assertThat(지하철노선_미포함_등록_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(지하철노선_미포함_등록_결과.jsonPath().getString("message")).isEqualTo(NOT_MATCH_STATION_ERROR)
        );
    }
}
