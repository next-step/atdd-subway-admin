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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @DisplayName("구간(Section) 추가 테스트(상행과 일치)")
    @Test
    void addSectionWhenIsSameUpStation() {
        ExtractableResponse<Response> response =
                addSection(generateSectionRequest(강남역.getId(), 정자역.getId(), 4), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간(Section) 추가 테스트(하행과 일치)")
    @Test
    void addSectionWhenIsSameDownStation() {
        ExtractableResponse<Response> response =
                addSection(generateSectionRequest(정자역.getId(), 광교역.getId(), 4), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간(Section) 추가 시 구간 길이로 인한 예외 테스트(하행과 일치)")
    @Test
    void makeExceptionToAddSectionWhenIsSameDownStationDistanceException() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(정자역.getId(), 광교역.getId(), DEFAULT_DISTANCE), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간(Section) 추가 시 구간 길이로 인한 예외 테스트(상행과 일치)")
    @Test
    void makeExceptionToAddSectionWhenIsSameUpStationDistanceException() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 광교역.getId(), DEFAULT_DISTANCE), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간(Section) 추가 시 일치하는 Station 없을 시 예외")
    @Test
    void makeExceptionWhenNoMatchBothStation() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(광교중앙역.getId(), 정자역.getId(), 5), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("구간(Section) 추가 시 이미 있는 Section인 경우 예외")
    @Test
    void makeExceptionWhenAddSameSection() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 광교역.getId(), 5), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("상행 종점 추가 테스트 ")
    @Test
    void addSectionWhenIsSameUpEndStation() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(정자역.getId(), 강남역.getId(), 5), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점 추가 테스트 ")
    @Test
    void addSectionWhenIsSameDownEndStation() {
        ExtractableResponse<Response> response = addSection(generateSectionRequest(광교역.getId(), 광교중앙역.getId(), 5), 신분당선.getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간(section) 추가 시 station이 모두 존재하면 예외")
    @TestFactory
    Stream<DynamicTest> makeExceptionWhenMatchBothStation() {
        return Stream.of(
                dynamicTest("테스트를 위한 section 추가", () -> {
                    ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 정자역.getId(), 5), 신분당선.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                dynamicTest("기존 존재하는 station으로 section 추가하여 예외 발생", () -> {
                    ExtractableResponse<Response> response = addSection(generateSectionRequest(강남역.getId(), 광교역.getId(), 5), 신분당선.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                })
        );
    }

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
