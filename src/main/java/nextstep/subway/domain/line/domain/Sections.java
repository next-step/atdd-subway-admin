package nextstep.subway.domain.line.domain;

import nextstep.subway.domain.section.domain.Section;
import nextstep.subway.domain.section.domain.Distance;
import nextstep.subway.domain.section.exception.AlreadyRegisterSectionException;
import nextstep.subway.domain.section.exception.StandardStationNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getStationInOrder() {
        List<Section> sectionsResult = new ArrayList<>();

        Optional<Section> preStation = this.sections.stream()
                .filter(st -> st.getPreStation() == null)
                .findFirst();

        while (preStation.isPresent()) {
            final Section section = preStation.get();
            sectionsResult.add(section);

            preStation = sections.stream()
                    .filter(st -> st.getPreStation() != null)
                    .filter(st -> st.getPreStation().equals(section.getStation()))
                    .findFirst();
        }

        return sectionsResult;
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void createSection(Section newSection) {
        createSectionValid(newSection);

        final Section sectionStart = this.sections.stream()
                .filter(st -> st.getPreStation() == null)
                .findFirst()
                .get();

        final Section sectionEnd = this.sections.stream()
                .filter(st -> st.getPreStation() != null)
                .filter(st -> st.getPreStation().equals(sectionStart.getStation()))
                .findFirst()
                .get();

        if (addBetweenStation(newSection, sectionStart, sectionEnd)) return;

        if (addDownStation(newSection, sectionEnd)) return;

        addUpStation(newSection, sectionStart);
    }

    private boolean addBetweenStation(final Section newSection, final Section sectionStart, final Section sectionEnd) {
        if (newSection.getPreStation().equals(sectionStart.getStation())) {
            final Distance distance = sectionEnd.oldSectionDistance(newSection);
            sectionEnd.changePreStation(newSection.getStation());
            sectionEnd.changeDistinct(distance);
            addSection(sectionEnd);
            addSection(newSection);
            return true;
        }
        return false;
    }

    private boolean addDownStation(final Section newSection, final Section sectionEnd) {
        if (newSection.getPreStation().equals(sectionEnd.getStation())) {
            addSection(newSection);
            return true;
        }
        return false;
    }

    private void addUpStation(final Section newSection, final Section sectionStart) {
        sectionStart.changePreStation(newSection.getPreStation());
        newSection.changeStation(newSection.getPreStation());
        newSection.changePreStation(null);
        addSection(sectionStart);
        addSection(newSection);
    }

    private void createSectionValid(final Section newSection) {
        final long sectionStationCount = this.sections.stream()
                .filter(st -> st.getStation().equals(newSection.getPreStation()) || st.getStation().equals(newSection.getStation()))
                .count();

        if (sectionStationCount == 2) {
            throw new AlreadyRegisterSectionException();
        }

        if (sectionStationCount == 0) {
            throw new StandardStationNotFoundException();
        }
    }
}