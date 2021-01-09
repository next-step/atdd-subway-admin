package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.utils.LineRestAssuredUtils.지하철_노선_생성_요청;
import static nextstep.subway.utils.SectionRestAssuredUtils.구간_등록_요청;
import static nextstep.subway.utils.StationRestAssuredUtils.지하철_역_생성_요청;

@DisplayName("노선 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        지하철_역_생성_요청("강남역");
        지하철_역_생성_요청("역삼역");
    }

    @Test
    @DisplayName("구간을 등록한다 (새로운 역을 하행 종점으로 등록)")
    void addSection() {
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green","1","2","10").as(LineResponse.class);

        지하철_역_생성_요청("사당역");
        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "1", "3", "3");
    }

    @Test
    @DisplayName("구간 등록시, 상행과 하행이 동일한 경우 400 익셉션 발생")
    void addDuplicationSection() {
        // when
        LineResponse lineResponse = 지하철_노선_생성_요청("2호선", "green", "1", "2", "10").as(LineResponse.class);

        지하철_역_생성_요청("사당역");
        ExtractableResponse<Response> response = 구간_등록_요청(lineResponse.getId(), "3", "3", "10");

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }



}
