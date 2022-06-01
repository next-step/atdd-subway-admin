package nextstep.subway.domain;

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
            throw new IllegalStateException("구간에 추가할 길이가 너무 깁니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("찾을수 없습니다."));
    }

    private void validExistUpStationAndDownStation(Section newSection) {
        if (isExistUpStation(newSection) && isExistDownStation(newSection)) {
            throw new IllegalArgumentException("상행선과 하행선이 이미 존재하는 구간입니다.");
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
