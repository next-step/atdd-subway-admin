package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {

        if (sections.isEmpty()) {
            setInitSection(newSection);
            return;
        }

        addNewSection(newSection);

    }

    private void setInitSection(Section newSection) {
        sections.add(new Section(newSection.getLine(), null, newSection.getUpStation(), 0));
        sections.add(new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
        sections.add(new Section(newSection.getLine(), newSection.getDownStation(), null, 0));
    }

    private void addNewSection(Section newSection) {
        //기존 상행역과 신규역인 경우
        if (exitsUpStation(newSection)) {
            Section section = findSectionWithUpStation(newSection.getUpStation());
            Station preDownStation = section.getDownStation();
            section.switchDownStation(newSection.getDownStation());
            addGenerateSection(newSection.getLine(), newSection.getDownStation(), preDownStation, section.getDistance() - newSection.getDistance());
            section.updateDistance(newSection.getDistance());
            return;
        }

        //신규역과 기존 하행역인 경우
        if (exitsDownStation(newSection)) {
            Section section = findSectionWithDownStation(newSection.getDownStation());
            Station preUpStation = section.getUpStation();
            section.switchUpStation(newSection.getUpStation());
            addGenerateSection(newSection.getLine(), preUpStation, newSection.getUpStation(), newSection.getDistance());
            section.updateDistance(section.getDistance() - newSection.getDistance());
            return;
        }
    }

    private boolean exitsUpStation(Section section) {
        return findSectionWithUpStation(section.getUpStation()) != null;
    }

    private Section findSectionWithUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst()
                .orElse(null);
    }

    private boolean exitsDownStation(Section section) {
        return findSectionWithDownStation(section.getDownStation()) != null;
    }

    private Section findSectionWithDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasDownStation(station))
                .findFirst()
                .orElse(null);
    }

    private void addGenerateSection(Line line, Station upStation, Station downStation, int distance) {
        sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<Section> getSectionList() {
        return sections;
    }
}
