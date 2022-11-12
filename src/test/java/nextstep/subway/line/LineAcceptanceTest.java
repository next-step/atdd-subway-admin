package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceFixture.지하철_생성_결과에서_지하철역_번호를_조회한다;
import static nextstep.subway.station.StationAcceptanceFixture.지하철_역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     **/
    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> 지하철_노선_생성결과 = 노선을_생성한다(노선_2호선_요청());

        // then
        List<String> 노선_목록 = 노선_목록을_조회한다();
        String 노선_이름 = "2호선";
        assertThat(노선_목록).contains(노선_이름);
    }



    private LineRequest 노선_2호선_요청() {
        ExtractableResponse<Response> 신도림역 = 지하철_역을_생성한다("신도림역");
        Long 지하철역_번호 = 지하철_생성_결과에서_지하철역_번호를_조회한다(신도림역);
        return new LineRequest("2호선", "Green", 지하철역_번호, 지하철역_번호, 30L);
    }


    private List<String> 노선_목록을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

    }
    private ExtractableResponse<Response> 노선을_생성한다(LineRequest 노선_요청_정보) {
        return RestAssured.given().log().all()
                        .body(노선_요청_정보)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();
    }
}
