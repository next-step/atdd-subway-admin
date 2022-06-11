package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineException;
import nextstep.subway.line.exception.LineExceptionType;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list = new ArrayList<>();

    public void add(final Section section) {
        list.add(section);
    }

    public boolean contains(final Section section) {
        return list.contains(section);
    }

    public Station finalUpStation() {
        return list.stream()
                .filter(Section::isFinalUpStation)
                .map(Section::getUpStation)
                .findFirst()
                .orElseThrow(() -> new LineException(LineExceptionType.FINAL_UP_STATION_NOT_FOUND));
    }

    public Station finalDownStation() {
        return list.stream()
                .filter(Section::isFinalDownStation)
                .map(Section::getDownStation)
                .findFirst()
                .orElseThrow(() -> new LineException(LineExceptionType.FINAL_DOWN_STATION_NOT_FOUND));
    }

    public List<Section> getList() {
        return Collections.unmodifiableList(list);
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

