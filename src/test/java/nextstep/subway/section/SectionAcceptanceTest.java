package nextstep.subway.section;

import nextstep.subway.abstracts.DoBeforeEachAbstract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철구간 관련 기능")
public class SectionAcceptanceTest extends DoBeforeEachAbstract {

    @BeforeEach
    public void setUp() {
        super.setUp();

    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청

        // then
        // 지하철_노선에_지하철역_등록됨
    }
}
