package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.utils.RestAssuredTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.PageController.URIMapping.LINE;
import static nextstep.subway.PageController.URIMapping.SECTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private RestAssuredTemplate restAssuredTemplate;

    private Long lineId;
    private Long stationIdA;
    private Long stationIdB;
    private Long stationIdC;

    private static final int DEFAULT_DISTANCE = 7;

    @BeforeEach
    public void register() {
        ExtractableResponse<Response> stationResponseA = StationAcceptanceTest.requestCreateStation("A");
        ExtractableResponse<Response> stationResponseB = StationAcceptanceTest.requestCreateStation("B");
        ExtractableResponse<Response> stationResponseC = StationAcceptanceTest.requestCreateStation("C");

        //등록된_노선
        LineRequest lineRequest = LineRequest.builder()
                .name("1호선")
                .color("green lighten-1")
                .upStationId(RestAssuredTemplate.getLocationId(stationResponseA))
                .downStationId(RestAssuredTemplate.getLocationId(stationResponseC))
                .distance(DEFAULT_DISTANCE)
                .build();

        ExtractableResponse<Response> lineResponse = LineAcceptanceTest.requestCreatedLine(lineRequest);

        //등록된_ID들
        lineId = RestAssuredTemplate.getLocationId(lineResponse);
        stationIdA = RestAssuredTemplate.getLocationId(stationResponseA);
        stationIdB = RestAssuredTemplate.getLocationId(stationResponseB);
        stationIdC = RestAssuredTemplate.getLocationId(stationResponseC);

        restAssuredTemplate = new RestAssuredTemplate(String.format("%s/%s%s", LINE, lineId, SECTION));
    }

    @DisplayName("노선 구간 등록 - 역 사이에 새로운 역을 등록할 경우")
    @Test
    protected void addSection1() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(stationIdA)
                .downStationId(stationIdB)
                .distance(4)
                .build();

        ExtractableResponse<Response> response = requestCreatedSection(sectionRequest);

        // then
        assertAll(
            // 지하철_노선에_지하철역_등록됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),

            // 등록된_노선_결과
            () -> {
                LineResponse lineResponse = LineAcceptanceTest.requestShowLines(lineId).as(LineResponse.class);

                List<Long> stationResponseIds = lineResponse.getStations()
                        .stream()
                        .map(x -> x.getId())
                        .collect(Collectors.toList());

                //A----B---C
                assertThat(stationResponseIds).containsExactly(stationIdA, stationIdB, stationIdC);
                assertThat(lineResponse.getDistances()).containsExactly(4, 3);
            }
        );
    }

    @DisplayName("노선 구간 등록 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    protected void addSection2() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(stationIdB)
                .downStationId(stationIdA)
                .distance(4)
                .build();

        ExtractableResponse<Response> response = requestCreatedSection(sectionRequest);

        // then
        assertAll(
            // 지하철_노선에_지하철역_등록됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),

            // 등록된_노선_결과
            () -> {
                LineResponse lineResponse = LineAcceptanceTest.requestShowLines(lineId).as(LineResponse.class);

                List<Long> stationResponseIds = lineResponse.getStations()
                        .stream()
                        .map(x -> x.getId())
                        .collect(Collectors.toList());

                //B----A-------C
                assertThat(stationResponseIds).containsExactly(stationIdB, stationIdA, stationIdC);
                assertThat(lineResponse.getDistances()).containsExactly(4, 7);
            }
        );
    }

    @DisplayName("노선 구간 등록 - 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    protected void addSection3() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(stationIdC)
                .downStationId(stationIdB)
                .distance(3)
                .build();

        ExtractableResponse<Response> response = requestCreatedSection(sectionRequest);

        // then
        assertAll(
            // 지하철_노선에_지하철역_등록됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),

            // 등록된_노선_결과
            () -> {
                LineResponse lineResponse = LineAcceptanceTest.requestShowLines(lineId).as(LineResponse.class);

                List<Long> stationResponseIds = lineResponse.getStations()
                        .stream()
                        .map(x -> x.getId())
                        .collect(Collectors.toList());

                //A-------C---B
                assertThat(stationResponseIds).containsExactly(stationIdA, stationIdC, stationIdB);
                assertThat(lineResponse.getDistances()).containsExactly(7, 3);
            }
        );
    }

    @DisplayName("노선 구간 등록 예외 - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @ParameterizedTest
    @ValueSource(ints = {DEFAULT_DISTANCE, DEFAULT_DISTANCE + 1})
    protected void addSectionException1(int distance) {
        // when
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(stationIdB)
                .downStationId(stationIdC)
                .distance(distance)
                .build();

        ExtractableResponse<Response> response = requestCreatedSection(sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 구간 등록 예외 - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    protected void addSectionException2() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(stationIdA)
                .downStationId(stationIdC)
                .distance(3)
                .build();

        ExtractableResponse<Response> response = requestCreatedSection(sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 구간 등록 예외 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    protected void addSectionException3() {
        // when
        ExtractableResponse<Response> stationResponseX = StationAcceptanceTest.requestCreateStation("X");
        ExtractableResponse<Response> stationResponseY = StationAcceptanceTest.requestCreateStation("Y");

        long stationIdX = RestAssuredTemplate.getLocationId(stationResponseX);
        long stationIdY = RestAssuredTemplate.getLocationId(stationResponseY);

        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(stationIdX)
                .downStationId(stationIdY)
                .distance(3)
                .build();

        ExtractableResponse<Response> response = requestCreatedSection(sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 구간 제거")
    @Test
    protected void deleteSection() {
        // given
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(stationIdA)
                .downStationId(stationIdB)
                .distance(4)
                .build();

        requestCreatedSection(sectionRequest);

        //when
        //지하철_역_삭제_요청
        ExtractableResponse<Response> response = requestDeleteSection(lineId, stationIdB);

        //then
        assertAll(
            // 지하철_노선에_지하철역_삭제됨
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),

            // 삭제된_노선_결과
            () -> {
                LineResponse lineResponse = LineAcceptanceTest.requestShowLines(lineId).as(LineResponse.class);

                List<Long> stationResponseIds = lineResponse.getStations()
                        .stream()
                        .map(x -> x.getId())
                        .collect(Collectors.toList());

                //A-------C
                assertThat(stationResponseIds).containsExactly(stationIdA, stationIdC);
                assertThat(lineResponse.getDistances()).containsExactly(7);
            }
        );
    }

    @DisplayName("구간이 하나인 노선에서는 구간을 제거할수 없다.")
    @Test
    protected void deleteSectionException1() {
        //when
        ExtractableResponse<Response> response = requestDeleteSection(lineId, stationIdA);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 역을 제거시 예외를 발생한다.")
    @Test
    protected void deleteSectionException2() {
        //given
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(stationIdA)
                .downStationId(stationIdB)
                .distance(4)
                .build();

        requestCreatedSection(sectionRequest);

        //지하철역_생성_요청
        Long stationIdX = RestAssuredTemplate.getLocationId(
            StationAcceptanceTest.requestCreateStation("X")
        );

        //when
        ExtractableResponse<Response> response = requestDeleteSection(lineId, stationIdX);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public ExtractableResponse<Response> requestCreatedSection(final SectionRequest param) {
        return restAssuredTemplate.post(param.toMap());
    }

    public ExtractableResponse<Response> requestShowSection(final Long id) {
        return restAssuredTemplate.get(id);
    }

    public ExtractableResponse<Response> requestShowSection() {
        return restAssuredTemplate.get();
    }

    public ExtractableResponse<Response> requestDeleteSection(final Long lineId, Long stationId) {
        String uri = String.format("%s/%s/sections?stationId=%s", LINE, lineId, stationId);
        return restAssuredTemplate.delete(uri);
    }
}
