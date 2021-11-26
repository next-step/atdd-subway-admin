package nextstep.subway.domain.line.domain;

import nextstep.subway.domain.section.domain.Distance;
import nextstep.subway.domain.section.domain.Section;
import nextstep.subway.domain.section.exception.AlreadyRegisterSectionException;
import nextstep.subway.domain.section.exception.StandardStationNotFoundException;
import nextstep.subway.global.error.exception.EntityNotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Section> getStationInOrder() {
        List<Section> sectionsResult = new ArrayList<>();

        try {
            sectionQuest(sectionsResult);
        }catch (EntityNotFoundException e) {
            return sectionsResult;
        }

        return sectionsResult;
    }

    private void sectionQuest(final List<Section> sectionsResult) {
        Section sectionStart = getSectionStart();

        while (true) {
            sectionsResult.add(sectionStart);
            sectionStart = getSectionEnd(sectionStart);
        }
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void createSection(Section newSection) {
        createSectionValid(newSection);

        final Section sectionStart = getSectionStart();

        final Section sectionEnd = getSectionEnd(sectionStart);

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

    private Section getSectionStart() {
        return this.sections.stream()
                .filter(st -> st.getPreStation() == null)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("상행 종점역이 존재하지 않습니다."));
    }

    private Section getSectionEnd(Section sectionStart) {
        return this.sections.stream()
                .filter(st -> st.getPreStation() != null)
                .filter(st -> st.getPreStation().equals(sectionStart.getStation()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("하행 종점역이 존재하지 않습니다."));
    }
}