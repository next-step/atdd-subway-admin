package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static nextstep.subway.line.LineAcceptanceTest.지하철노선_조회_요청;
import static nextstep.subway.station.StationAcceptanceTest.응답코드_확인;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.line.TestLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@Sql("/truncate.sql")
@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    int port;
    public static Map<String, String> params = new HashMap<>();


    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    //   * Given : 지하철 노선 생성하고
    //    * When : 기존 노선에 있는 상행역과 신규 하행역을 사이에 추가하면
    //    * Then : 추가된 노선을 사이에 포함하여 차례되로 조회되는 것을 볼 수 있다.
    @DisplayName("노선의 사이에 새로운 역 구간을 등록한다.")
    @Test
    void addSectionInMiddle() {
        //Given : 지하철 노선 생성하고
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        지하철역_생성_요청("양재시민의숲역");
        // when
        // 기존 노선에 있는 상행역과 신규 하행역을 사이에 추가하면
        지하철구간_추가_요청(1L, 3L, 5L, 신분당선.jsonPath().getLong("id"));
        // then
        // 추가된 노선을 사이에 포함하여 차례되로 조회되는 것을 볼 수 있다.
        ExtractableResponse<Response> 지하철노선_조회_요청 = 지하철노선_조회_요청(신분당선.jsonPath().getLong("id"));
        지하철역_순서_확인(지하철노선_조회_요청, Arrays.asList("강남역", "양재시민의숲역", "판교역"));
    }

    // * Given : 지하철 노선 생성하고
    // * When : 신규 역을 상행 종점으로 하고, 기존의 상행 종점을 하행 역으로 추가하면
    // * Then : 추가된 노선이 상행 종점이 된 상태로 차례대로 조회된다.
    @DisplayName("노선의 상행종점으로 새로운 역구간을 등록한다.")
    @Test
    void addSectionAtFirst() {
        //Given : 지하철 노선 생성하고
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        지하철역_생성_요청("신사역");
        // when
        // 신규 역을 상행 종점으로 하고, 기존의 상행 종점을 하행 역으로 추가하면
        지하철구간_추가_요청(3L, 1L, 5L, 신분당선.jsonPath().getLong("id"));
        // then
        // 추가된 노선이 상행 종점이 된 상태로 차례대로 조회된다.
        ExtractableResponse<Response> 지하철노선_조회_요청 = 지하철노선_조회_요청(신분당선.jsonPath().getLong("id"));
        지하철역_순서_확인(지하철노선_조회_요청, Arrays.asList("신사역", "강남역", "판교역"));
    }

    // * Given : 지하철 노선 생성하고
    // * When : 신규 역을 하행 종점으로 하고, 기존의 하행 종점을 상행 역으로 추가하면
    // * Then : 추가된 노선이 하행 종점이 된 상태로 차례대로 조회된다.
    @DisplayName("노선의 하행종점으로 새로운 역구간을 등록한다.")
    @Test
    void addSectionAtLast() {
        //Given : 지하철 노선 생성하고
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        지하철역_생성_요청("광교역");
        // when
        // 신규 역을 하행 종점으로 하고, 기존의 하행 종점을 상행 역으로 추가하면
        지하철구간_추가_요청(2L, 3L, 5L, 신분당선.jsonPath().getLong("id"));
        // then
        // 추가된 노선이 하행 종점이 된 상태로 차례대로 조회된다.
        ExtractableResponse<Response> 지하철노선_조회_요청 = 지하철노선_조회_요청(신분당선.jsonPath().getLong("id"));
        지하철역_순서_확인(지하철노선_조회_요청, Arrays.asList("강남역", "판교역", "광교역"));
    }

    // * Given : 지하철 노선 생성하고
    // * When : 기존 노선에 있는 상행역과 신규 하행역을 사이에 추가하는데, 거리를 기존 노선보다 길거나 같게 하면
    // * Then : 등록되지 않고 에러 발생
    @DisplayName("이미 등록된 지하철역을 신규 구간으로 추가한다.")
    @Test
    void addSectionWithLongerDistance() {
        //Given : 지하철 노선 생성하고
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        지하철역_생성_요청("양재시민의숲역");
        // when
        // 기존 노선에 있는 상행역과 신규 하행역을 사이에 추가하는데, 거리를 기존 노선보다 길거나 같게 하면
        ExtractableResponse<Response> 지하철구간_추가_요청 = 지하철구간_추가_요청(1L, 3L, 10L,
            신분당선.jsonPath().getLong("id"));
        // then
        // 등록되지 않고 에러 발생
        응답코드_확인(지하철구간_추가_요청, HttpStatus.BAD_REQUEST);

    }

    // * Given : 지하철 노선 생성하고
    // * When : 이미 노선에 등록이 되어있는 역을 상행역과 하행역으로 하면
    // * Then : 등록되지 않고 에러 발생
    @DisplayName("등록된 노선의 길이보다 길게 새 구간을 추가한다.")
    @Test
    void addSectionAlreadyExist() {
        //Given : 지하철 노선 생성하고
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        // when
        // 이미 노선에 등록이 되어있는 역을 상행역과 하행역으로 하면
        ExtractableResponse<Response> 지하철구간_추가_요청 = 지하철구간_추가_요청(1L, 2L, 10L,
            신분당선.jsonPath().getLong("id"));
        // then
        // 등록되지 않고 에러 발생
        응답코드_확인(지하철구간_추가_요청, HttpStatus.BAD_REQUEST);

    }

    // * Given : 지하철 노선 생성하고
    // * When :  노선에 없는 역을 상행역과 하행역으로 하면
    // * Then : 등록되지 않고 에러 발생
    @DisplayName("노선에 존재하지 않는 역을 구간으로 등록한다.")
    @Test
    void addSectionNotExistStation() {
        //Given : 지하철 노선 생성하고
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        지하철역_생성_요청("광교역");
        지하철역_생성_요청("수지구청역");
        // when
        // 이미 노선에 등록이 되어있는 역을 상행역과 하행역으로 하면
        ExtractableResponse<Response> 지하철구간_추가_요청 = 지하철구간_추가_요청(3L, 4L, 10L,
            신분당선.jsonPath().getLong("id"));
        // then
        // 등록되지 않고 에러 발생
        응답코드_확인(지하철구간_추가_요청, HttpStatus.BAD_REQUEST);
    }


    //* Given : 지하철 노선 생성하고, 구간을 추가한다.
    //* When : 노선의 상행 종점역을 삭제한다.
    //* Then : 다음으로 오던 역이 상행 종점역이 된 순서로 조회된다.
    @DisplayName("지하철 노선의 상행 종점 역을 삭제한다.")
    @Test
    void deleteSection() {
        //Given : 지하철 노선 생성하고, 구간을 추가한다.
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록되어_있음(TestLine.SHINBUNDANG);
        지하철역_생성_요청("양재시민의숲역");
        지하철구간_추가_요청(1L, 3L, 5L, 신분당선.jsonPath().getLong("id"));
        // when
        // 노선의 상행 종점역을 삭제한다.
        지하철구간_삭제_요청(1L, 신분당선.jsonPath().getLong("id"));
        // then
        // 다음으로 오던 역이 상행 종점역이 된 순서로 조회된다.
        ExtractableResponse<Response> 지하철노선_조회_요청 = 지하철노선_조회_요청(신분당선.jsonPath().getLong("id"));
        지하철역_순서_확인(지하철노선_조회_요청, Arrays.asList("양재시민의숲역", "판교역"));
    }

    private void 지하철역_순서_확인(ExtractableResponse<Response> getResponse, List<String> stationNames) {
        assertThat(getResponse.jsonPath().getList("stations.name")).hasSameElementsAs(stationNames);
    }

    private static ExtractableResponse<Response> 지하철구간_추가_요청(Long upStationId,
        Long downStationId, Long distance, Long lineId) {
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
            .body(params)
            .pathParam("lineId", lineId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }

    private static ExtractableResponse<Response> 지하철구간_삭제_요청(Long stationId, Long lineId) {

        return RestAssured.given().log().all()
            .pathParam("lineId", lineId)
            .param("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/{lineId}/sections")
            .then().log().all()
            .extract();
    }
}
