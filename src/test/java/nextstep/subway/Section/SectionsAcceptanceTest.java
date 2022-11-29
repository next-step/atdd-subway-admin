package nextstep.subway.Section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.constants.ErrorCode;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.SectionRequest;
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

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.execute();
        createLine("2호선", "green", creatStation("강남역"), creatStation("잠실역"), DEFAULT_DISTANCE);
    }

    @Test
    public void test() {
        Line line = findLine("2호선");
        System.out.println(line.getSections().asList().size());
    }

    @DisplayName("Section 추가 시 상행이 같을 때")
    @TestFactory
    Stream<DynamicTest> addSectionWhenIsSameUpStation() {
        return Stream.of(
                dynamicTest("지하철 노선을 조회하고 구간(Sction) 추가 요청", () -> {
                    Line line = findLine("2호선");
                    ExtractableResponse<Response> response =
                            addSection(generateRequest(findStation("강남역").getId(), creatStation("삼성역"), 4), line.getId());
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),


                dynamicTest("지하철 노선 수정된 정보 확인", () -> {
                    Line line = findLine("2호선");
                    Section section = line.getSections().asList()
                            .stream()
                            .filter(it -> it.isSameUpStationId(new Section(findStation("강남역"), findStation("잠실역"), DEFAULT_DISTANCE)))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NO_SAME_SECTION_EXCEPTION.getErrorMessage()));
                    assertThat(section.findDownStationName()).isEqualTo("잠실역");
                })
        );
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

    private SectionRequest generateRequest(long upStationId, long downStationId, long distance) {
        return new SectionRequest(upStationId, downStationId, distance);
    }
}
