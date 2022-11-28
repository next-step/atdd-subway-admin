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

        Section previousSection = getPreviousSection(newSection);
        switchSectionValue(previousSection, newSection);
        sections.add(newSection);

        return this;
    }

    private void switchSectionValue(Section previousSection, Section newSection) {
        if (previousSection != null) {
            previousSection.switchValue(newSection);
        }
    }

    private Section getPreviousSection(Section newSection) {

        Section previousSection = findSectionWithUpStation(newSection);

        if (previousSection == null) {
            previousSection = findSectionWithDownStation(newSection);
        }

        return previousSection;
    }


    private Section findSectionWithUpStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasUpStation(newSection))
                .findFirst()
                .orElse(null);
    }

    private Section findSectionWithDownStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.hasDownStation(newSection))
                .findFirst()
                .orElse(null);
    }

    public List<Section> getSectionList() {
        return sections;
    }

    public void validateNewSection(Section newSection) {

        boolean containsUpStation = sections.stream()
                .filter(section -> section.containsStation(newSection.getUpStation()))
                .findAny()
                .isPresent();

        boolean containsDownStation = sections.stream()
                .filter(section -> section.containsStation(newSection.getDownStation()))
                .findAny()
                .isPresent();

        if (containsUpStation && containsDownStation) {
            System.out.println(STATIONS_ALREADY_EXIT_EXCEPTION);
            throw new IllegalArgumentException(STATIONS_ALREADY_EXIT_EXCEPTION);
        }

        if (!containsUpStation && !containsDownStation) {
            System.out.println(STATIONS_DO_NOT_EXIST_EXCEPTION);
            throw new IllegalArgumentException(STATIONS_DO_NOT_EXIST_EXCEPTION);
        }
    }
}
