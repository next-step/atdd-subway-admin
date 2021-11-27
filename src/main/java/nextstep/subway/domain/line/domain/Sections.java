package nextstep.subway.domain.line.domain;

import nextstep.subway.domain.section.domain.Distance;
import nextstep.subway.domain.section.domain.Section;
import nextstep.subway.domain.section.exception.AlreadyRegisterSectionException;
import nextstep.subway.domain.section.exception.StandardStationNotFoundException;
import nextstep.subway.domain.station.domain.Station;
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

        final Section startSection = getSectionStart();

        final Section endSection = getSectionEnd(startSection);

        if (isStationBetween(newSection, startSection)) {
            addStationBetween(newSection, endSection);
            return;
        }

        if (isStationDown(newSection, endSection)) {
            addSection(newSection);
            return;
        }

        addStationUp(newSection, startSection);
    }

    private boolean isStationBetween(final Section newSection, final Section startSection) {
        return newSection.getPreStation().equals(startSection.getStation());

    }

    private boolean isStationDown(final Section newSection, final Section endSection) {
        return newSection.getPreStation().equals(endSection.getStation());
    }

    private void addStationBetween(final Section newSection, final Section sectionEnd) {
            final Distance distance = sectionEnd.oldSectionDistance(newSection);
            sectionEnd.changePreStation(newSection.getStation());
            sectionEnd.changeDistinct(distance);
            addSection(sectionEnd);
            addSection(newSection);
    }

    private void addStationUp(final Section newSection, final Section sectionStart) {
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

    public void remove(final Station station) {
        final Section sectionStart = getSectionStart();
        if (sectionStart.getStation().equals(station)) {
            final Section section = this.sections.stream()
                    .filter(st -> st.getPreStation() != null)
                    .filter(st -> st.getPreStation().equals(sectionStart.getStation()))
                    .findFirst()
                    .get();
            section.changePreStation(null);
            this.sections.remove(sectionStart);
            this.sections.add(section);
            return;
        }

        this.sections.stream()
                .filter(st -> st.getStation().equals(station))
                .findFirst()
                .ifPresent(st -> this.sections.remove(st));
    }
}