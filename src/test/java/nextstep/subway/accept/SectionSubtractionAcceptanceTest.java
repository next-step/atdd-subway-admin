package nextstep.subway.accept;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 제거 기능")
public class SectionSubtractionAcceptanceTest {

    /**
     *  Given 노선과 구간이 주어지고
     *  When 구간을 제거하면
     *  Then 해당 역을 찾을 수 없다
     */
    // TODO : 시작 구간, 중간 구간, 마지막 구간 제거 테스트 진행
    @Test
    void deleteSection() {
    }

    /**
     *  Given 노선과 구간이 주어지고
     *  When 노선에 등록되지 않는 구간을 지우려고 하면
     *  Then 해당 구간은 존재하지 않는다고 에러가 발생한다
     */
    @Test
    void deleteNotFoundSection() {
    }

    /**
     *  Given 노선과 구간이 주어지고
     *  When 마지막 남은 구간을 지우려고 하면
     *  Then 해당 구간은 노선에 유일한 구간이여서 지울 수 없다고 에러가 발생한다
     */
    @Test
    void deleteLastSection() {
    }
}
