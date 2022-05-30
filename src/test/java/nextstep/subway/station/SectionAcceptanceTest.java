package nextstep.subway.station;

import static nextstep.subway.utils.AttdLineHelper.지하철_노선_등록하기;
import static nextstep.subway.utils.AttdLineHelper.지하철_노선_조회하기;
import static nextstep.subway.utils.AttdSectionHelper.지하철_구간_등록하기;
import static nextstep.subway.utils.AttdSectionHelper.지하철_구간_조회하기;
import static nextstep.subway.utils.AttdStationHelper.지하철역_만들기;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 구간등록")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    int port;

    private final DatabaseCleanup databaseCleanup;
    private String 정자역_ID = null;
    private String 미금역_ID = null;
    private String ID_신분당선 = null;

    @Autowired
    public SectionAcceptanceTest(DatabaseCleanup databaseCleanup) {
        this.databaseCleanup = databaseCleanup;
    }

    @BeforeEach
    public void init() {
        RestAssured.port = port;
        databaseCleanup.execute();

        //given
        정자역_ID = 지하철역_만들기("정자역").jsonPath().get("id").toString();
        미금역_ID = 지하철역_만들기("미금역").jsonPath().get("id").toString();
        ID_신분당선 = 지하철_노선_등록하기(
            "신분당선", "bg-red-600", 정자역_ID, 미금역_ID, "10"
        ).jsonPath().get("id").toString();
    }

    /**
     * given 지하철역 2개와, 그를 포함하는 line이 주어지고(init) 미금-정자
     * given 지하철역 1개를 추가하고, 해당 지하철역을 line 상향종착지를 변경했을때
     * when 지하철 구간 전체조회 및 지하철 노선 조회시
     * then 지하철 구간이 정상적으로 확인되고, 지하철 노선의 상향종착지가 변경된다.
     */
    @Test
    @DisplayName("구간 등록 정상 테스트")
    public void 구간등록_상향종착지변경_테스트() {
        //given
        String 판교역_ID = 지하철역_만들기("판교역").jsonPath().get("id").toString();
        ExtractableResponse<Response> 지하철_구간_등록하기_response = 지하철_구간_등록하기(정자역_ID, 판교역_ID, "5",ID_신분당선);

        //when
        ExtractableResponse<Response> 지하철_구간_조회하기_response = 지하철_구간_조회하기(ID_신분당선);
        ExtractableResponse<Response> 지하철_노선_조회하기_response = 지하철_노선_조회하기(ID_신분당선);

        //then
        assertThat(지하철_구간_등록하기_response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_노선_조회하기_response.jsonPath().getList("stations.name"))
            .containsExactly("판교역", "미금역");
        assertThat(지하철_구간_조회하기_response.jsonPath().getList("downStationInfo.name"))
            .containsExactly("미금역","정자역");
        assertThat(지하철_구간_조회하기_response.jsonPath().getList("upStationInfo.name"))
            .containsExactly("정자역","판교역");
        assertThat(지하철_구간_조회하기_response.jsonPath().getList("distance"))
            .containsExactly(10,5);
    }


}
