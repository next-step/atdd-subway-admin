package nextstep.subway.domain;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

public class Sections {
    private List<Section> sections;
    private Line line;

    private Sections(Line line) {
        this.sections = line.getSections();
        this.line = line;
    }

    public static Sections of(Line line) {
        if (CollectionUtils.isEmpty(line.getSections())) {
            Section section = line.toSection();
            line.addSection(section);
        }
        return new Sections(line);
    }

    public void insertInsideFromUpStation(Station upStation, Station newStation, long distance) {
        Optional<Section> first = this.sections.stream()
                .filter(section -> section.hasUpStation(upStation))
                .findFirst();

        if (first.isPresent()) {
            Section section = first.get();
            section.splitFromUpStation(newStation, distance);
        }
    }


    public void insertInsideFromDownStation(Station downStation, Station newStation, long distance) {
        Optional<Section> first = this.sections.stream()
                .filter(section -> section.hasDownStation(downStation))
                .findFirst();

        if (first.isPresent()) {
            Section section = first.get();
            section.splitFromDownStation(newStation, distance);
        }
    }

    public boolean hasSection(Section of) {
        Optional<Section> any = this.sections.stream().filter(section -> section.equals(of)).findAny();
        return any.isPresent();
    }
}
