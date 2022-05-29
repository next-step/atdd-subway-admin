package nextstep.subway.accept;

import static nextstep.subway.accept.LineAcceptanceTest.노선_생성;
import static nextstep.subway.accept.LineAcceptanceTest.노선_조회;
import static nextstep.subway.accept.StationAcceptanceTest.강남역;
import static nextstep.subway.accept.StationAcceptanceTest.교대역;
import static nextstep.subway.accept.StationAcceptanceTest.서초역;
import static nextstep.subway.accept.StationAcceptanceTest.양재시민의숲역;
import static nextstep.subway.accept.StationAcceptanceTest.양재역;
import static nextstep.subway.accept.StationAcceptanceTest.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
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

@DisplayName("구간 추가 기능")
@Sql(value = {"classpath:truncate_line_table.sql",
        "classpath:truncate_station_table.sql",
        "classpath:truncate_section_table.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SectionAdditionAcceptanceTest {
    @LocalServerPort
    int port;

    private StationResponse 생성된_강남역, 생성된_양재시민의숲역, 생성된_양재역, 생성된_서초역, 생성된_교대역;
    private LineResponse 생성된_신분당선, 생성된_이호선;

    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    private void saveStationAndLine() {
        생성된_강남역 = 지하철역_생성(강남역);
        생성된_양재역 = 지하철역_생성(양재역);
        생성된_양재시민의숲역 = 지하철역_생성(양재시민의숲역);
        생성된_서초역 = 지하철역_생성(서초역);
        생성된_교대역 = 지하철역_생성(교대역);

        생성된_신분당선 = 노선_생성(new LineRequest("신분당선", "bg-red-600", 생성된_강남역.getId(), 생성된_양재시민의숲역.getId(), 10L));
        생성된_이호선 = 노선_생성(new LineRequest("이호선", "bg-yellow-100", 생성된_강남역.getId(), 생성된_서초역.getId(), 20L));
    }

    /**
     * Given 라인을 만들고 노선의 역 사이에 새로운 구간 추가한다 When 해당 노선을 조회하면 Then 추가된 구간을 찾을 수 있다
     */
    @Test
    @DisplayName("역 사이에 새로운 구간 추가시 해당 역은 사이에 조회된다")
    void 역_사이에_새로운_구간_추가시_노선에서_조회가능() {
        // given
        saveStationAndLine();
        SectionRequest 강남역_양재시민의숲역_사이_추가 = new SectionRequest(생성된_강남역.getId(), 생성된_양재역.getId(), 5L);
        ExtractableResponse<Response> 구간_추가_결과 = 구간_추가(생성된_신분당선.getId(), 강남역_양재시민의숲역_사이_추가);

        // when
        LineResponse 노선_조회_결과 = 노선_조회(생성된_신분당선.getId());

        // then
        verifySizeAndStationOrder(노선_조회_결과, 생성된_강남역, 생성된_양재역, 생성된_양재시민의숲역);
    }

    /**
     * Given 라인을 만들고 노선 상행선에 새로운 구간 추가한다 When 해당 노선을 조회하면 Then 추가된 구간을 첫 번째로 찾을 수 있다
     */
    @Test
    void 상행선에_새로운_구간_추가시_노선_첫_번째에서_조회가능() {
        // given
        saveStationAndLine();
        SectionRequest 강남역_앞에_추가 = new SectionRequest(생성된_양재역.getId(), 생성된_강남역.getId(), 10L);
        ExtractableResponse<Response> 구간_추가_결과 = 구간_추가(생성된_신분당선.getId(), 강남역_앞에_추가);

        // when
        LineResponse 노선_조회_결과 = 노선_조회(생성된_신분당선.getId());

        // then
        verifySizeAndStationOrder(노선_조회_결과, 생성된_양재역, 생성된_강남역, 생성된_양재시민의숲역);
    }

    /**
     * Given 라인을 만들고 노선 하행선에 새로운 구간 추가한다 When 해당 노선을 조회하면 Then 추가된 구간을 마지막에 찾을 수 있다
     */
    @Test
    void 하행선에_새로운_구간_추가시_노선_마지막에서_조회가능() {
        // given
        saveStationAndLine();
        SectionRequest 양재시민의숲역_뒤에_추가 = new SectionRequest(생성된_양재시민의숲역.getId(), 생성된_양재역.getId(), 10L);
        ExtractableResponse<Response> 구간_추가_결과 = 구간_추가(생성된_신분당선.getId(), 양재시민의숲역_뒤에_추가);

        // when
        LineResponse 노선_조회_결과 = 노선_조회(생성된_신분당선.getId());

        // then
        verifySizeAndStationOrder(노선_조회_결과, 생성된_강남역, 생성된_양재역, 생성된_서초역);
    }

    /**
     * Given 라인을 만들고 When 기존 구간의 길이보다 같거나 큰 새로운 구간 추가하면 Then 등록할 수 없다는 에러가 발생한다
     */
    @Test
    void 역_사이_길이보다_같거나_큰_새로운_구간은_등록불가() {
        // given
        saveStationAndLine();

        // when
        SectionRequest 강남역_양재시민의숲역_사이에_같은_길이_추가 = new SectionRequest(생성된_강남역.getId(), 생성된_양재역.getId(), 10L);
        SectionRequest 강남역_양재시민의숲역_사이에_더_긴_길이_추가 = new SectionRequest(생성된_강남역.getId(), 생성된_양재역.getId(), 15L);
        ExtractableResponse<Response> 같은_길이_추가_결과 = 구간_추가(생성된_신분당선.getId(), 강남역_양재시민의숲역_사이에_같은_길이_추가);
        ExtractableResponse<Response> 더_긴_길이_추가_결과 = 구간_추가(생성된_신분당선.getId(), 강남역_양재시민의숲역_사이에_더_긴_길이_추가);

        // then
        assertAll(
                () -> assertThat(같은_길이_추가_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(더_긴_길이_추가_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    /**
     * Given 라인을 만들고 When 이미 등록된 구간을 추가하면 Then 등록할 수 없다는 에러가 발생
     */
    @Test
    void 기존에_있는_구간_추가시_등록불가() {
        // given
        saveStationAndLine();

        // when
        SectionRequest 강남역_양재시민의숲역_사이에_값은_구간_추가 = new SectionRequest(생성된_강남역.getId(), 생성된_양재시민의숲역.getId(), 1L);
        ExtractableResponse<Response> 구간_추가_결과 = 구간_추가(생성된_신분당선.getId(), 강남역_양재시민의숲역_사이에_값은_구간_추가);

        // then
        assertThat(구간_추가_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 라인을 만들고 When 상, 하행선 모두 새로운 값을 가진 새로운 구간 추가하면 Then 등록할 수 없다는 에러가 발생
     */
    @Test
    void 상_하행선_모두_해당_노선이_아닌_구간_추가시_등록불가() {
        // given
        saveStationAndLine();

        // when
        SectionRequest 해당_노선이_아닌_구간_추가 = new SectionRequest(생성된_서초역.getId(), 생성된_교대역.getId(), 5L);
        ExtractableResponse<Response> 구간_추가_결과 = 구간_추가(생성된_신분당선.getId(), 해당_노선이_아닌_구간_추가);

        // then
        assertThat(구간_추가_결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 구간_추가(Long 노선아이디, SectionRequest 구간) {
        return RestAssured.given().log().all()
                .body(구간)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{id}/sections", 노선아이디)
                .then().log().all().extract();
    }

    private void verifySizeAndStationOrder(LineResponse 노선_조회_결과, StationResponse... 예상되는_순서로_배치된_역들) {
        assertAll(
                () -> assertThat(노선_조회_결과.getStations()).hasSize(3),
                () -> assertThat(노선_조회_결과.getStations().get(0).getId()).isEqualTo(예상되는_순서로_배치된_역들[0].getId()),
                () -> assertThat(노선_조회_결과.getStations().get(1).getId()).isEqualTo(예상되는_순서로_배치된_역들[1].getId()),
                () -> assertThat(노선_조회_결과.getStations().get(2).getId()).isEqualTo(예상되는_순서로_배치된_역들[2].getId())
        );
    }
}
