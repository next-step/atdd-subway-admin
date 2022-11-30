package nextstep.subway.Section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.constants.ErrorCode;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("지하철 노선 관련 기능")
public class SectionsAcceptanceTest extends AcceptanceTest {
    private final long DEFAULT_DISTANCE = 10;
    private StationResponse 강남역;
    private StationResponse 광교역;
    private StationResponse 정자역;
    private StationResponse 광교중앙역;
    private LineResponse 신분당선;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        강남역 = registerStation("강남역").as(StationResponse.class);
        광교역 = registerStation("광교역").as(StationResponse.class);
        정자역 = registerStation("정자역").as(StationResponse.class);
        광교중앙역 = registerStation("광교중앙역").as(StationResponse.class);
        신분당선 = registerLine("신분당선", "red", 강남역.getId(), 광교역.getId(), DEFAULT_DISTANCE).as(LineResponse.class);
    }

    @DisplayName("지하철 노선을 조회하고 구간(Section) 추가 요청")
    @Test
    void addSectionWhenIsSameUpStation() {
        Line line = findLine("신분당선");
        ExtractableResponse<Response> response =
                addSection(
                        generateSectionRequest(강남역.getId(), 정자역.getId(), 4), line.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    // "Section 추가 시 하행이 같을 때"
    // "Section 추가 시 상행이 같을 때 역간 거리 예외"
    // "Section 추가 시 하행이 같을 때 역간 거리 예외"
    // "상행 종점에 추가할 때"
    // "하행 종점에 추가할 때"

    private ExtractableResponse<Response> addSection(SectionRequest sectionRequest, long id) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines" + DELIMITER + id + "/sections")
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> registerStation(String name) {
        return RestAssured.given().log().all()
                .body(generateStationRequest(name))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> registerLine(String name, String color, long upStationId, long downStationId, long distance) {
        return RestAssured.given().log().all()
                .body(generateLineRequest(name, color, upStationId, downStationId, distance))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private SectionRequest generateSectionRequest(long upStationId, long downStationId, long distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }

}
