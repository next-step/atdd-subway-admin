package nextstep.subway.domain;

import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.InvalidParameterException;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line")
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

        if (isNothingToChange(changedStationSection)) {
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

        if (isNothingToChange(changedStationSection)) {
            changedStationSection = sections
                    .stream()
                    .filter(section -> section.isDownStationEqualsId(infixSection.getUpStation().getId()))
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

    public Set<StationResponse> toStationSet() {
        Set<StationResponse> stationResponses = new HashSet<>();
        this.getList().stream().forEach(section -> {
            stationResponses.add(section.getUpStation().toStationResponse());
            stationResponses.add(section.getDownStation().toStationResponse());
        });
        return stationResponses;
    }
}
