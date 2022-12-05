package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.TestUtil;
import nextstep.subway.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

@DisplayName("지하철 노선 관련 기능")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends LineAcceptanceTestFixture {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    void 지하철_노선_생성_확인() {
        //When
        ExtractableResponse<Response> response = 지하철_노선_생성("2호선", "green");

        //Then
        TestUtil.응답확인(response, HttpStatus.CREATED);

        //Then
        List<Line> 조회된_노선_목록 = 노선목록(지하철_노선_목록_조회());
        assertThat(조회된_노선_목록).containsAnyOf(노선정보(response));
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void 지하철_노선_목록_조회_확인() {
        //Given
        List<Line> 생성된_노선_목록 = new ArrayList<>();
        생성된_노선_목록.add(노선정보(지하철_노선_생성("2호선", "green")));
        생성된_노선_목록.add(노선정보(지하철_노선_생성("3호선", "orange")));

        //When
        ExtractableResponse<Response> response = 지하철_노선_목록_조회();

        //Then
        TestUtil.응답확인(response, HttpStatus.OK);

        //Then
        List<Line> 조회된_노선_목록 = 노선목록(response);
        생성된_노선_목록.forEach(l -> assertThat(조회된_노선_목록.contains(l)).isTrue());
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다")
    @Test
    void 지하철_노선_조회_확인() {
        //Given
        Line 생성된_노선 = 노선정보(지하철_노선_생성("2호선", "green"));

        //When
        ExtractableResponse<Response> response = 지하철_노선_조회(생성된_노선.getId());

        //Then
        TestUtil.응답확인(response, HttpStatus.OK);

        //Then
        Line 조회된_노선 = 노선정보(response);
        assertThat(조회된_노선).isEqualTo(생성된_노선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다")
    @Test
    void 지하철_노선_수정_확인() {
        //Given
        Line 생성된_노선 = 노선정보(지하철_노선_생성("2호선", "green"));
        Long lineId = 생성된_노선.getId();

        //When
        Map<String, String> patchParams = new HashMap<>();
        patchParams.put("name", "3호선");
        patchParams.put("color", "orange");
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(patchParams)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().patch("/lines/" + lineId)
                        .then().log().all()
                        .extract();

        //Then
        TestUtil.응답확인(response, HttpStatus.OK);

        //Then
        Line 조회된_노선 = 노선정보(지하철_노선_조회(lineId));
        assertAll(
                () -> assertThat(조회된_노선.getName()).isEqualTo("3호선"),
                () -> assertThat(조회된_노선.getColor()).isEqualTo("orange")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void 지하철_노선_삭제_확인() {
        //Given
        Line 생성된_노선 = 노선정보(지하철_노선_생성("2호선", "green"));

        //When
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().delete("/lines/" + 생성된_노선.getId())
                        .then().log().all()
                        .extract();

        //Then
        TestUtil.응답확인(response, HttpStatus.NO_CONTENT);

        //Then
        List<Line> 조회된_노선_목록 = 노선목록(지하철_노선_목록_조회());
        assertThat(조회된_노선_목록.contains(생성된_노선)).isFalse();
    }
}
