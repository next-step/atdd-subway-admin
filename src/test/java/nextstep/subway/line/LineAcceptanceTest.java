package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseTest;
import nextstep.subway.domain.line.LineStation;
import nextstep.subway.domain.line.LineStationRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.request.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("지하철 노선 테스트")
public class LineAcceptanceTest extends BaseTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineStationRepository lineStationRepository;

    @BeforeEach
    void setUp() {
        Station 강남역 = 지하철역_생성("강남역");
        Station 서초역 = 지하철역_생성("서초역");

        지하철역_구간_생성(강남역, 서초역, 7);
    }

    private Station 지하철역_생성(String stationName) {
        return stationRepository.save(new Station(stationName));
    }

    public LineStation 지하철역_구간_생성(Station upStation, Station downStation, int distance) {
        return lineStationRepository.save( new LineStation(upStation.getId(), downStation.getId(), distance) );
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @Test
    @DisplayName("지하철 노선도 생성 테스트")
    public void crete_line_test() {
        // Given
        LineRequest lineRequest = new LineRequest("신분당선", "red", Long.valueOf(1), Long.valueOf(2), 5);

        // When
        ExtractableResponse<Response> response = reqeust_register_line(lineRequest);
        String stationName = 지하철역_노선_이름_가져옴(response);

        // Then
        assertThat(stationName).isEqualTo("신분당선");
    }

    private String 지하철역_노선_이름_가져옴(ExtractableResponse response) {
        return response.jsonPath()
                .get("name");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선으로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @Test
    @DisplayName("지하철 노선도 생성 실패 테스트")
    public void crete_line_with_duplicate_name_test() {
        // Given
        LineRequest lineRequest = new LineRequest("신분당선", "red", Long.valueOf(1), Long.valueOf(2), 5);

        // When
        reqeust_register_line(lineRequest);
        ExtractableResponse<Response> response = reqeust_register_line(lineRequest);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private List<String> reqeust_get_line_names() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name");
    }

    private ExtractableResponse<Response> reqeust_register_line(LineRequest lineRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());
        params.put("upStationId", lineRequest.getUpStationId());
        params.put("downStationId", lineRequest.getDownStationId());
        params.put("distance", lineRequest.getDistance());

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선도 목록 조회 테스트")
    public void get_lines_test() {
        // Given
        LineRequest firstLineRequest = new LineRequest("신분당선", "red", Long.valueOf(1), Long.valueOf(2), 5);
        LineRequest secondLineRequest = new LineRequest("분당선", "green", Long.valueOf(1), Long.valueOf(2), 5);

        // When
        reqeust_register_line(firstLineRequest);
        reqeust_register_line(secondLineRequest);
        List<String> lineNames = reqeust_get_line_names();

        // Then
        assertThat(lineNames).contains("신분당선", "분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 조회 테스트")
    public void get_line_test() {
        // Given
        LineRequest lineRequest = new LineRequest("신분당선", "red", Long.valueOf(1), Long.valueOf(2), 5);

        // When
        ExtractableResponse<Response> createLine = reqeust_register_line(lineRequest);
        ExtractableResponse response = LineAcceptanceSupport.지하철_노선_조회(createLine.jsonPath().get("id").toString());

        // Then
        assertThat(지하철역_노선_이름_가져옴(response)).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    @DisplayName("지하철 노선 수정 테스트")
    public void update_line_test() {
        // Given
        LineRequest firstLineRequest = new LineRequest("신분당선", "red", Long.valueOf(1), Long.valueOf(2), 5);
        LineRequest updateLineRequest = new LineRequest("6호선", "green", Long.valueOf(1), Long.valueOf(2), 5);

        // When
        ExtractableResponse<Response> createLine = reqeust_register_line(firstLineRequest);
        ExtractableResponse response = LineAcceptanceSupport.지하철_노선_수정(createLine, updateLineRequest);

        // Then
        assertThat(지하철역_노선_이름_가져옴(response)).isEqualTo("6호선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    @DisplayName("지하철 노선 삭제 테스트")
    public void delete_line_test() {
        // Given
        LineRequest firstLineRequest = new LineRequest("신분당선", "red", Long.valueOf(1), Long.valueOf(2), 5);

        // When
        ExtractableResponse<Response> createLine = reqeust_register_line(firstLineRequest);
        ExtractableResponse<Response> response = LineAcceptanceSupport.지하철_노선_삭제(createLine);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
