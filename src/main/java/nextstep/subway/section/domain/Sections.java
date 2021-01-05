package nextstep.subway.section.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@NoArgsConstructor
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Section add(Section section) {
        if (sections.size() > 0) {
            checkValidateSection(section);
            adjustSections(section);
        }

        sections.add(section);
        return section;
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .sorted(Comparator.comparingLong(Station::getId))
                .collect(Collectors.toList());
    }

    public void removeByStation(Line line, Station station) {
        checkArgument(sections.size() > 1, "구간 개수가 하나이거나 없습니다");

        List<Section> matchSections = matchedSectionsByStation(station);

        checkArgument(matchSections.size() > 0, "노선에 등록 안된 역입니다");

        if (matchSections.size() > 1) {
            Section newSection = matchSections.stream()
                    .sorted(this::comparing)
                    .reduce(combineSection(line))
                    .orElseThrow(() -> new IllegalArgumentException("구간 결합에 실패하였습니다"));

            sections.add(newSection);
        }

        sections.removeAll(matchSections);
    }

    private void adjustSections(Section newSection) {
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);

            AdjustSectionType adjustSectionType = AdjustSectionType.valueOf(section, newSection);
            if (adjustSectionType != AdjustSectionType.NONE) {
                checkDistance(section, newSection.getDistance());
                sections.add(adjustSectionType.createSection(section, newSection));
                sections.remove(i);
                break;
            }
        }
    }

    private BinaryOperator<Section> combineSection(Line line) {
        return (frontSection, backSection) -> new Section(
                line,
                frontSection.getUpStation(),
                backSection.getDownStation(),
                frontSection.getDistance() + backSection.getDistance());
    }

    private List<Section> matchedSectionsByStation(Station station) {
        return sections.stream()
                .filter(section -> station.equals(section.getUpStation()) || station.equals(section.getDownStation()))
                .collect(Collectors.toList());
    }

    private int comparing(Section s1, Section s2) {
        return s1.getDownStation().equals(s2.getUpStation()) ? -1 : 1;
    }

    private void checkDistance(Section oldSection, int distance) {
        if (oldSection.getDistance() <= distance) {
            throw new IllegalArgumentException(
                    String.format("기존 역 사이 길이 (%d) 보다 크거나 같습니다: %d", oldSection.getDistance(), distance));
        }
    }

    private void checkValidateSection(Section section) {
        long count = sections.stream()
                .flatMap(sect -> sect.getStations().stream())
                .filter(station -> station.equals(section.getUpStation()) || station.equals(section.getDownStation()))
                .count();
        if (count != 1) {
            throw new IllegalArgumentException("기존에 존재하는 구간이거나 상행역 또는 하행역 모두 등록되어 있지 않습니다.");
        }
    }
}
