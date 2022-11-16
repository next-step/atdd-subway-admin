package nextstep.subway.section;

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

import static nextstep.subway.section.LineSectionStep.*;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineSectionTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @LocalServerPort
    int port;

    private int lineId;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        // db 초기화
        databaseCleanup.execute();

        // 2개 지하철역 등록, 노선 1개 등록
        lineId = 역_3개와_노선을_생성한다().jsonPath().get("id");
    }

    /**
     * Given 2개의 지하철역이 등록되어 있고, 노선이 등록되어있다
     * When 역 사이에 새로운 역이 등록된다
     * Then 새로운 구간을 확인할 수 있다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 수 있다")
    @Test
    void createSection() {
        //given -> beforeEach

        //when
        ExtractableResponse<Response> savedSection = 역_사이에_새로운_역을_등록한다(lineId);

        //then
        구간_추가_등록_결과_확인(lineId, savedSection, 2, "sections");
    }

    /**
     * Given 2개의 지하철역이 등록되어 있고, 노선이 등록되어있다
     * When 동일한 구간을 추가하면
     * Then 예외를 던진다
     */
    @DisplayName("중복된 구간을 등록한다면 예외를 던진다")
    @Test
    void 중복_구간_예외() {
        // given -> beforeEach

        // when
        ExtractableResponse<Response> response = 구간_생성_요청(lineId, 1L, 2L, 2);

        // then
        구간_등록_실패(response);
    }

    /**
     * Given 5개의 지하철역이 등록되어 있고, 노선이 등록되어있다
     * When 지하철 역 중 하나도 포함되어있지 않으면
     * Then 예외를 던진다
     */
    @DisplayName("지하철 역 중 하나도 포함되어있지 않으면 예외를 던진다")
    @Test
    void 구간_생성_포함_예외() {
        // given -> beforeEach
        추가_역을_3개_생성한다();

        // when
        ExtractableResponse<Response> response = 구간_생성_요청(lineId, 4L, 5L, 10);

        // then
        구간_등록_실패(response);
    }

    /**
     * Given 2개의 지하철역이 등록되어 있고, 노선이 등록되어있다
     * When 새로운 구간의 길이가 기존 구간의 길이보다 크거나 같으면
     * Then 예외를 던진다
     */
    @DisplayName("새로운 구간의 길이가 기존 구간 길이보다 크거나 같으면 예외를 던진다")
    @Test
    void 새로운_구간_길이_예외() {
        // given -> beforeEach

        // when
        ExtractableResponse<Response> response1 = 구간_생성_요청(lineId, 1L, 2L, 7);
        ExtractableResponse<Response> response2 = 구간_생성_요청(lineId, 1L, 2L, 8);

        // then
        구간_등록_실패(response1);
        구간_등록_실패(response2);
    }

    /**
     * Given 2개의 지하철역이 등록되어 있고, 노선이 등록되어있다
     * When 새로운 역을 상행 종점으로 생성하면
     * Then 생성된 지하철 구간을 확인할 수 있다
     */
    @DisplayName("새로운 역을 상행 종점으로 구간 등록할 수 있다")
    @Test
    void 새로운_역_상행_종점_등록_성공() {
        // given -> beforeEach

        // when 상행종점
        ExtractableResponse<Response> response = 구간_생성_요청(lineId, 3L, 1L, 3);

        // then
        구간_추가_등록_결과_확인(lineId, response, 2, "sections");
    }



    /**
     * Given 2개의 지하철역이 등록되어 있고, 노선이 등록되어있다
     * When 새로운 역을 하행 종점으로 생성하면
     * Then 생성된 지하철 구간을 확인할 수 있다
     */
    @DisplayName("새로운 역을 하행 종점으로 구간 등록할 수 있다")
    @Test
    void 새로운_역_하행_종점_등록_성공() {
        // given -> beforeEach

        // when 상행종점
        ExtractableResponse<Response> response = 구간_생성_요청(lineId, 2L, 3L, 10);

        // then
        구간_추가_등록_결과_확인(lineId, response, 2, "sections");
    }

}
