package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import javassist.NotFoundException;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineAcceptanceStep.*;
import static nextstep.subway.section.LineSectionStep.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
        lineId = 역_2개와_노선을_생성한다().jsonPath().get("id");
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
        구간_등록_성공_확인(savedSection);
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
        ExtractableResponse<Response> response = 중복_구간_생성_요청(lineId, 1L, 2L, 2);

        // then
        구간_등록_실패(response);

    }


}
