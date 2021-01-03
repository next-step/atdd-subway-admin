package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RequestTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse lastUpStation;
    private StationResponse lastDownStation;
    private LineResponse line;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        // 지하철_역_등록_되어있음
        lastUpStation = StationAcceptanceTest.createRequest(new StationRequest("도림천"))
                .as(StationResponse.class);
        lastDownStation = StationAcceptanceTest.createRequest(new StationRequest("신정네거리"))
                .as(StationResponse.class);

        line = LineAcceptanceTest.createRequest(new LineRequest("2호선", "초록색", lastUpStation.getId(), lastDownStation.getId(), 30))
                .as(LineResponse.class);
    }

    @DisplayName("상행, 하행 역이 동일한 지하철 구간을 등록한다")
    @Test
    void createDuplicateStation() {
        ExtractableResponse<Response> response = createRequest(new SectionRequest(line.getId(), lastUpStation.getId(), lastUpStation.getId(), 3));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 등록 된 지하철 구간을 등록한다")
    @Test
    void createDuplicateSection() {
        ExtractableResponse<Response> response = createRequest(new SectionRequest(line.getId(), lastUpStation.getId(), lastDownStation.getId(), 3));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선이 없는 지하철 구간을 등록한다")
    @Test
    void createWithNotExistsLine() {
        ExtractableResponse<Response> response = createRequest(new SectionRequest(10L, lastUpStation.getId(), lastDownStation.getId(), 3));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("역이 없는 지하철 구간을 등록한다")
    @Test
    void createWithNotExistsStation() {
        ExtractableResponse<Response> response = createRequest(new SectionRequest(line.getId(), lastUpStation.getId(), 10L, 3));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 사이에 구간 거리가 더 긴 새로운 구간을 등록한다")
    @Test
    void createBetweenOverDistance() {
        StationResponse station = StationAcceptanceTest.createRequest(new StationRequest("양천구청"))
                .as(StationResponse.class);

        ExtractableResponse<Response> upResponse = createRequest(new SectionRequest(line.getId(), lastUpStation.getId(), station.getId(), 50));
        assertThat(upResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ExtractableResponse<Response> downResponse = createRequest(new SectionRequest(line.getId(), station.getId(), lastDownStation.getId(), 50));
        assertThat(downResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 사이에 새로운 구간을 등록한다(상행기준)")
    @Test
    void createBetween() {
        StationResponse station = StationAcceptanceTest.createRequest(new StationRequest("양천구청"))
                .as(StationResponse.class);

        ExtractableResponse<Response> response = createRequest(new SectionRequest(line.getId(), lastUpStation.getId(), station.getId(), 3));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> selectedResponse = selectAllRequest(line.getId());
        assertThat(selectedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> sectionNames = toSectionNames(selectedResponse);
        assertThat(sectionNames).containsExactly("도림천", "양천구청", "신정네거리");
    }

    @DisplayName("구간 사이에 새로운 구간을 등록한다(하행기준)")
    @Test
    void createBetween2() {
        StationResponse station = StationAcceptanceTest.createRequest(new StationRequest("양천구청"))
                .as(StationResponse.class);

        ExtractableResponse<Response> response = createRequest(new SectionRequest(line.getId(), station.getId(), lastDownStation.getId(), 3));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> selectedResponse = selectAllRequest(line.getId());
        assertThat(selectedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> sectionNames = toSectionNames(selectedResponse);
        assertThat(sectionNames).containsExactly("도림천", "양천구청", "신정네거리");
    }


    @DisplayName("새로운 상행 종점구간을 등록한다")
    @Test
    void createLastUp() {
        StationResponse station = StationAcceptanceTest.createRequest(new StationRequest("신도림"))
                .as(StationResponse.class);

        ExtractableResponse<Response> response = createRequest(new SectionRequest(line.getId(), station.getId(), lastUpStation.getId(), 3));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> selectedResponse = selectAllRequest(line.getId());
        assertThat(selectedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> sectionNames = toSectionNames(selectedResponse);
        assertThat(sectionNames).containsExactly("신도림", "도림천", "신정네거리");
    }

    @DisplayName("새로운 하행 종점구간을 등록한다")
    @Test
    void createLastDown() {
        StationResponse station = StationAcceptanceTest.createRequest(new StationRequest("까치산"))
                .as(StationResponse.class);

        ExtractableResponse<Response> response = createRequest(new SectionRequest(line.getId(), lastDownStation.getId(), station.getId(), 3));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        ExtractableResponse<Response> selectedResponse = selectAllRequest(line.getId());
        assertThat(selectedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<String> sectionNames = toSectionNames(selectedResponse);
        assertThat(sectionNames).containsExactly("도림천", "신정네거리", "까치산");
    }

    private List<String> toSectionNames(ExtractableResponse<Response> selectedResponse) {
        List<SectionResponse> sections = selectedResponse.jsonPath()
                .getList(".", SectionResponse.class);
        List<String> sectionNames = sections.stream()
                .map(SectionResponse::getUpStationName)
                .collect(Collectors.toList());
        sectionNames.add(sections.get(sections.size()-1).getDownStationName());
        return sectionNames;
    }

    public static ExtractableResponse<Response> createRequest(SectionRequest sectionRequest) {
        final String url = "/sections";
        return RequestTest.doPost(url, sectionRequest);
    }

    private ExtractableResponse<Response> selectAllRequest(Long lineId) {
        final String url = "/sections/" + lineId;
        return RequestTest.doGet(url);
    }
}
