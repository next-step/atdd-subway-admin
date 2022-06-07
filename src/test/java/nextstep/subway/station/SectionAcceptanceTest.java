package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static nextstep.subway.station.LineAcceptanceTest.노선_등록;
import static nextstep.subway.station.LineAcceptanceTest.노선_조회;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    private StationResponse A역;
    private StationResponse B역;
    private StationResponse C역;
    private StationResponse D역;
    private StationResponse E역;
    private StationResponse F역;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }
        databaseCleanup.execute();

        A역 = 응답_객체_생성(지하철역_등록("A역"), StationResponse.class);
        B역 = 응답_객체_생성(지하철역_등록("B역"), StationResponse.class);
        C역 = 응답_객체_생성(지하철역_등록("C역"), StationResponse.class);
        D역 = 응답_객체_생성(지하철역_등록("D역"), StationResponse.class);
        E역 = 응답_객체_생성(지하철역_등록("E역"), StationResponse.class);
        F역 = 응답_객체_생성(지하철역_등록("F역"), StationResponse.class);
    }

    /**
     * Given 노선을 생성하고
     * When 구간을 추가하면
     * Then 노선에 속한 지하철역의 숫자가 증가한다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionCase1() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 7, A역.getId(), C역.getId()), LineResponse.class);

        // when
        구간_추가(A역.getId(), B역.getId(), 4, line.getId());

        // then
        LineResponse find = 응답_객체_생성(노선_조회(line.getId()), LineResponse.class);
        assertThat(find.getStations()).hasSize(3);
    }

    /**
     * Given 노선을 생성하고
     * When 구간을 추가하면
     * Then 노선에 속한 지하철역의 숫자가 증가한다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSectionCase2() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 7, A역.getId(), C역.getId()), LineResponse.class);

        // when
        구간_추가(B역.getId(), C역.getId(), 4, line.getId());

        // then
        LineResponse find = 응답_객체_생성(노선_조회(line.getId()), LineResponse.class);
        assertThat(find.getStations()).hasSize(3);
    }

    /**
     * Given 노선을 생성하고
     * When 구간을 추가하면
     * Then 노선에 속한 지하철역의 숫자가 증가한다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionCase3() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 7, A역.getId(), C역.getId()), LineResponse.class);

        // when
        구간_추가(B역.getId(), A역.getId(), 4, line.getId());

        // then
        LineResponse find = 응답_객체_생성(노선_조회(line.getId()), LineResponse.class);
        assertThat(find.getStations()).hasSize(3);
    }

    /**
     * Given 노선을 생성하고
     * When 구간을 추가하면
     * Then 노선에 속한 지하철역의 숫자가 증가한다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionCase4() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 7, A역.getId(), C역.getId()), LineResponse.class);

        // when
        구간_추가(C역.getId(), B역.getId(), 3, line.getId());

        // then
        LineResponse find = 응답_객체_생성(노선_조회(line.getId()), LineResponse.class);
        assertThat(find.getStations()).hasSize(3);
    }

    /**
     * Given 노선을 생성하고
     * When 구간을 추가하면
     * Then 노선에 속한 지하철역의 숫자가 증가한다
     */
    @DisplayName("다양한 패턴으로 노선에 지하철역 등록할 경우")
    @Test
    void addSectionCase5() {
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), D역.getId()), LineResponse.class);

        구간_추가(A역.getId(), B역.getId(), 4, line.getId());
        구간_추가(C역.getId(), D역.getId(), 3, line.getId());
        구간_추가(E역.getId(), A역.getId(), 15, line.getId());
        구간_추가(D역.getId(), F역.getId(), 30, line.getId());

        // then
        LineResponse find = 응답_객체_생성(노선_조회(line.getId()), LineResponse.class);
        assertThat(find.getStations()).hasSize(6);
    }

    /**
     * Given 노선을 생성하고
     * When 노선 거리를 초과하는 구간을 중간에 추가하면
     * Then 노선을 추가하지 못한다
     */
    @DisplayName("구간 추가 오류(구간 거리 초과1)")
    @Test
    void addSectionException1() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 7, A역.getId(), C역.getId()), LineResponse.class);

        // when
        ValidatableResponse response = 구간_추가(A역.getId(), B역.getId(), 7, line.getId());

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 노선을 생성하고
     * When 노선 거리를 초과하는 구간을 중간에 추가하면
     * Then 노선을 추가하지 못한다
     */
    @DisplayName("구간 추가 오류(구간 거리 초과2)")
    @Test
    void addSectionException2() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 7, A역.getId(), C역.getId()), LineResponse.class);

        // when
        ValidatableResponse response = 구간_추가(B역.getId(), C역.getId(), 8, line.getId());

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 노선을 생성하고
     * When 노선 거리를 초과하는 구간을 중간에 추가하면
     * Then 노선을 추가하지 못한다
     */
    @DisplayName("구간 추가 오류(노선에 이미 포함된 지하철 역)")
    @Test
    void addSectionException3() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 7, A역.getId(), C역.getId()), LineResponse.class);

        // when
        ValidatableResponse response = 구간_추가(A역.getId(), B역.getId(), 7, line.getId());

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 노선을 생성하고
     * When 노선에 포함되지 않은 지하철 구간을 추가하면
     * Then 노선을 추가하지 못한다
     */
    @DisplayName("구간 추가 오류(노선에 모두 포함안된 지하철 역)")
    @Test
    void addSectionException4() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 7, A역.getId(), C역.getId()), LineResponse.class);

        // when
        ValidatableResponse response = 구간_추가(E역.getId(), F역.getId(), 10, line.getId());

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 노선을 생성하고
     * When [중간] 노선 내 구간을 삭제하면
     * Then 노선이 삭제된다
     */
    @DisplayName("중간 지하철 역 삭제")
    @Test
    void deleteSectionCase1() {
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), B역.getId()), LineResponse.class);

        구간_추가(B역.getId(), C역.getId(), 4, line.getId());
        구간_추가(C역.getId(), D역.getId(), 3, line.getId());
        구간_추가(D역.getId(), E역.getId(), 15, line.getId());
        구간_삭제(line.getId(), B역.getId());

        // then
        LineResponse find = 응답_객체_생성(노선_조회(line.getId()), LineResponse.class);
        assertThat(find.getStations()).hasSize(4);
    }

    /**
     * Given 노선을 생성하고
     * When [상행 종점 역] 노선 내 구간을 삭제하면
     * Then 노선이 삭제된다
     */
    @DisplayName("상행 종점역 지하철 역 삭제")
    @Test
    void deleteSectionCase2() {
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), B역.getId()), LineResponse.class);

        구간_추가(B역.getId(), C역.getId(), 4, line.getId());
        구간_추가(C역.getId(), D역.getId(), 3, line.getId());
        구간_추가(D역.getId(), E역.getId(), 15, line.getId());
        구간_삭제(line.getId(), A역.getId());

        // then
        LineResponse find = 응답_객체_생성(노선_조회(line.getId()), LineResponse.class);
        assertThat(find.getStations()).hasSize(4);
    }

    /**
     * Given 노선을 생성하고
     * When [하행 종점 역] 노선 내 구간을 삭제하면
     * Then 노선이 삭제된다
     */
    @DisplayName("하행 종점역 지하철 역 삭제")
    @Test
    void deleteSectionCase3() {
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), B역.getId()), LineResponse.class);

        구간_추가(B역.getId(), C역.getId(), 4, line.getId());
        구간_추가(C역.getId(), D역.getId(), 3, line.getId());
        구간_추가(D역.getId(), E역.getId(), 15, line.getId());
        구간_삭제(line.getId(), E역.getId());

        // then
        LineResponse find = 응답_객체_생성(노선_조회(line.getId()), LineResponse.class);
        assertThat(find.getStations()).hasSize(4);
    }

    /**
     * Given 노선을 생성하고
     * When 노선 내 하나 남은 구간의 지하철 역을 삭제하면
     * Then 지하철 역을 삭제할 수 없다
     */
    @DisplayName("노선 내 하나 남은 구간의 지하철 역을 삭제")
    @Test
    void deleteSectionException1() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), B역.getId()), LineResponse.class);

        // when
        ValidatableResponse response = 구간_삭제(line.getId(), A역.getId());

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 노선을 생성하고
     * When 노선 내 존재하지 않는 지하철 역을 삭제하면
     * Then 지하철 역을 삭제할 수 없다.
     */
    @DisplayName("노선 내 존재하지 않는 지하철 역을 삭제")
    @Test
    void deleteSectionException2() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), B역.getId()), LineResponse.class);
        구간_추가(B역.getId(), C역.getId(), 4, line.getId());
        구간_추가(C역.getId(), D역.getId(), 3, line.getId());

        // when
        ValidatableResponse response = 구간_삭제(line.getId(), E역.getId());

        // then
        응답_검증(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 노선 및 구간을 설정한다. (구간 추가만)
     * When, then 노선 내 순서대로 구간을 조회한다.
     */
    @DisplayName("노선에 포함된 구간 리스트 조회")
    @Test
    void showLineSections() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), D역.getId()), LineResponse.class);
        구간_추가(A역.getId(), B역.getId(), 4, line.getId());
        구간_추가(C역.getId(), D역.getId(), 3, line.getId());
        구간_추가(E역.getId(), A역.getId(), 15, line.getId());
        구간_추가(D역.getId(), F역.getId(), 30, line.getId());

        // when, then
        List<SectionResponse> responses = 구간_목록_조회(line.getId());
        assertThat(responses).hasSize(5);
    }

    /**
     * Given 노선 및 구간을 설정한다. (구간 추가 삭제)
     * When, then 노선 내 순서대로 구간을 조회한다.
     */
    @DisplayName("노선에 포함된 구간 리스트 조회")
    @Test
    void showLineSections2() {
        // given
        LineResponse line = 응답_객체_생성(노선_등록("2호선", "초록", 15, A역.getId(), D역.getId()), LineResponse.class);
        구간_추가(A역.getId(), B역.getId(), 4, line.getId());
        구간_삭제(line.getId(), B역.getId());
        구간_추가(C역.getId(), D역.getId(), 3, line.getId());
        구간_삭제(line.getId(), C역.getId());
        구간_추가(E역.getId(), A역.getId(), 15, line.getId());
        구간_삭제(line.getId(), E역.getId());
        구간_추가(D역.getId(), F역.getId(), 30, line.getId());
        구간_삭제(line.getId(), F역.getId());

        // when, then
        List<SectionResponse> responses = 구간_목록_조회(line.getId());
        assertThat(responses).hasSize(1);
    }

    public static ValidatableResponse 구간_추가(long upStationId, long downStationId, int distance, long lineId) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);

        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/" + lineId + "/sections")
                .then().log().all();
    }

    public static ValidatableResponse 구간_삭제(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .when().delete("/lines/" + lineId + "/sections")
                .then().log().all();
    }

    public static List<SectionResponse> 구간_목록_조회(Long lineId) {
        ValidatableResponse listResponse = RestAssured.given().log().all()
                .when().get("/lines/" + lineId + "/sections")
                .then().log().all();
        return getJsonPathForResponse(listResponse).getList("$", SectionResponse.class);
    }
}
