package nextstep.subway.section;

import static nextstep.subway.AcceptanceTestFactory.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import nextstep.subway.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.cleanUp();
    }

    /**
     * Given: 지하철 노선이 생성되어 있다.
     * When: 사용자는 지하철 구간 추가를 요청한다.
     * Then: 역 사이에 새로운 역이 추가된다.
     */
    @Test
    @DisplayName("기존 노선 구간 중간에 새로운 구간을 추가한다.")
    void addSection() {
        Long 애오개역_ID = 지하철_역_생성_ID_추출("애오개역");
        Long 마포역_ID = 지하철_역_생성_ID_추출("마포역");
        Long 노선_ID = 지하철_노선_생성_ID_추출(지하철_노선_정보_생성("5호선", "bg-red-600", 애오개역_ID, 마포역_ID, 10));

        Long 공덕역_ID = 지하철_역_생성_ID_추출("공덕역");
        Map<String, Object> 구간_정보 = 지하철_구간_정보_생성(애오개역_ID, 공덕역_ID, 5);
        ExtractableResponse<Response> 지하철_노선_구간_추가_응답_결과 = 지하철_노선_구간_추가_요청(노선_ID, 구간_정보);

        수정_성공_확인(지하철_노선_구간_추가_응답_결과);
    }
}
