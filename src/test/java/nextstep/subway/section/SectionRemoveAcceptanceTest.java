package nextstep.subway.section;

import nextstep.subway.base.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@DisplayName("지하철 구간 제거 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SectionRemoveAcceptanceTest extends AcceptanceTest {

    /**
     *  Given 지하철 노선(A-B-C)을 생성하고
     *  When 지하철 노선의 상행 종점(A)를 제거할 경우
     *  Then 지하철 노선의 상행 종점은 B가 된다.
     */
    @DisplayName("상행 종점을 제거한다.")
    @Test
    void removeSectionOfHeadLine() {

    }

    /**
     *  Given 지하철 노선(A-B-C)을 생성하고
     *  When 지하철 노선의 하행 종점(C)를 제거할 경우
     *  Then 지하철 노선의 하행 종점은 B가 된다.
     */
    @DisplayName("하행 종점을 제거한다.")
    @Test
    void removeSectionEndOfLine() {

    }

    /**
     *  Given 지하철 노선(A-B-C)을 생성하고
     *  When 지하철 노선의 중간역(B)를 제거할 경우
     *  Then 지하철 노선이 (A-C)로 재조정된다.
     */
    @DisplayName("중간 역을 제거한다.")
    @Test
    void removeMiddleStation() {

    }

    /**
     *  When 구간이 하나인 지하철 노선을 제거 하려는 경우
     *  Then 제거할 수 없다.
     */
    @DisplayName("구간이 하나인 지하철 노선은 제거할 수 없다.")
    @Test
    void removeSectionOnlyOneExist() {

    }

    /**
     *  When 노선에 포함되지 않는 역을 제거하려는 경우
     *  Then 제거할 수 없다.
     */
    @DisplayName("노선에 포함되지 않는 역을 제거할 수 없다.")
    @Test
    void removeStationNotContainsLine() {

    }
}
