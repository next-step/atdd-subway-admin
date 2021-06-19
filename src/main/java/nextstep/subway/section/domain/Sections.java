package nextstep.subway.section.domain;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.SectionCreateFailException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {
    private static final String NOT_FOUND_START_SECTION = "시작 구간을 찾을 수 없습니다.";

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "line")
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();

        Section section = sections.stream()
                .filter(startSection -> startSection.beforeSection() == null)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_START_SECTION));

        stations.add(section.upStation());
        stations.add(section.downStation());

        while (section.nextSection() != null) {
            section = section.nextSection();
            stations.add(section.downStation());
        }

        return Collections.unmodifiableList(stations);
    }

    public void toSection(Section section) {
        validateStationNoneMatch(section);

        upStationBeforeAddIfExists(section);
        upStationAfterAddIfExists(section);
        downStationBeforeAddIfExists(section);
        downStationAfterAddIfExists(section);

        this.sections.add(section);
    }

    private void validateStationNoneMatch(Section section) {
        this.stations().stream()
                .filter(station -> station.equals(section.upStation()) || station.equals(section.downStation()))
                .findFirst()
                .orElseThrow(() -> new SectionCreateFailException("기존에 존재하지 않는 구간 입니다."));
    }

    private void upStationBeforeAddIfExists(Section newSection) {
        sections.stream()
                .filter(oriSection -> oriSection.upStation().equals(newSection.downStation()))
                .findFirst()
                .ifPresent(oriSection -> oriSection.upStationBeforeAdd(newSection));
    }

    private void upStationAfterAddIfExists(Section newSection) {
        sections.stream()
                .filter(oriSection -> oriSection.upStation().equals(newSection.upStation()))
                .findFirst()
                .ifPresent(oriSection -> oriSection.upStationAfterAdd(newSection));
    }

    private void downStationBeforeAddIfExists(Section newSection) {
        sections.stream()
                .filter(oriSection -> oriSection.downStation().equals(newSection.downStation()))
                .findFirst()
                .ifPresent(oriSection -> oriSection.downStationBeforeAdd(newSection));
    }

    private void downStationAfterAddIfExists(Section newSection) {
        sections.stream()
                .filter(oriSection -> oriSection.downStation().equals(newSection.upStation()))
                .findFirst()
                .ifPresent(beforeSection -> beforeSection.downStationAfterAdd(newSection));
    }
}
