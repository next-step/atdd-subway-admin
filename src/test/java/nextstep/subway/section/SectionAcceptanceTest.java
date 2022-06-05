package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    public static final String SECTIONS = "/sections";
    public static final String LINES = "/lines";

    @LocalServerPort
    int port;

    StationResponse 청량리역;
    StationResponse 왕십리역;
    StationResponse 서울숲역;
    StationResponse 강남구청역;
    StationResponse 선릉역;
    StationResponse 도곡역;
    LineResponse 분당선;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        청량리역 = StationAcceptanceTest.createStation(new StationRequest("청량리역")).as(StationResponse.class);
        왕십리역 = StationAcceptanceTest.createStation(new StationRequest("왕십리역")).as(StationResponse.class);
        서울숲역 = StationAcceptanceTest.createStation(new StationRequest("서울숲역")).as(StationResponse.class);
        강남구청역 = StationAcceptanceTest.createStation(new StationRequest("강남구청역")).as(StationResponse.class);
        선릉역 = StationAcceptanceTest.createStation(new StationRequest("선릉역")).as(StationResponse.class);
        도곡역 = StationAcceptanceTest.createStation(new StationRequest("도곡역")).as(StationResponse.class);
        분당선 = LineAcceptanceTest.createLine(new LineRequest(
                        "분당선", "bg-yellow-600", 왕십리역.getId(), 선릉역.getId(), 7))
                .as(LineResponse.class);
    }

    /**
     * Given 구간 하나를 가진 노선이 있을 때
     * When 상행이 같은 구간을 추가하면
     * Then 역 사이에 역이 추가된 것을 확인할 수 있다
     */
    @DisplayName("상행이 같은 구간을 추가한다.")
    @Test
    void 역_사이_새로운_역_추가_상행_동일() {
        // given
        // 분당선(왕십리역-선릉역)

        // when
        addSection(분당선.getId(), new SectionRequest(왕십리역.getId(), 서울숲역.getId(), 4));

        // then
        List<String> stationNames = LineAcceptanceTest.getLine("/lines/" + 분당선.getId())
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly("왕십리역", "서울숲역", "선릉역");
    }

    /**
     * Given 구간 하나를 가진 노선이 있을 때
     * When 하행이 같은 구간을 추가하면
     * Then 역 사이에 역이 추가된 것을 확인할 수 있다
     */
    @DisplayName("하행이 같은 구간을 추가한다.")
    @Test
    void 역_사이_새로운_역_추가_하행_동일() {
        // given
        // 분당선(왕십리역-선릉역)

        // when
        addSection(분당선.getId(), new SectionRequest(서울숲역.getId(), 선릉역.getId(), 4));

        // then
        List<String> stationNames = LineAcceptanceTest.getLine("/lines/" + 분당선.getId())
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly("왕십리역", "서울숲역", "선릉역");
    }

    /**
     * Given 구간 하나를 가진 노선이 있을 때
     * When 하행 역이 기존의 상행 종점과 같은 구간을 추가하면
     * Then 추가된 구간의 상행 역이 새로운 상행 종점이 된다
     */
    @DisplayName("하행 역이 기존의 상행 종점과 같은 구간을 추가한다.")
    @Test
    void 새로운_상행_종점() {
        // given
        // 분당선(왕십리역-선릉역)

        // when
        addSection(분당선.getId(), new SectionRequest(청량리역.getId(), 왕십리역.getId(), 4));

        // then
        List<String> stationNames = LineAcceptanceTest.getLine("/lines/" + 분당선.getId())
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly("청량리역", "왕십리역", "선릉역");
    }

    /**
     * Given 구간 하나를 가진 노선이 있을 때
     * When 상행 역이 기존의 하행 종점과 같은 구간을 추가하면
     * Then 추가된 구간의 하행 역이 새로운 하행 종점이 된다
     */
    @DisplayName("상행 역이 기존의 하행 종점과 같은 구간을 추가한다.")
    @Test
    void 새로운_하행_종점() {
        // given
        // 분당선(왕십리역-선릉역)

        // when
        addSection(분당선.getId(), new SectionRequest(선릉역.getId(), 도곡역.getId(), 4));

        // then
        List<String> stationNames = LineAcceptanceTest.getLine("/lines/" + 분당선.getId())
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly("왕십리역", "선릉역", "도곡역");
    }

    /**
     * Given 구간 하나를 가진 노선이 있을 때
     * When 모든 케이스의 구간을 추가하면
     * Then 추가된 역들을 모두 확인할 수 있다
     */
    @DisplayName("모든 케이스의 구간을 추가한다.")
    @Test
    void 다양한_구간_추가() {
        // given
        // 분당선(왕십리역-7m-선릉역)

        // when
        addSection(분당선.getId(), new SectionRequest(청량리역.getId(), 왕십리역.getId(), 3));
        addSection(분당선.getId(), new SectionRequest(왕십리역.getId(), 서울숲역.getId(), 4));
        addSection(분당선.getId(), new SectionRequest(강남구청역.getId(), 선릉역.getId(), 2));
        addSection(분당선.getId(), new SectionRequest(선릉역.getId(), 도곡역.getId(), 2));

        // then
        List<String> stationNames = LineAcceptanceTest.getLine("/lines/" + 분당선.getId())
                .jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly("청량리역","왕십리역", "서울숲역", "강남구청역", "선릉역", "도곡역");
    }

    /**
     * Given 구간 하나를 가진 노선이 있을 때
     * When 기존 구간과 같은 구간을 추가하면
     * Then 400 에러가 전달된다
     */
    @DisplayName("기존 구간과 중복인 구간을 추가한다.")
    @Test
    void 중복_구간_추가_1() {
        // given
        // 분당선(왕십리역-선릉역)

        // when
        ExtractableResponse<Response> response = addSection(
                분당선.getId(), new SectionRequest(왕십리역.getId(), 선릉역.getId(), 4));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 구간이 추가된 노선이 있을 때
     * When 상행 역이 조정된 기존 구간과 중복인 구간을 추가하면
     * Then 400 에러가 전달된다
     */
    @DisplayName("상행 역이 조정된 기존 구간과 중복인 구간을 추가한다.")
    @Test
    void 중복_구간_추가_2() {
        // given
        // 분당선(null-왕십리역, 왕십리역-선릉역)

        // 분당선(null-청량리역, 청량리역-왕십리역, 왕십리역-선릉역)
        addSection(분당선.getId(), new SectionRequest(청량리역.getId(), 왕십리역.getId(), 3));

        // 분당선(null-청량리역, 청량리역-왕십리역, 왕십리역-서울숲역, **서울숲역-선릉역**)
        addSection(분당선.getId(), new SectionRequest(왕십리역.getId(), 서울숲역.getId(), 4));

        // when
        ExtractableResponse<Response> response = addSection(
                분당선.getId(), new SectionRequest(서울숲역.getId(), 선릉역.getId(), 4));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 구간 하나를 가진 노선이 있을 때
     * When 역 사이에 새로운 역을 추가할 때, 기존 구간의 길이를 초과하는 구간을 추가하려 하면
     * Then 400 에러가 전달된다
     */
    @DisplayName("기존 구간의 길이를 초과하는 구간을 추가하려 한다.")
    @Test
    void 길이_초과_구간_추가_1() {
        // given
        // 분당선(왕십리역-(7m)-선릉역)

        // when
        ExtractableResponse<Response> response = addSection(
                분당선.getId(), new SectionRequest(왕십리역.getId(), 서울숲역.getId(), 7));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 구간이 추가되어 길이가 줄어든 구간을 가진 노선이 있을 때
     * When 줄어든 기존 구간 역 사이에 역을 추가하기 위한 구간이 길이를 초과하면
     * Then 400 에러가 전달된다
     */
    @DisplayName("줄어든 기존 구간의 길이를 초과하는 구간을 추가하려 한다.")
    @Test
    void 길이_초과_구간_추가_2() {
        // given
        // 분당선(null-0m-왕십리역, 왕십리역-7m-선릉역)

        // 분당선(null-0m-왕십리역, 왕십리역-4m-서울숲역, **서울숲역-3m-선릉역**)
        addSection(분당선.getId(), new SectionRequest(왕십리역.getId(), 서울숲역.getId(), 4));

        // when
        ExtractableResponse<Response> response = addSection(
                분당선.getId(), new SectionRequest(서울숲역.getId(), 강남구청역.getId(), 3));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 구간이 추가된 노선이 있을 때
     * When 겹치는 역이 없어서 추가할 수 없는 구간을 추가하려 하면
     * Then 400 에러가 전달된다
     */
    @DisplayName("기존의 어떤 구간들과도 역이 겹치지 않는 구간을 추가하려 한다.")
    @Test
    void 일치_역_없는_구간_추가() {
        // given
        // 분당선(왕십리역-선릉역)

        // when
        ExtractableResponse<Response> response = addSection(
                분당선.getId(), new SectionRequest(청량리역.getId(), 서울숲역.getId(), 7));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> addSection(Long lineId, SectionRequest body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(LINES + "/" + lineId + SECTIONS)
                .then().log().all()
                .extract();
    }
}
