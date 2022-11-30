package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    private static final String STATIONS_DO_NOT_EXIST_EXCEPTION = "상하행역 모두 존재하지 않아 구간을 생성할 수 없습니다.";
    private static final String STATIONS_ALREADY_EXIT_EXCEPTION = "상하행역 기존에 존재하므로 구간을 생성할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    List<Section> sections = new ArrayList<>();

    public Sections addSection(Section newSection) {

        if (sections.isEmpty()) {
            sections.add(newSection);
            return this;
        }

        validateNewSection(newSection);

        Section switchSection = getSwitchTarget(newSection);
        switchSectionValue(switchSection, newSection);
        sections.add(newSection);
        sortSections();

        return this;
    }

    private void switchSectionValue(Section previousSection, Section newSection) {
        if (previousSection != null) {
            previousSection.switchValue(newSection);
        }
    }

    private Section getSwitchTarget(Section newSection) {

        Section previousSection = findSectionWithUpStation(newSection);

        if (previousSection == null) {
            previousSection = findSectionWithDownStation(newSection);
        }

        return previousSection;
    }


    private Section findSectionWithUpStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasUpStation(newSection.getUpStation()))
                .findFirst()
                .orElse(null);
    }

    private Section findSectionWithDownStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasDownStation(newSection.getDownStation()))
                .findFirst()
                .orElse(null);
    }

    public List<Section> getSectionList() {
        return sections;
    }

    public void validateNewSection(Section newSection) {

        boolean containsUpStation = sections.stream()
                .anyMatch(section -> section.containsStation(newSection.getUpStation()));

        boolean containsDownStation = sections.stream()
                .anyMatch(section -> section.containsStation(newSection.getDownStation()));

        if (containsUpStation && containsDownStation) {
            System.out.println(STATIONS_ALREADY_EXIT_EXCEPTION);
            throw new IllegalArgumentException(STATIONS_ALREADY_EXIT_EXCEPTION);
        }

        if (!containsUpStation && !containsDownStation) {
            System.out.println(STATIONS_DO_NOT_EXIST_EXCEPTION);
            throw new IllegalArgumentException(STATIONS_DO_NOT_EXIST_EXCEPTION);
        }
    }

    private void sortSections() {
        //상행종점역 찾기
        Section firstSection = sections.stream()
                .filter(section -> getPreviousSection(section) == null)
                .findAny()
                .get();

        //상행종점역부터 정렬
        List<Section> sortedSections = new ArrayList<>();
        sortedSections.add(firstSection);

        Section previousSection = firstSection;

        while (sortedSections.size() != sections.size()) {
            Section nextSection = getNextSection(previousSection);
            sortedSections.add(nextSection);
            previousSection = nextSection;

        }

        sections = sortedSections;
    }

    private Section getPreviousSection(Section tmp) {
        return sections.stream()
                .filter(section -> section.hasDownStation(tmp.getUpStation()))
                .findAny()
                .orElse(null);
    }

    private Section getNextSection(Section tmp) {
        Section nextSection = sections.stream()
                .filter(section -> section.hasUpStation(tmp.getDownStation()))
                .findAny()
                .orElse(null);

        return nextSection;
    }

    public Sections getSortedSections() {
        sortSections();
        return this;
    }
}
