package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.SectionAcceptanceTest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final Line line3 = new Line("3호선", "orange");
    public static final Line line5 = new Line("5호선", "purple");

    private Station aeogaeStation;
    private Station chungjeongnoStation;
    private Station seodaemunStation;
    private Station gwanghwamunStation;

    @BeforeEach
    void setup() {
        aeogaeStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.aeogaeStation).as(Station.class);
        chungjeongnoStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.chungjeongnoStation).as(Station.class);
        seodaemunStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.seodaemunStation).as(Station.class);
        gwanghwamunStation = StationAcceptanceTest.지하철역_등록되어_있음(StationAcceptanceTest.gwanghwamunStation).as(Station.class);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(line3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(line3.getName());
        assertThat(response.jsonPath().getString("color")).isEqualTo(line3.getColor());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음(line3);

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(line3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음(line3);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음(line5);

        // when
        ExtractableResponse<Response> response = RestAssured
            .when().get("/lines")
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(line3);
        Long lineId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured
            .when().get("/lines/" + lineId)
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("id")).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(line3);

        // when
        Map<String, Object> params = new HashMap<>();
        params.put("name", line5.getName());
        params.put("color", line5.getColor());
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(createResponse.header("Location"))
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_등록되어_있음(line3);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(Line line) {
        Map<String, String> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("color", line.getColor());

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음_두_종점역_포함(Line line, Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", line.getId());
        params.put("name", line.getName());
        params.put("color", line.getColor());
        params.put("upStationId", section.getUpStation().getId());
        params.put("downStationId", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/withSection")
            .then().log().all().extract();
    }

    @DisplayName("지하철 노선 생성 시 두 종점역 추가하기")
    @Test
    void createLineWithStations() {
        // when
        Section section = new Section(aeogaeStation.getId(), gwanghwamunStation.getId(), 3000);
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음_두_종점역_포함(line5, section);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(line5.getName());
        assertThat(response.jsonPath().getString("color")).isEqualTo(line5.getColor());
        assertThat(response.jsonPath().getLong("stationResponses[0].id")).isEqualTo(section.getUpStation().getId());
        assertThat(response.jsonPath().getLong("stationResponses[1].id")).isEqualTo(section.getDownStation().getId());
    }

    @DisplayName("지하철 노선의 역 목록을 조회하기")
    @Test
    void findLineStations() {
        // given
        ExtractableResponse<Response> createLineResponse = 지하철_노선_등록되어_있음_두_종점역_포함(line5, new Section(aeogaeStation.getId(), chungjeongnoStation.getId(), 1000));
        Long lineId = createLineResponse.jsonPath().getLong("id");
        SectionAcceptanceTest.지하철_구간_등록되어_있음(new Section(lineId, chungjeongnoStation.getId(), seodaemunStation.getId(), 1000));
        SectionAcceptanceTest.지하철_구간_등록되어_있음(new Section(lineId, seodaemunStation.getId(), gwanghwamunStation.getId(), 1000));

        // when
        ExtractableResponse<Response> response = RestAssured
            .when().get("/lines/" + lineId)
            .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<StationResponse> stationResponses = response.as(LineResponse.class).getStationResponses();
        assertThat(stationResponses).isNotEmpty();
        assertThat(stationResponses.get(0)).isEqualTo(aeogaeStation.toStationResponse());
        assertThat(stationResponses.get(1)).isEqualTo(chungjeongnoStation.toStationResponse());
        assertThat(stationResponses.get(2)).isEqualTo(seodaemunStation.toStationResponse());
        assertThat(stationResponses.get(3)).isEqualTo(gwanghwamunStation.toStationResponse());
    }

}