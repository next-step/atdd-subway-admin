package nextstep.subway.section.domain;

import nextstep.subway.exception.InvalidSectionException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> values;

    private Sections(List<Section> values) {
        this.values = values;
    }

    protected Sections() {

    }

    private List<Section> getValues() {
        return values;
    }

    public Stream<Section> stream(){
        return values.stream();
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section targetSection, Line line) {
        validateStationsContains(targetSection, line);
        targetSection.setLine(line);

        for (Section original : values) {
            if (original.isUpStationEquals(targetSection)) {
                addSectionOriginalIndex(targetSection, original);
                original.minusDistance(targetSection.getDistance());
                original.changeUpStation(targetSection.getDownStation());
                return;
            }

            if (original.isDownStationEquals(targetSection)) {
                original.minusDistance(targetSection.getDistance());
                original.changeDownStation(targetSection.getUpStation());
                addSectionBehindOfOriginal(targetSection, original);
                return;
            }

            if (original.isUpStationAndTargetDownStationEquals(targetSection)) {
                addSectionOriginalIndex(targetSection, original);
                return;
            }

            if (original.isDownStationAndTargetUpStationEquals(targetSection)) {
                addSectionBehindOfOriginal(targetSection, original);
                return;
            }
        }
    }

    private void addSectionBehindOfOriginal(Section targetSection, Section original) {
        values.add(values.indexOf(original) + 1, targetSection);
    }

    private void addSectionOriginalIndex(Section targetSection, Section original) {
        values.add(values.indexOf(original), targetSection);
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
            throw new IllegalArgumentException("상행, 종행 역 모두가 포함되어있습니다..");
        }
    }

    private void checkNotContainingStations(Line line, Station upStation, Station downStation) {
        if (line.isContainingStation(upStation) == false &&
                line.isContainingStation(downStation) == false) {
            throw new IllegalArgumentException("상행, 종행 역 모두가 포함되지 않았습니다.");
        }
    }

    public void removeStation(Station station) {
        List<Section> adjacentSections = getSectionsContainingStation(station);
        if (adjacentSections.size() != 2){
            throw new InvalidSectionException("adjacentSections size: " + adjacentSections.size());
        }

        Section section = adjacentSections.get(0);
        Section nextSection = adjacentSections.get(1);
        section.changeDownStation(nextSection.getDownStation());
        section.plusDistance(nextSection.getDistance());
        getValues().remove(nextSection);
    }

    private List<Section> getSectionsContainingStation(Station station) {
        return getValues().stream()
                .filter(section -> section.isUpStationEquals(station) || section.isDownStationEquals(station))
                .collect(Collectors.toList());
    }

    public void add(Section section) {
        this.values.add(section);
    }

    public void clear() {
        values.clear();
    }
}
