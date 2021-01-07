package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceTestSupport;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTestSupport;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선에 구간을 등록한다.")
    void addSection() {
        // given
        String upStationName = "강남역";
        String downStationName = "양재역";
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , upStationName, downStationName, 10);
        LineResponse lineResponse = response.as(LineResponse.class);

        String addStationName = "판교역";
        ExtractableResponse<Response> addStationsResponse
                = StationAcceptanceTestSupport.지하철_역_생성_요청(new StationRequest(addStationName));
        Long addStationId = addStationsResponse.as(StationResponse.class).getId();

        SectionRequest sectionRequest
                = new SectionRequest(LineAcceptanceTestSupport.getUpStationId(), addStationId, 5);

        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> sectionAddResponse
                = SectionAcceptanceTestSupport.지하철_노선에_지하철역_등록_요청(lineResponse.getId(), sectionRequest);

        LineResponse findUpdateLineResponse
                = LineAcceptanceTestSupport.지하철_노선_조회_요청(response.header(HttpHeaders.LOCATION))
                .as(LineResponse.class);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();

        // 지하철 노선이 추가 됨
        assertThat(sectionAddResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findUpdateLineResponse.getStations().size()).isEqualTo(3);
        assertThat(findUpdateLineResponse.getStations().stream().map(StationResponse::getName))
                .contains(upStationName, downStationName, addStationName);
    }

}
