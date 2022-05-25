package nextstep.subway.section.domain;

import nextstep.subway.SectionUtils;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Sections 클래스 테스트")
class SectionsTest {

    @DisplayName("다른 Section를 추가하여 반영된 Station 정보들을 가져올 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "강남역, 판교역, '강남역,판교역,정자역'",
            "정자역, 광교역, '판교역,정자역,광교역'",
    })
    void successfulAdd(String upStationName, String downStationName, String expectedStationNames) {
        Sections sections = new Sections();
        sections.add(SectionUtils.generateSection("판교역", "정자역", 10L));
        Section otherSection = SectionUtils.generateSection(upStationName, downStationName, 9L);

        sections.add(otherSection);

        List<String> expected = Arrays.asList(expectedStationNames.split(","));
        assertAll(
                () -> assertThat(sections.getStations()).hasSize(expected.size()),
                () -> assertThat(toStationNames(sections)).containsExactlyElementsOf(expected)
        );
    }

    @DisplayName("Sections에 다른 Section를 추가하지 못하고 예외 발생하고 기존과 동일한 Station 정보들을 가져온다.")
    @ParameterizedTest
    @CsvSource({
            "청계산입구역, 정자역",
            "정자역, 청계산입구역",
            "강남역, 판교역",
            "청계산입구역, 판교역"
    })
    void failureAdd(String upStationName, String downStationName) {
        Sections sections = new Sections();
        sections.add(SectionUtils.generateSection("청계산입구역", "정자역", 10L));
        Section otherSection = SectionUtils.generateSection(upStationName, downStationName, 11L);

        List<String> expected = Arrays.asList("청계산입구역", "정자역");
        assertAll(
                () -> assertThatThrownBy(() -> sections.add(otherSection)),
                () -> assertThat(sections.getStations()).hasSize(expected.size()),
                () -> assertThat(toStationNames(sections)).containsExactlyElementsOf(expected)
        );
    }

    private List<String> toStationNames(Sections sections) {
        return sections.getStations()
                       .stream()
                       .map(Station::getName)
                       .collect(Collectors.toList());
    }
}
