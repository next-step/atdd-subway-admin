package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationApiRequests;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private Long 서울2호선;

    @BeforeEach
    void setUpSectionAcceptanceTest() {
        super.setUp();

        강남역 = StationApiRequests.지하철_역_생성됨("강남역");
        StationResponse 잠실역 = StationApiRequests.지하철_역_생성됨("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "bg-red-600", 강남역.getId(), 잠실역.getId(), 10);
        서울2호선 = LineHelper.지하철_노선_등록되어_있음(lineRequest);
    }

    @DisplayName("역 사이에 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        // 지하철 역 생성됨
        // 지하철 노선 생성됨
        StationResponse 삼성역 = StationApiRequests.지하철_역_생성됨("삼성역");
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 삼성역.getId(), 4);


        // when
        // 노선 구간 등록 요청
        ExtractableResponse<Response> response = SectionApiRequests.지하철_노선_구간_등록_요청(서울2호선, sectionRequest);

        // then
        // 노선에 구간 등록됨.
        지하철_노선_구간_등록됨(response);
    }

    private void 지하철_노선_구간_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


}
