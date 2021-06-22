package nextstep.subway.line.domain;

import static nextstep.subway.station.domain.StationTest.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    @DisplayName("두 구간의 거리를 비교한다")
    void isLongerThan() {
        Section fifty = new Section(강남역, 양재역, 50);
        Section forty_nine = new Section(강남역, 양재역, 49);

        assertThat(fifty.isLongerThan(forty_nine)).isTrue();
        assertThat(fifty.isLongerThan(fifty)).isFalse();
    }

    @Test
    @DisplayName("두 구간이 병합가능한지 검증한다")
    void isMergeableWith() {
        Section S1 = new Section(강남역, 양재역, 100);
        Section S2 = new Section(양재역, 광교중앙역, 30);
        Section S3 = new Section(광교중앙역, 광교역, 30);

        assertThat(S1.isMergeableWith(S2)).isTrue();
        assertThat(S2.isMergeableWith(S1)).isTrue();
        assertThat(S3.isMergeableWith(S1)).isFalse();
        assertThat(S3.isMergeableWith(S3)).isFalse();
    }

    @Test
    @DisplayName("두 구간을 머지하고 확인한다")
    void mergeWith() {
        Section S1 = new Section(강남역, 양재역, 100);
        Section S2 = new Section(양재역, 광교역, 30);
        Section newSection = S1.mergeWith(S2);

        assertThat(newSection.distance()).isEqualTo(130);
        assertThat(newSection.upStation()).isEqualTo(강남역);
        assertThat(newSection.downStation()).isEqualTo(광교역);
    }

    @Test
    @DisplayName("두 구간이 한쪽끝만 동일한지 확인한다")
    void matchesOnlyOneEndOf() {
        Section S1 = new Section(강남역, 양재역, 100);
        Section S2 = new Section(강남역, 광교중앙역, 30);
        Section S3 = new Section(광교중앙역, 광교역, 30);

        assertThat(S1.matchesOnlyOneEndOf(S2)).isTrue();
        assertThat(S2.matchesOnlyOneEndOf(S1)).isTrue();
        assertThat(S1.matchesOnlyOneEndOf(S1)).isFalse();
        assertThat(S1.matchesOnlyOneEndOf(S3)).isFalse();
    }

    @Test
    @DisplayName("다른 구간에 의해 기존구간이 밀려난 결과를 확인한다")
    void shiftedBy() {
        Section S1 = new Section(강남역, 광교역, 100);
        Section S2 = new Section(강남역, 양재역, 30);
        Section newSection = S1.shiftedBy(S2);

        assertThat(newSection.distance()).isEqualTo(70);
        assertThat(newSection.upStation()).isEqualTo(양재역);
        assertThat(newSection.downStation()).isEqualTo(광교역);
    }
}
