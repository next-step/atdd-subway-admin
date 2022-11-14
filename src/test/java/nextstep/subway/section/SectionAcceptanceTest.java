package nextstep.subway.section;

import nextstep.subway.base.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {

    /**
     *  Given 지하철 노선을 생성하고
     *  When 지하철 구간을 추가하면
     *  Then 노선에 새로운 역이 추가된다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    void addSectionBetweenLine() {

    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 새로운 역을 상행 종점으로 등록할 경우
     *  Then 노선의 상행 종점이 변경된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    void addSectionHeadOfLine() {

    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 새로운 역을 하행 종점으로 등록할 경우
     *  Then 노선의 하행 종점이 변경된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    void addSectionEndOfLine() {

    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 역 사이의 거리가 기존 역 사이의 거리보다 크거나 같은 경우
     *  Then 등록할 수 없다
     */
    @DisplayName("추가하려는 역 사이의 거리가 기존 역 사이의 거리보다 크거나 같다면 등록할 수 없다")
    void addSectionGreaterThanExistDistance() {

    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 이미 노선에 등록된 구간(상행, 하행)을 등록할 경우
     *  Then 등록할 수 없다
     */
    @DisplayName("이미 노선에 존재하는 구간을 등록 할 수 없다")
    void addSectionDuplicateInLine() {

    }

    /**
     *  Given 지하철 노선을 생성하고
     *  When 노선에 포함되지 않는 역이 있는 구간을 등록할 경우
     *  Then 등록할 수 없다
     */
    @DisplayName("노선에 존재하지 않는 역을 포함한 구간은 등록할 수 없다")
    void addSectionNotExistStationInLine() {

    }
}
