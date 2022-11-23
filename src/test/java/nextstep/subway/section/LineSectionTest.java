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

import static nextstep.subway.line.LineAcceptanceStep.*;
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
    void 새로운_구간_추가() {
        //given -> beforeEach

        //when
        ExtractableResponse<Response> savedSection = 구간_생성_요청(lineId, 1L, 3L, 4);

        //then
        구간_추가_등록_결과_확인(savedSection, 3, 7);
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
        구간_추가_등록_결과_확인(response, 3, 10);
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
        구간_추가_등록_결과_확인(response, 3, 17);
    }

    /**
     * Given 3개 이상의 지하철 역이 등록되어 있다
     * When 상행 종점이 제거 될 경우
     * Then 다음으로 오던 역이 종점이 된다
     */
    @DisplayName("상행 종점을 제거할 수 있다")
    @Test
    void 상행_종점_제거() {
        // given -> beforeEach
        구간_생성_요청(lineId, 1L, 3L, 4);

        // when
        ExtractableResponse<Response> response = 구간_삭제_호출(lineId, 1);

        // then
        상행_구간_삭졔_확인(lineId);
    }

    /**
     * Given 3개 이상의 지하철 역이 등록되어 있다
     * When 하행 종점이 제거 될 경우
     * Then 전으로 오던 역이 종점이 된다
     */
    @DisplayName("하행 종점을 제거할 수 있다")
    @Test
    void 하행_종점_제거() {
        // given -> beforeEach
        구간_생성_요청(lineId, 1L, 3L, 4);

        // when
        구간_삭제_호출(lineId, 2);

        // then
        하행_구간_삭졔_확인(lineId);
    }

    /**
     * Given 3개 이상의 지하철 역이 등록되어 있다
     * When 증간 구간이 제거된 경우
     * Then 처음과 끝 역이 재배체 되고
     * Then 두 구간 거리는 합쳐진다
     */
    @DisplayName("중간 종점을 제거할 수 있다")
    @Test
    void 중간_구간_제거() {
        // given -> beforeEach
        구간_생성_요청(lineId, 1L, 3L, 4);

        // when
        ExtractableResponse<Response> response = 구간_삭제_호출(lineId, 3);

        // then
        중간_구간_삭졔_확인(lineId);
    }

    /**
     * Given 2개의 지하철 역이 노선으로 등록되어 있다
     * When 구간을 제거할 경우
     * Then 구간이 하나인 노선은 제거할 수 없다
     */
    @DisplayName("구간이 하나인 노선은 제거할 수 없다")
    @Test
    void 구간이_하나인_노선_제거_에러() {
        // given -> beforeEach

        // when
        ExtractableResponse<Response> response = 구간_삭제_호출(lineId, 1);

        // then
        구간_삭제_실패(response);
    }

    /**
     * Given 2개의 지하철 역이 노선으로 등록되어 있다
     * When 구간에 포함되지 않은 역을 삭제할 경우
     * Then 제거할 수 없다
     */
    @DisplayName("구간에 포함되지 않은 역을 삭제할 수 없다")
    @Test
    void 구간에_포함되지_않은_역_제거_에러() {
        // given -> beforeEach
        추가_역을_3개_생성한다();

        // when
        ExtractableResponse<Response> response = 구간_삭제_호출(lineId, 5);

        // then
        구간_삭제_실패(response);
    }
}
