package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.SectionAddAcceptanceTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class SectionAddAcceptanceTest extends AcceptanceTest {
    private Long 노선ID;
    private Long 강남ID;
    private Long 역삼ID;
    private Long 선릉ID;
    private Long DEFAULT_DISTANCE;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> 강남역 = 지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> 역삼역 = 지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> 선릉역 = 지하철역_등록되어_있음("선릉역");

        강남ID = 강남역.as(StationResponse.class).getId();
        역삼ID = 역삼역.as(StationResponse.class).getId();
        선릉ID = 선릉역.as(StationResponse.class).getId();

        DEFAULT_DISTANCE = 10L;
        LineRequest 노선_요청 = 노선_구간_요청_생성("2호선", "bg-green-600", 강남ID, 역삼ID, DEFAULT_DISTANCE);
        ExtractableResponse<Response> 노선 = 노선_등록시_구간_등록되어_있음(노선_요청);

        노선ID = 노선.as(LineResponse.class).getId();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우: 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정")
    @Test
    void addStationBetweenStations() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(노선ID, 강남ID, 선릉ID, DEFAULT_DISTANCE - 5L);

        // then
        지하철_노선에_지하철역_등록됨(response);
        assertThat(response.as(SectionResponse.class).getUpStation().getId()).isEqualTo(강남ID);
        assertThat(response.as(SectionResponse.class).getDownStation().getId()).isEqualTo(선릉ID);
        assertThat(response.as(SectionResponse.class).getDistance()).isEqualTo(5L);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록 가능")
    @Test
    void addUpStationsInSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(노선ID, 선릉ID, 강남ID,DEFAULT_DISTANCE - 5L);

        // then
        지하철_노선에_지하철역_등록됨(response);
        assertThat(response.as(SectionResponse.class).getUpStation().getId()).isEqualTo(선릉ID);
        assertThat(response.as(SectionResponse.class).getDownStation().getId()).isEqualTo(강남ID);
        assertThat(response.as(SectionResponse.class).getDistance()).isEqualTo(5L);
    }

    @DisplayName("새로운 역을 하행 종점으로 등록 가능")
    @Test
    void addDownStationsInSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = 노선에_지하철역_등록_요청(노선ID, 역삼ID, 선릉ID, DEFAULT_DISTANCE - 5L);

        // then
        지하철_노선에_지하철역_등록됨(response);
        assertThat(response.as(SectionResponse.class).getUpStation().getId()).isEqualTo(역삼ID);
        assertThat(response.as(SectionResponse.class).getDownStation().getId()).isEqualTo(선릉ID);
        assertThat(response.as(SectionResponse.class).getDistance()).isEqualTo(5L);
    }
}
