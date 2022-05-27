package nextstep.subway.accept;

import static nextstep.subway.accept.StationAcceptanceTest.강남역;
import static nextstep.subway.accept.StationAcceptanceTest.서초역;
import static nextstep.subway.accept.StationAcceptanceTest.양재역;
import static nextstep.subway.accept.StationAcceptanceTest.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DisplayName("지하철 노선 관련 기능")
@Sql(value = {"classpath:truncate_station_table.sql",
        "classpath:truncate_line_table.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    private LineRequest 신분당선, 이호선, 변경할_신분당선_내용;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        saveStationsAndSettingLines();
    }

    private void saveStationsAndSettingLines() {
        final StationResponse 생성된_강남역 = 지하철역_생성(강남역);
        final StationResponse 생성된_양재역 = 지하철역_생성(양재역);
        final StationResponse 생성된_서초역 = 지하철역_생성(서초역);

        신분당선 = new LineRequest("신분당선", "bg-red-600", 생성된_강남역.getId(), 생성된_양재역.getId(), 10L);
        이호선 = new LineRequest("이호선", "bg-yellow-600", 생성된_강남역.getId(), 생성된_서초역.getId(), 5L);
        변경할_신분당선_내용 = new LineRequest("구분당선", "bg-red-100");
    }

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("노선 생성하면 해당 노선이 검색이 가능하다.")
    void 노선_생성하면_노선_목록에서_조회가능() {
        // when
        final LineResponse 생성된_신분당선 = 노선_생성(신분당선);

        // then
        final List<LineResponse> 노선_목록 = 노선_목록();
        assertAll(
                () -> assertThat(노선_목록).hasSize(1),
                () -> verifyEqualsLineResponseFields(노선_목록.get(0), 생성된_신분당선)
        );
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("여러개 노선 생성시 여러개 노선이 검색이 가능하다.")
    void 여러개_노선_생성후_노선_목록에서_해당노선들_조회가능() {
        // given
        final LineResponse 생성된_신분당선 = 노선_생성(신분당선);
        final LineResponse 생성된_이호선 = 노선_생성(이호선);

        // when
        final List<LineResponse> 조회한_노선목록 = 노선_목록();

        // then
        assertAll(
                () -> assertThat(조회한_노선목록).hasSize(2),
                () -> verifyEqualsLineResponseFields(조회한_노선목록.get(0), 생성된_신분당선),
                () -> verifyEqualsLineResponseFields(조회한_노선목록.get(1), 생성된_이호선)
        );
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("노선 생성 후 조회시 해당 노선에 대한 정보를 받는다.")
    void 노선_조회시_해당_노선정보_응답() {
        // given
        final LineResponse 생성된_신분당선 = 노선_생성(신분당선);

        // when
        final LineResponse 조회한_신분당선 = 노선_조회(생성된_신분당선.getId());

        // then
        verifyEqualsLineResponseFields(조회한_신분당선, 생성된_신분당선);
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("노선 생성 후 정보 변경시 해당 노선의 정보가 변경된다.")
    void 노선_생성후_수정시_해당_노선정보가_변경() {
        // given
        final LineResponse 생성된_신분당선 = 노선_생성(신분당선);

        // when
        ExtractableResponse<Response> 노선_수정_결과 = 노선_수정(생성된_신분당선.getId(), 변경할_신분당선_내용);
        assertThat(노선_수정_결과.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        LineResponse 변경된_신분당선 = 노선_조회(생성된_신분당선.getId());
        assertAll(
                () -> assertThat(변경된_신분당선.getName()).isEqualTo(변경할_신분당선_내용.getName()),
                () -> assertThat(변경된_신분당선.getColor()).isEqualTo(변경할_신분당선_내용.getColor())
        );
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("노선 생성 후 노선 삭제시 해당 노선의 정보는 삭제된다.")
    void 노선_생성후_삭제시_해당_노선정보_없음() {
        // given
        LineResponse 생성된_신분당선 = 노선_생성(신분당선);

        // when
        ExtractableResponse<Response> 노선_삭제_결과 = 노선_삭제(생성된_신분당선.getId());

        // then
        assertThat(노선_삭제_결과.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static LineResponse 노선_생성(LineRequest 노선) {
        return RestAssured.given().log().all()
                .body(노선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract().jsonPath().getObject("", LineResponse.class);
    }

    public static List<LineResponse> 노선_목록() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("", LineResponse.class);
    }

    public static LineResponse 노선_조회(Long 노선아이디) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + 노선아이디)
                .then().log().all()
                .extract().jsonPath().getObject("", LineResponse.class);
    }

    public static ExtractableResponse<Response> 노선_수정(Long 노선아이디, LineRequest 노선정보) {
        return RestAssured.given().log().all()
                .body(노선정보)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + 노선아이디)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 노선_삭제(Long 노선아이디) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + 노선아이디)
                .then().log().all()
                .extract();
    }

    private void verifyEqualsLineResponseFields(LineResponse lr1, LineResponse lr2) {
        assertAll(
                () -> assertThat(lr1.getId()).isEqualTo(lr2.getId()),
                () -> assertThat(lr1.getColor()).isEqualTo(lr2.getColor()),
                () -> assertThat(lr1.getName()).isEqualTo(lr2.getName()),
                () -> assertThat(lr1.getStations().get(0).getId()).isEqualTo(lr2.getStations().get(0).getId()),
                () -> assertThat(lr1.getStations().get(1).getId()).isEqualTo(lr2.getStations().get(1).getId()),
                () -> assertThat(lr1.getCreatedDate()).isEqualTo(lr2.getCreatedDate()),
                () -> assertThat(lr1.getModifiedDate()).isEqualTo(lr2.getModifiedDate())
        );
    }
}
