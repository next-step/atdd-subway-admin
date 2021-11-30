package nextstep.subway.section.domain;

import nextstep.subway.common.Message;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections;

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections from(List<Section> sectionList) {
        return new Sections(sectionList);
    }

    public static Sections from(Section section) {
        return new Sections(Arrays.asList(section));
    }

    public static Sections empty() {
        return Sections.from(new ArrayList<>());
    }

    public void add(Section targetSection, Line line) {
        validateStationsContains(targetSection, line);
        targetSection.addLine(line);

        for (Section original : sections) {
            if (isUpStationEquals(targetSection, original)) return;

            if (isDownStationEquals(targetSection, original)) return;

            if (isUpStationAndTargetDownStationEquals(targetSection, original)) return;

            if (isDownStationAndTargetUpStationEquals(targetSection, original)) return;
        }
    }

    private boolean isUpStationAndTargetDownStationEquals(Section targetSection, Section original) {
        if (original.isUpStationAndTargetDownStationEquals(targetSection)) {
            addSectionOriginalIndex(targetSection, original);
            return true;
        }
        return false;
    }

    private boolean isDownStationEquals(Section targetSection, Section original) {
        if (original.isDownStationEquals(targetSection)) {
            original.minusDistance(targetSection);
            original.changeDownStation(targetSection);
            addSectionBehindOfOriginal(targetSection, original);
            return true;
        }
        return false;
    }

    private boolean isDownStationAndTargetUpStationEquals(Section targetSection, Section original) {
        if (original.isDownStationAndTargetUpStationEquals(targetSection)) {
            addSectionBehindOfOriginal(targetSection, original);
            return true;
        }
        return false;
    }

    private boolean isUpStationEquals(Section targetSection, Section original) {
        if(original.isUpStationEquals(targetSection)) {
            addSectionOriginalIndex(targetSection, original);
            original.minusDistance(targetSection);
            original.changeUpStation(targetSection);
            return true;
        }
        return false;
    }

    private void addSectionBehindOfOriginal(Section targetSection, Section original) {
        sections.add(sections.indexOf(original) + 1, targetSection);
    }

    private void addSectionOriginalIndex(Section targetSection, Section original) {
        sections.add(sections.indexOf(original), targetSection);
    }

    private void validateStationsContains(Section section, Line line) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        checkNotContainingStations(line, upStation, downStation);
        checkContainingStations(line, upStation, downStation);
    }

    private void checkContainingStations(Line line, Station upStation, Station downStation) {
        if (line.isContainingStation(upStation) &&
                line.isContainingStation(downStation)) {
            throw new IllegalArgumentException(Message.NOT_REGISTER_ALL_INCLUDE.getMessage());
        }
    }

    private void checkNotContainingStations(Line line, Station upStation, Station downStation) {
        if (line.isContainingStation(upStation) == false &&
                line.isContainingStation(downStation) == false) {
            throw new IllegalArgumentException(Message.NOT_REGISTER_NOT_ALL_INCLUDE.getMessage());
        }
    }

    public List<Section> getSections() {
        return this.sections;
    }

    public boolean contains(Station station) {
        return getStations().contains(station);
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
