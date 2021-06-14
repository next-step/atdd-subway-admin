package nextstep.subway.section.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 정보 파라미터 테스트")
class SectionRequestTest {

    @Test
    void 구간_파라미터_생성() {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        assertThat(sectionRequest.getUpStationId()).isEqualTo(1L);
        assertThat(sectionRequest.getDownStationId()).isEqualTo(2L);
        assertThat(sectionRequest.getDistance()).isEqualTo(10);
    }


}