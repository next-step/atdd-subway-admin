package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineException;
import nextstep.subway.line.exception.LineExceptionType;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int MIN = 0;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list = new ArrayList<>();

    public void add(final Section section) {
        validate(section);

        updateUpStation(section);
        updateDownStation(section);
        list.add(section);
    }

    private void validate(final Section section) {
        existedEachStation(section);
        notExistedEachStation(section);
    }

    private void existedEachStation(final Section section) {
        if (existStation(section)) {
            throw new LineException(LineExceptionType.STATION_ALL_USED);
        }
    }

    private boolean existStation(final Section section) {
        return containStation(section.getUpStation()) && containStation(section.getDownStation());
    }

    private void notExistedEachStation(final Section section) {
        if (list.size() > MIN && notExistStation(section)) {
            throw new LineException(LineExceptionType.STATION_ALL_NOT_USED);
        }
    }

    private boolean notExistStation(final Section section) {
        return !containStation(section.getUpStation()) && !containStation(section.getDownStation());
    }


    private boolean containStation(final Station station) {
        return getAllStation().contains(station);
    }

    private void updateUpStation(final Section section) {
        list.stream()
                .filter(it -> it.isEqualsDownStation(section))
                .findFirst()
                .ifPresent(it ->it.updateDownStation(section));
    }

    private void updateDownStation(final Section section) {
        list.stream()
                .filter(it -> it.isEqualsUpStation(section))
                .findFirst()
                .ifPresent(it ->it.updateUpStation(section));
    }

    public boolean contains(final Section section) {
        return list.contains(section);
    }

    public Set<Station> getAllStation() {
        Set<Station> set = new HashSet<>();
        for (Section section : list) {
            set.add(section.getUpStation());
            set.add(section.getDownStation());
        }
        return set;
    }

    public Station finalUpStation() {
        return getUpStations().stream()
                .filter(it -> !getDownStations().contains(it))
                .findFirst()
                .orElseThrow(() -> new LineException((LineExceptionType.FINAL_UP_STATION_NOT_FOUND)));
    }

    private List<Station> getUpStations() {
        return list.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public Station finalDownStation() {
        return getDownStations().stream()
                .filter(it -> !getUpStations().contains(it))
                .findFirst()
                .orElseThrow(() -> new LineException(LineExceptionType.FINAL_DOWN_STATION_NOT_FOUND));
    }

    private List<Station> getDownStations() {
        return list.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + list +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Sections sections1 = (Sections) o;
        return Objects.equals(list, sections1.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

}

