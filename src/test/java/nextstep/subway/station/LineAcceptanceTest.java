package nextstep.subway.station;

import static nextstep.subway.utils.AttdLineUtils.지하철_노선_등록하기;
import static nextstep.subway.utils.AttdStationUtils.지하철역_만들기;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DisplayName("지하철 노선")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @BeforeEach
    public void init() {
        RestAssured.port = port;
    }


    /**
     * given 지하철역 2개를 생성해놓은 뒤
     * when 지하철 노선을 등록하며 해당 지하철역 2개를 상향, 하향 종착점으로 설정시
     * then 노선이 등록된다.
     */
    @Test
    @DisplayName("지하철 노선 등록")
    public void 지하철_노선_등록하기_테스트() {
        //given
        지하철역_만들기("정자역");
        지하철역_만들기("광교역");

        //when
        ExtractableResponse<Response> 신분당선 = 지하철_노선_등록하기(
            "신분당선", "bg-red-600", "1", "2", "10"
        );

        //then
        assertAll(
            () -> assertThat(신분당선.jsonPath().get("id").toString()).isEqualTo("1"),
            () -> assertThat(신분당선.jsonPath().get("name").toString()).isEqualTo("신분당선"),
            () -> assertThat(신분당선.jsonPath().get("color").toString()).isEqualTo("bg-red-600"),
            () -> assertThat(신분당선.jsonPath().getList("stations")).hasSize(2)
        );
    }

}
