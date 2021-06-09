package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

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

    public List<Section> getValues() {
        return values;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section targetSection, Line line) {
        validateTargetStationContains(targetSection, line);
        targetSection.setLine(line);

        makeSeqUpStationEquals(targetSection);
        makeSeqWhenDownStationEquals(targetSection);
        makeSeqWhenUpStationAndTargetDownStationEquals(targetSection);
        makeSeqWhenDownStationAndTargetUpStationEquals(targetSection);

        values.add(targetSection);
    }

    private void makeSeqUpStationEquals(Section targetSection) {
        values.stream().filter(s -> s.isUpStationEquals(targetSection))
                .findFirst().ifPresent(
                section -> {
                    int sequence = section.getSequence();
                    targetSection.setSequence(sequence);
                    section.setSequence(sequence + 1);
                    section.minusDistance(targetSection.getDistance());
                    section.changeUpStation(targetSection.getDownStation());
                }
        );
    }

    private void makeSeqWhenDownStationEquals(Section targetSection) {
        values.stream().filter(s -> s.isDownStationEquals(targetSection))
                .findFirst().ifPresent(
                section -> {
                    targetSection.setSequence(section.getSequence() + 1);
                    section.minusDistance(targetSection.getDistance());
                    section.changeDownStation(targetSection.getUpStation());
                }
        );
    }

    private void makeSeqWhenUpStationAndTargetDownStationEquals(Section targetSection) {
        values.stream().filter(s -> s.isUpStationAndTargetDownStationEquals(targetSection))
                .findFirst().ifPresent(
                section -> {
                    int sequence = section.getSequence();
                    targetSection.setSequence(sequence);
                    section.setSequence(sequence + 1);
                }
        );
    }

    private void makeSeqWhenDownStationAndTargetUpStationEquals(Section targetSection) {
        values.stream().filter(s -> s.isDownStationAndTargetUpStationEquals(targetSection))
                .findFirst().ifPresent(
                        section -> {
                            int sequence = section.getSequence();
                            targetSection.setSequence(sequence+1);
                        }
        );
    }

    private void validateTargetStationContains(Section section, Line line) {
        List<Station> stations = line.getStations();
        if (stations.contains(section.getUpStation()) == false &&
                stations.contains(section.getDownStation()) == false){
            throw new IllegalArgumentException("상행, 종행 역 모두가 포함되지 않았습니다.");
        };

        if (stations.contains(section.getUpStation()) &&
                stations.contains(section.getDownStation())){
            throw new IllegalArgumentException("상행, 종행 역 모두가 포함되어있습니다..");
        };

    }
}
