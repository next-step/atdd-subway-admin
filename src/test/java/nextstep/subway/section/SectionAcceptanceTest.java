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
    private Long stationIdD;

    private static final int DEFAULT_DISTANCE = 7;

    @BeforeEach
    public void register() {
        ExtractableResponse<Response> stationResponseA = StationAcceptanceTest.requestCreateStation("A");
        ExtractableResponse<Response> stationResponseB = StationAcceptanceTest.requestCreateStation("B");
        ExtractableResponse<Response> stationResponseC = StationAcceptanceTest.requestCreateStation("C");
        ExtractableResponse<Response> stationResponseD = StationAcceptanceTest.requestCreateStation("D");

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
        stationIdD = RestAssuredTemplate.getLocationId(stationResponseD);

        restAssuredTemplate = new RestAssuredTemplate(String.format("%s/%s%s", LINE, lineId, SECTION));
    }

    @DisplayName("노선 구간 등록 - 역 사이에 새로운 역을 등록할 경우")
    @Test
    protected void addSection() {
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


    //TODO : 새로운 역을 상행 종점으로 등록할 경우
    //TODO : 새로운 역을 하행 종점으로 등록할 경우

    //TODO : 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    //TODO : 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    //TODO : 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음

    public ExtractableResponse<Response> requestCreatedSection(final SectionRequest param) {
        return restAssuredTemplate.post(param.toMap());
    }

    public ExtractableResponse<Response> requestShowSection(final Long id) {
        return restAssuredTemplate.get(id);
    }

    public ExtractableResponse<Response> requestShowSection() {
        return restAssuredTemplate.get();
    }
}
