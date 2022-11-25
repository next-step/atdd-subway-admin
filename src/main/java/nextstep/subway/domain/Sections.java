package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line")
    List<Section> sections = new ArrayList<>();

    public Sections addSection(Section newSection) {

        if (sections.isEmpty()) {
            setInitSection(newSection);
            return this;
        }

        if (exitsUpStation(newSection)) {
            insertNewDownStation(newSection);
            return this;
        }

        if (exitsDownStation(newSection)) {
            insertNewUpStation(newSection);
            return this;
        }

        return this;
    }

    private void setInitSection(Section newSection) {
        sections.add(new Section(newSection.getLine(), null, newSection.getUpStation(), 0));
        sections.add(new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
        sections.add(new Section(newSection.getLine(), newSection.getDownStation(), null, 0));
    }

    private void insertNewDownStation(Section newSection) {
        Section section = findSectionWithUpStation(newSection.getUpStation());
        Station preDownStation = section.getDownStation();
        int preDistance = section.getDistance();
        section.switchDownStation(newSection);
        addGeneratedSection(newSection.getLine(), newSection.getDownStation(), preDownStation, preDistance - newSection.getDistance());
    }

    private void insertNewUpStation(Section newSection) {
        Section section = findSectionWithDownStation(newSection.getDownStation());
        Station preUpStation = section.getUpStation();
        int preDistance = section.getDistance();
        section.switchUpStation(newSection);
        addGeneratedSection(newSection.getLine(), preUpStation, newSection.getUpStation(), preDistance - newSection.getDistance());
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

    private void addGeneratedSection(Line line, Station upStation, Station downStation, int distance) {
        sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<Section> getSectionList() {
        return sections;
    }
}
