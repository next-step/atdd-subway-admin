package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.utils.RestAssuredTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static nextstep.subway.PageController.URIMapping.LINE;
import static nextstep.subway.PageController.URIMapping.SECTION;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private RestAssuredTemplate restAssuredTemplate;

    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long middleStationId1;
    private Long middleStationId2;

    private static final int DEFAULT_DISTANCE = 100;

    @BeforeEach
    public void register() {
        // 등록된_상행역_하행역
        ExtractableResponse<Response> upStationResponse = StationAcceptanceTest.requestCreateStation("온수역");
        ExtractableResponse<Response> downStationResponse = StationAcceptanceTest.requestCreateStation("장암역");

        // 등록된_중간역
        ExtractableResponse<Response> middleStationResponse1 = StationAcceptanceTest.requestCreateStation("대림역");
        ExtractableResponse<Response> middleStationResponse2 = StationAcceptanceTest.requestCreateStation("건대입구역");

        //등록된_노선
        LineRequest lineRequest = LineRequest.builder()
                .name("7호선")
                .color("green lighten-1")
                .upStationId(RestAssuredTemplate.getLocationId(upStationResponse))
                .downStationId(RestAssuredTemplate.getLocationId(downStationResponse))
                .distance(DEFAULT_DISTANCE)
                .build();

        //등록된_ID들
        lineId = RestAssuredTemplate.getLocationId(LineAcceptanceTest.requestCreatedLine(lineRequest));
        upStationId = RestAssuredTemplate.getLocationId(upStationResponse);
        downStationId = RestAssuredTemplate.getLocationId(downStationResponse);
        middleStationId1 = RestAssuredTemplate.getLocationId(middleStationResponse1);
        middleStationId2 = RestAssuredTemplate.getLocationId(middleStationResponse2);

        restAssuredTemplate = new RestAssuredTemplate(String.format("%s/%s%s", LINE, lineId, SECTION));
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    protected void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        SectionRequest sectionRequest = SectionRequest.builder()
                .upStationId(upStationId)
                .downStationId(middleStationId1)
                .distance(30)
                .build();

        ExtractableResponse<Response> response = requestCreatedLine(sectionRequest);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public ExtractableResponse<Response> requestCreatedLine(final SectionRequest param) {
        return restAssuredTemplate.post(param.toMap());
    }

    //TODO : 역 사이에 새로운 역을 등록할 경우
    //TODO : 새로운 역을 상행 종점으로 등록할 경우
    //TODO : 새로운 역을 하행 종점으로 등록할 경우

    //TODO : 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
    //TODO : 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    //TODO : 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
}
