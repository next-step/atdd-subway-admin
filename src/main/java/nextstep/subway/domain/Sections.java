package nextstep.subway.domain;

import nextstep.subway.exception.InvalidParameterException;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public void infix(Section infixSection) {
        Section needChangedStation = getNeedChangedStationSection(infixSection);
        needChangedStation.changed(infixSection);
        add(infixSection);
    }

    public List<Section> getList() {
        return sections;
    }

    private Section getNeedChangedStationSection(Section infixSection) {
        sections.stream().forEach(System.out::println);
        Section changedStationSection = getInnerStationSection(infixSection);

        if (changedStationSection == null) {
            return getOuterStationSection(infixSection);
        }

        return changedStationSection;
    }

    private Section getInnerStationSection(Section infixSection) {
        Section changedStationSection = sections
                .stream()
                .filter(section -> section.isUpStationEqualsId(infixSection.getUpStation().getId()))
                .findAny()
                .orElse(null);

        if (changedStationSection == null) {
            return sections
                    .stream()
                    .filter(section -> section.isDownStationEqualsId(infixSection.getDownStation().getId()))
                    .findAny()
                    .orElse(null);
        }
        return changedStationSection;
    }

    private Section getOuterStationSection(Section infixSection) {
        Section changedStationSection = sections
                .stream()
                .filter(section -> section.isUpStationEqualsId(infixSection.getDownStation().getId()))
                .findAny()
                .orElse(null);

        if (changedStationSection == null) {
            changedStationSection = sections
                    .stream()
                    .filter(section -> section.isDownStationEqualsId(infixSection.getUpStation().getId()))
                    .findAny()
                    .orElse(null);
        }

        if (changedStationSection == null) {
            throw new InvalidParameterException("상행역과 하행역 모두 노선에 등록되어있지 않습니다.");
        }

        return changedStationSection;
    }

}
