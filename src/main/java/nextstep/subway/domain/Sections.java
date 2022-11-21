package nextstep.subway.domain;

import nextstep.subway.exception.InvalidParameterException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public void infix(Section infixSection) {
        checkContainsAllSectionStation(infixSection);
        Section needChangedStation = getNeedChangedStationSection(infixSection);
        needChangedStation.changed(infixSection);
        add(infixSection);
    }

    private void checkContainsAllSectionStation(Section infixSection) {
        sections.stream().forEach(section -> {
            if (section.containsAllStation(infixSection)) {
                throw new InvalidParameterException("상행역과 하행역이 모두 노선에 등록되어 있는 경우 새롭게 등록할 수 없습니다.");
            }
        });
    }

    public List<Section> getList() {
        return sections;
    }

    private Section getNeedChangedStationSection(Section infixSection) {
        Section changedStationSection = getInnerStationSection(infixSection);

        if (changedStationSection == null) {
            return getOuterStationSection(infixSection);
        }

        return changedStationSection;
    }

    private Section getInnerStationSection(Section infixSection) {
        Section changedStationSection = sections
                .stream()
                .filter(section -> section.isUpStationEqualsId(infixSection.getUpStation()))
                .findAny()
                .orElse(null);

        if (isNothingToChange(changedStationSection)) {
            return sections
                    .stream()
                    .filter(section -> section.isDownStationEqualsId(infixSection.getDownStation()))
                    .findAny()
                    .orElse(null);
        }
        return changedStationSection;
    }

    private Section getOuterStationSection(Section infixSection) {
        Section changedStationSection = sections
                .stream()
                .filter(section -> section.isUpStationEqualsId(infixSection.getDownStation()))
                .findAny()
                .orElse(null);

        if (isNothingToChange(changedStationSection)) {
            changedStationSection = sections
                    .stream()
                    .filter(section -> section.isDownStationEqualsId(infixSection.getUpStation()))
                    .findAny()
                    .orElse(null);
        }

        if (isNothingToChange(changedStationSection)) {
            throw new InvalidParameterException("상행역과 하행역 모두 노선에 등록되어있지 않습니다.");
        }

        return changedStationSection;
    }

    private boolean isNothingToChange(Section changedStationSection) {
        return changedStationSection == null;
    }

}
