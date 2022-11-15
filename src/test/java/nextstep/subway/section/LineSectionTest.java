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

import java.util.List;

import static nextstep.subway.line.LineAcceptanceStep.*;
import static nextstep.subway.section.LineSectionStep.역_2개와_노선을_생성한다;
import static nextstep.subway.section.LineSectionStep.역_사이에_새로운_역을_등록한다;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineSectionTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        // db 초기화
        databaseCleanup.execute();
    }

    /**
     * Given 2개의 지하철역이 등록되어 있고, 노선이 등록되어있다
     * When 역 사이에 새로운 역이 등록된다
     * Then 새로운 구간을 확인할 수 있다
     */
    @DisplayName("역 사이에 새로운 역 등록")
    @Test
    void createSection() {
        //given
        역_2개와_노선을_생성한다();

        //when
        역_사이에_새로운_역을_등록한다();
        List<String> allLineNames = 모든_노선_이름을_조회한다();

        //then
        노선_이름이_조회된다(allLineNames, "2호선");
    }


}
