package nextstep.subway.section.domain;

import nextstep.subway.SectionUtils;
import nextstep.subway.station.domain.Distance;
import nextstep.subway.station.exception.InvalidDistanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Section 클래스 테스트")
class SectionTest {

    @DisplayName("한쪽 Station만이 일치하여 Section과 다른 Section간의 연결이 가능하다.")
    @ParameterizedTest
    @CsvSource({
            "강남역, 판교역",
            "정자역, 광교역",
    })
    void successfulIsConnectable(String upStationName, String downStationName) {
        Section section = SectionUtils.generateSection("판교역", "정자역", 10L);
        Section otherSection = SectionUtils.generateSection(upStationName, downStationName, 9L);

        assertThat(section.isConnectable(otherSection)).isTrue();
    }

    @DisplayName("양쪽 Station이 일치하여 Section과 다른 Section간의 연결이 불가능하다.")
    @Test
    void failureIsConnectable() {
        Section section = SectionUtils.generateSection("판교역", "정자역", 10L);
        Section otherSection = SectionUtils.generateSection("정자역", "판교역", 9L);

        assertThat(section.isConnectable(otherSection)).isFalse();
    }

    @DisplayName("양쪽 Station이 일치하거나 모두 일치하지 않아 Section과 다른 Section간의 연결이 불가능하다.")
    @Test
    void failureIsConnectableWhenNotFound() {
        Section section = SectionUtils.generateSection("판교역", "정자역", 10L);
        Section otherSection = SectionUtils.generateSection("청계산입구역", "양재역", 9L);

        assertThat(section.isConnectable(otherSection)).isFalse();
    }

    @DisplayName("Section과 다른 Section와 재배치 실패")
    @ParameterizedTest
    @CsvSource({
            "청계산입구역, 판교역",
            "판교역, 정자역",
    })
    void failureRelocate(String upStationName, String downStationName) {
        Section section = SectionUtils.generateSection("청계산입구역", "정자역", 10L);
        Section otherSection = SectionUtils.generateSection(upStationName, downStationName, 11L);

        assertThatThrownBy(() -> {
            section.relocate(otherSection);
        }).isInstanceOf(InvalidDistanceException.class);
    }

    @DisplayName("Section과 다른 Section와의 상행 재배치")
    @Test
    void relocateUp() {
        Section section = SectionUtils.generateSection("청계산입구역", "정자역", 10L);
        Section otherSection = SectionUtils.generateSection("청계산입구역", "판교역", 9L);

        section.relocate(otherSection);

        assertAll(
                () -> assertThat(section.getUpStation().getName()).isEqualTo("청계산입구역"),
                () -> assertThat(section.getDownStation().getName()).isEqualTo("판교역"),
                () -> assertThat(otherSection.getUpStation().getName()).isEqualTo("판교역"),
                () -> assertThat(otherSection.getDownStation().getName()).isEqualTo("정자역"),
                () -> assertThat(section.getDistance()).isEqualTo(new Distance(1L)),
                () -> assertThat(otherSection.getDistance()).isEqualTo(new Distance(9L))
        );
    }

    @DisplayName("Section과 다른 Section와의 하행 재배치")
    @Test
    void relocateDown() {
        Section section = SectionUtils.generateSection("청계산입구역", "정자역", 10L);
        Section otherSection = SectionUtils.generateSection("판교역", "정자역", 9L);

        section.relocate(otherSection);

        assertAll(
                () -> assertThat(section.getUpStation().getName()).isEqualTo("청계산입구역"),
                () -> assertThat(section.getDownStation().getName()).isEqualTo("판교역"),
                () -> assertThat(otherSection.getUpStation().getName()).isEqualTo("판교역"),
                () -> assertThat(otherSection.getDownStation().getName()).isEqualTo("정자역"),
                () -> assertThat(section.getDistance()).isEqualTo(new Distance(1L)),
                () -> assertThat(otherSection.getDistance()).isEqualTo(new Distance(9L))
        );
    }

    @DisplayName("Section과 다른 Section와의 병합")
    @Test
    void merge() {
        Section section = SectionUtils.generateSection("청계산입구역", "판교역", 10L);
        Section otherSection = SectionUtils.generateSection("판교역", "정자역", 9L);

        section.merge(otherSection);

        assertAll(
                () -> assertThat(section.getUpStation().getName()).isEqualTo("청계산입구역"),
                () -> assertThat(section.getDownStation().getName()).isEqualTo("정자역"),
                () -> assertThat(section.getDistance()).isEqualTo(new Distance(19L))
        );
    }
}
