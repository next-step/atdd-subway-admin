package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 섹션등록 인수테스트")
public class LineSectionAcceptanceTest extends AcceptanceTest{

    private StationResponse stationResponse1;
    private StationResponse stationResponse2;
    private StationResponse stationResponse3;
    private StationResponse stationResponse4;

    @BeforeEach
    public void setUp() {
        super.setUp();
        stationResponse1 = createStation("강남역").jsonPath().getObject(".", StationResponse.class);
        stationResponse2 = createStation("역삼역").jsonPath().getObject(".", StationResponse.class);
        stationResponse3 = createStation("선릉역").jsonPath().getObject(".", StationResponse.class);
        stationResponse4 = createStation("삼성역").jsonPath().getObject(".", StationResponse.class);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse2.getId(), 10);
        ExtractableResponse<Response> response = createSubwayLineSection(lineRequest);
        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선 구간추가를 한다.(상행역 동일)")
    @Test
    void addLineSection1() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse3.getId(), 10);
        ExtractableResponse<Response> response = createSubwayLineSection(lineRequest);

        String lineId = response.header("Location").split("/")[2];

        SectionRequest sectionRequest = new SectionRequest(stationResponse1.getId(), stationResponse2.getId(), 4);
        addSubwayLineSection(lineId, sectionRequest);

        // 지하철_구간_목록_조회_요청
        LineResponse lineResponse = getSubwayLine(response).jsonPath().getObject(".", LineResponse.class);

        // then
        // 지하철_구간_추가되고 정렬되어 조회됨
        assertThat(lineResponse.getStations()).extracting("name").containsExactly(stationResponse1.getName(), stationResponse2.getName(), stationResponse3.getName());
    }

    @DisplayName("지하철 노선 구간추가를 한다.(하행역 동일)")
    @Test
    void addLineSection2() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse3.getId(), 10);
        ExtractableResponse<Response> response = createSubwayLineSection(lineRequest);

        String lineId = response.header("Location").split("/")[2];

        SectionRequest sectionRequest = new SectionRequest(stationResponse2.getId(), stationResponse3.getId(), 4);
        addSubwayLineSection(lineId, sectionRequest);

        // 지하철_구간_목록_조회_요청
        LineResponse lineResponse = getSubwayLine(response).jsonPath().getObject(".", LineResponse.class);

        // then
        // 지하철_구간_추가되고 정렬되어 조회됨
        assertThat(lineResponse.getStations()).extracting("name").containsExactly(stationResponse1.getName(), stationResponse2.getName(), stationResponse3.getName());
    }
    @DisplayName("지하철 노선 구간추가를 한다.(상행역 신규 종점)")
    @Test
    void addLineSection3() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse2.getId(), stationResponse3.getId(), 10);
        ExtractableResponse<Response> response = createSubwayLineSection(lineRequest);

        String lineId = response.header("Location").split("/")[2];

        SectionRequest sectionRequest = new SectionRequest(stationResponse1.getId(), stationResponse2.getId(), 11);
        addSubwayLineSection(lineId, sectionRequest);

        // 지하철_구간_목록_조회_요청
        LineResponse lineResponse = getSubwayLine(response).jsonPath().getObject(".", LineResponse.class);

        // then
        // 지하철_구간_추가되고 정렬되어 조회됨
        assertThat(lineResponse.getStations()).extracting("name").containsExactly(stationResponse1.getName(), stationResponse2.getName(), stationResponse3.getName());
    }

    @DisplayName("지하철 노선 구간추가를 한다.(하행역 신규 종점)")
    @Test
    void addLineSection4() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse2.getId(), 10);
        ExtractableResponse<Response> response = createSubwayLineSection(lineRequest);

        String lineId = response.header("Location").split("/")[2];

        SectionRequest sectionRequest = new SectionRequest(stationResponse2.getId(), stationResponse3.getId(), 11);
        addSubwayLineSection(lineId, sectionRequest);

        // 지하철_구간_목록_조회_요청
        LineResponse lineResponse = getSubwayLine(response).jsonPath().getObject(".", LineResponse.class);

        // then
        // 지하철_구간_추가되고 정렬되어 조회됨
        assertThat(lineResponse.getStations()).extracting("name").containsExactly(stationResponse1.getName(), stationResponse2.getName(), stationResponse3.getName());
    }

    @DisplayName("지하철 노선 구간추가를 한다.(신규 구간이 더 길 경우 400 에러)")
    @Test
    void addLineSection_Error1() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse3.getId(), 10);
        ExtractableResponse<Response> response = createSubwayLineSection(lineRequest);

        // 지하철_구간_생성_요청
        String lineId = response.header("Location").split("/")[2];

        SectionRequest sectionRequest = new SectionRequest(stationResponse2.getId(), stationResponse3.getId(), 11);
        ExtractableResponse<Response> createResponse = addSubwayLineSection(lineId, sectionRequest);
        // then
        // BAD_REQUEST(400) ERROR 발생
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 구간추가를 한다.(기존에 등록된 구간에 대해서 중복으로 등록 요청할 경우 400 에러)")
    @Test
    void addLineSection_Error2() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse3.getId(), 10);
        ExtractableResponse<Response> response = createSubwayLineSection(lineRequest);

        // 지하철_구간_생성_요청
        String lineId = response.header("Location").split("/")[2];

        SectionRequest sectionRequest = new SectionRequest(stationResponse1.getId(), stationResponse3.getId(), 11);
        ExtractableResponse<Response> createResponse = addSubwayLineSection(lineId, sectionRequest);
        // then
        // BAD_REQUEST(400) ERROR 발생
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 구간추가를 한다.(상행, 하행역 중 하나라도 등록이 되어있지 않을 경우 400 에러)")
    @Test
    void addLineSection_Error3() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse3.getId(), 10);
        ExtractableResponse<Response> response = createSubwayLineSection(lineRequest);

        // 지하철_구간_생성_요청
        String lineId = response.header("Location").split("/")[2];

        SectionRequest sectionRequest = new SectionRequest(stationResponse2.getId(), stationResponse4.getId(), 11);
        ExtractableResponse<Response> createResponse = addSubwayLineSection(lineId, sectionRequest);
        // then
        // BAD_REQUEST(400) ERROR 발생
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 모든 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // when
        LineRequest lineRequest = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse2.getId(), 10);
        createSubwayLineSection(lineRequest);

        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = getSubwayLineList();
        // then
        // 지하철_노선_목록_조회됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        assertThat(lineResponses.stream().anyMatch(lineResponse -> lineResponse.getName().equals("2호선"))).isEqualTo(true);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLine() {
        // when
        LineRequest lineRequest1  = new LineRequest("2호선", "초록", stationResponse1.getId(), stationResponse2.getId(), 10);
        ExtractableResponse<Response> createResponse1 = createSubwayLineSection(lineRequest1);
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = getSubwayLine(createResponse1);
        // then
        // 지하철_노선_목록_조회됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName().equals("2호선")).isEqualTo(true);
        assertThat(lineResponse.getStations().contains(stationResponse1)).isEqualTo(true);

    }

    private ExtractableResponse<Response> createStation(String name) {
        StationRequest stationRequest = new StationRequest(name);

        return RestAssured.given()
                .log().all()
                .when()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/stations")
                .then().log().all()
                .extract();
    }


    private ExtractableResponse<Response> createSubwayLineSection(LineRequest lineRequest) {

        return RestAssured.given()
                .log().all()
                .when()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> addSubwayLineSection(String lineId, SectionRequest sectionRequest) {

        return RestAssured.given()
                .log().all()
                .when()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getSubwayLineList() {

        return RestAssured.given()
                .log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/lines")
                .then().log().all()
                .extract();

    }

    private ExtractableResponse<Response> getSubwayLine(ExtractableResponse<Response> createResponse) {

        String lineId = createResponse.header("Location").split("/")[2];

        return RestAssured.given()
                .log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("/lines/"+lineId)
                .then().log().all()
                .extract();
    }

}
