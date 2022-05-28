package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.subway.line.LineRestAssured.*;
import static nextstep.subway.station.StationRestAssured.지하철역_등록;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 삼성역;
    private StationResponse 서초역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철역_등록("강남역").as(StationResponse.class);
        역삼역 = 지하철역_등록("역삼역").as(StationResponse.class);
        신분당선 = 노선_등록("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId()).as(LineResponse.class);

        삼성역 = 지하철역_등록("삼성역").as(StationResponse.class);
        서초역 = 지하철역_등록("서초역").as(StationResponse.class);
    }

    /**
     * When 지하철_노선에_지하철역_등록_요청하면
     * Then 지하철_노선에_지하철역_등록됨
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        //when
        ExtractableResponse<Response> response = 노선_구간_추가(신분당선.getId(), 삼성역.getId(), 서초역.getId(), 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
