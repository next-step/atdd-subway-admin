package nextstep.subway.section;

import static nextstep.subway.line.LineAcceptanceTest.노션_조회;
import static nextstep.subway.line.LineAcceptanceTest.지하철역과_노선_동시_생성;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private static LineResponse lineResponse;
    private static StationResponse newStationResponse;
    private static StationResponse newNotLineStationResponse;

    @BeforeEach
    public void init() {
        lineResponse = 지하철역과_노선_동시_생성("신림역", "서울대입구역", "2호선", "bg-green-600", 10L).extract()
            .as(LineResponse.class);
        newStationResponse = 지하철역_생성("봉천역").extract().as(StationResponse.class);
        newNotLineStationResponse = 지하철역_생성("강남역").extract().as(StationResponse.class);
    }

    @TestFactory
    Stream<DynamicTest> createSection_fail() {
        return Stream.of(
            DynamicTest.dynamicTest("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같다면 실패", () -> {
                List<StationResponse> preStations = lineResponse.getStations();
                지하철_구간_등록시_실패_검증(preStations.get(0).getId(), newStationResponse.getId(), 11L);
            }),
            DynamicTest.dynamicTest("상행역과 하행역이 노선에 등록되어있는 있지 않은 역일 경우 실패", () -> {
                지하철_구간_등록시_실패_검증(newNotLineStationResponse.getId(), newStationResponse.getId(), 10L);
            })
            ,
            DynamicTest.dynamicTest("상행역과 하행역이 노선에 이미 등록된 역일 경우 실패", () -> {
                List<StationResponse> preStations = lineResponse.getStations();
                지하철_구간_등록시_실패_검증(preStations.get(0).getId(), preStations.get(preStations.size() - 1).getId(), 11L);
            })
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("providerCreateSectionCase")
    void createSection(String name, SectionRequest sectionRequest, int resultSize) {
        LineResponse response = 지하철_구간_등록(sectionRequest, lineResponse.getId()).as(LineResponse.class);

        assertThat(response.getStations()).hasSize(resultSize);
    }


    @Test
    @DisplayName("노선이 하나일 경우 삭제에 실패한다.")
    void deleteSection_fail() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(lineResponse.getStations().get(0).getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }



    @ParameterizedTest(name = "{0}")
    @MethodSource("providerDeleteSectionCase")
    void deleteSection_success(String name, Long stationId) {
        // given
        List<StationResponse> preStations = lineResponse.getStations();
        SectionRequest sectionRequest = new SectionRequest(preStations.get(0).getId(), newStationResponse.getId(), 5L);
        지하철_구간_등록(sectionRequest, lineResponse.getId());

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제(stationId);

        // then
        List<Object> stations = 노션_조회(lineResponse.getId()).jsonPath().getList("stations");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stations).hasSize(2);
    }

    static Stream<Arguments> providerDeleteSectionCase() {
        List<StationResponse> preStations = lineResponse.getStations();

        return Stream.of(
            Arguments.of(
                "상행종점역이 삭제되는 경우",
                preStations.get(0).getId()
            ),
            Arguments.arguments(
                "하행종점역이 삭제되는 경우",
                preStations.get(preStations.size() - 1).getId()
            ),
            Arguments.arguments(
                "상하행종점역 사이의 역이 삭제되는 경우",
                newStationResponse.getId()
            )
        );
    }

    static Stream<Arguments> providerCreateSectionCase() {
        List<StationResponse> preStations = lineResponse.getStations();

        return Stream.of(
            Arguments.of(
                "역 사이에 새로운 역이 등록될 경우",
                new SectionRequest(preStations.get(0).getId(), newStationResponse.getId(), 5L),
                3
            ),
            Arguments.arguments(
                "새로운 역이 상행종점역으로 등록될 경우",
                new SectionRequest(newStationResponse.getId(), preStations.get(0).getId(), 10L),
                3
            ),
            Arguments.arguments(
                "새로운 역이 하행종점역으로 등록될 경우",
                new SectionRequest(preStations.get(preStations.size() - 1).getId(), newStationResponse.getId(), 10L),
                3
            )
        );
    }

    private ExtractableResponse<Response> 지하철_노선_구간_삭제(Long stationId) {
        return RestAssured.given().log().all()
            .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineResponse.getId(), stationId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_구간_등록(SectionRequest sectionRequest, Long lineId) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();
    }

    private void 지하철_구간_등록시_실패_검증(Long upStationId, Long downStationId, Long distance) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        ExtractableResponse<Response> response = 지하철_구간_등록(sectionRequest, lineResponse.getId());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
