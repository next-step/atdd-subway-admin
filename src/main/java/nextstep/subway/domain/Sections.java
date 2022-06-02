package nextstep.subway.domain;

import static nextstep.subway.message.ErrorMessage.SECTION_ADD_DISTANCE_IS_BIG;
import static nextstep.subway.message.ErrorMessage.SECTION_IS_NO_SEARCH;
import static nextstep.subway.message.ErrorMessage.SECTION_UP_STATION_AND_DOWN_STATION_EXIST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "sectionId")
    private List<Section> sectionElement = new ArrayList<>();

    protected Sections() {

    }

    public Sections(Section newSection) {
        this.sectionElement.add(newSection);
    }


    public List<Section> getList() {
        return Collections.unmodifiableList(sectionElement);
    }

    public void addSection(Section newSection) {
        validExistUpStationAndDownStation(newSection);
        if (isBetweenAddSection(newSection)) {
            addBetweenSection(newSection);
            return;
        }
        this.sectionElement.add(newSection);
    }


    private void addBetweenSection(Section newSection) {
        Section beforeSection = findAddSection(newSection);
        if (!validDistance(newSection, beforeSection)) {
            throw new IllegalStateException(SECTION_ADD_DISTANCE_IS_BIG.toMessage());
        }
        beforeSection.minusDistance(newSection.getDistance());
        this.sectionElement.add(newSection);
    }

    private boolean validDistance(Section newSection, Section beforeSection) {
        return newSection.getDistance().isLess(beforeSection.getDistance());
    }

    private boolean isBetweenAddSection(Section newSection) {
        return isExistDownStation(newSection);
    }

    private Section findAddSection(Section newSection) {
        return sectionElement.stream()
                .filter((section -> section.getDownStation().equals(newSection.getDownStation())))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(SECTION_IS_NO_SEARCH.toMessage()));
    }

    private void validExistUpStationAndDownStation(Section newSection) {
        if (isExistUpStation(newSection) && isExistDownStation(newSection)) {
            throw new IllegalArgumentException(SECTION_UP_STATION_AND_DOWN_STATION_EXIST.toMessage());
        }
    }

    private boolean isExistDownStation(Section newSection) {
        return sectionElement.stream()
                    .anyMatch(section ->
                            section.getDownStation().equals(newSection.getDownStation()));
    }

    private boolean isExistUpStation(Section newSection) {
        return sectionElement.stream()
                .anyMatch(section -> section.getUpStation().equals(newSection.getUpStation()));
    }

}
