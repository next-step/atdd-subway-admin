package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("종점역 정보를 포함한 지하철 노선을 생성한다.")
    @Test
    void createLastStopIncludeLine() {
        //given
        final StationResponse lastStopAscending = 종점역_생성("강남역");
        final StationResponse lastStopDescending = 종점역_생성("역삼역");

        //when
        final ExtractableResponse<Response> response = 종점역_정보를_포함한_지하철_노선_생성(lastStopAscending, lastStopDescending, "신분당선", "red", 10);

        //then
        //종점역_정보를_포함한_지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        String dupliecatLineName = "2호선";
        지하철_노선_생성(dupliecatLineName,"green");

        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성(dupliecatLineName, "red");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final StationResponse LineFirstUpStation = 종점역_생성("소요산");
        final StationResponse LineFirstDownStation = 종점역_생성("인천");
        final StationResponse LineThirdUpStation = 종점역_생성("대화");
        final StationResponse LineThirdDownStation = 종점역_생성("오금");
        종점역_정보를_포함한_지하철_노선_생성(LineFirstUpStation, LineFirstDownStation, "1호선", "blue", 30);
        종점역_정보를_포함한_지하철_노선_생성(LineThirdUpStation, LineThirdDownStation, "3호선", "Orange", 30);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        final List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponses.size()).isEqualTo(2);
        assertThat(lineResponses.get(0).getStations().size()).isEqualTo(2);
        assertThat(lineResponses.get(1).getStations().size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final StationResponse upStation = 종점역_생성("강남");
        final StationResponse downStation = 종점역_생성("광교");
        종점역_정보를_포함한_지하철_노선_생성(upStation, downStation, "신분당선", "red", 30);

        // when
        final ExtractableResponse<Response> lineResponse = 지하철_노선_목록_조회();

        // then
        // 지하철_노선_응답됨
        final List<LineResponse> lines = lineResponse.jsonPath().getList(".", LineResponse.class);
        assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lines.size()).isOne();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final StationResponse upStation = 종점역_생성("방화");
        final StationResponse downStation = 종점역_생성("마천");
        final ExtractableResponse<Response> lineCreateResponse = 종점역_정보를_포함한_지하철_노선_생성(upStation, downStation, "5호선", "purple", 30);
        final LineResponse lineResponse = lineCreateResponse.as(LineResponse.class);

        // when
        // 지하철_노선_수정_요청
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LineRequest("9호선", "golden"))
                .pathParam("lineId", lineResponse.getId())
                .when().put("/lines/{lineId}")
                .then().log().all().extract();

        // then
        // 지하철_노선_수정됨
        final LineResponse line = response.as(LineResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(line.getName()).isEqualTo("9호선");
        assertThat(line.getColor()).isEqualTo("golden");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final StationResponse upStation = 종점역_생성("방화");
        final StationResponse downStation = 종점역_생성("마천");
        final ExtractableResponse<Response> lineCreateResponse = 종점역_정보를_포함한_지하철_노선_생성(upStation, downStation, "5호선", "purple", 30);
        final LineResponse lineResponse = lineCreateResponse.as(LineResponse.class);

        // when
        // 지하철_노선_제거_요청
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lineId", lineResponse.getId())
                .when().delete("/lines/{lineId}")
                .then().log().all().extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 상세 조회시 노선에 속한 역 정보도 포함한다.")
    @Test
    void getIncludeStationLine() {
        //given
        final StationResponse upStation = 종점역_생성("강남");
        final StationResponse downStation = 종점역_생성("광교");
        final ExtractableResponse<Response> response = 종점역_정보를_포함한_지하철_노선_생성(upStation, downStation, "신분당선", "red", 30);

        //when
        // 지하철_노선_상세_조회
        final ExtractableResponse<Response> lineNo = RestAssured.given().log().all()
                .when()
                .pathParam("lineNo", response.as(LineResponse.class).getId())
                .get("/lines/{lineNo}")
                .then().log().all().extract();

        //then
        // 지하철_노선_응답됨
        // 노선에_속한_역_정보_상행_하행_정렬_되어_응답
        final LineResponse lineResponses = lineNo.as(LineResponse.class);
        assertThat(lineNo.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponses.getStations()).extracting("name").containsExactly("강남","광교");

    }


    private ExtractableResponse<Response> 종점역_정보를_포함한_지하철_노선_생성(final StationResponse lastStopAscending, final StationResponse lastStopDescending, String lineName, String lineColor, int sectionDistance) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LineRequest(lineName, lineColor, lastStopAscending.getId(), lastStopDescending.getId(), sectionDistance))
                .when().post("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_생성(String name, String color) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LineRequest(name, color))
                .when().post("/lines")
                .then().log().all().extract();
    }

    private StationResponse 종점역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return response.as(StationResponse.class);
    }
}
