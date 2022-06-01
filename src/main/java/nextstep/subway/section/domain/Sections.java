package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "line")
    private List<Section> sections = new ArrayList<>();

    public void add(Section section, Line line) {
        validateSection(section);
        addAndChangeSection(section, line);
    }

    public void addForInit(Section section, Line line) {
        addSection(section, line);
    }

    public List<Station> orderStationsOfLine() {
        Section section = sections.stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("구간이 존재하지 않습니다."));

        Section preSection = this.findStartSection(section);
        List<Station> stations = new ArrayList<>();
        stations.add(preSection.getUpStation());

        while (preSection != null) {
            stations.add(preSection.getDownStation());
            preSection = this.findPostSection(preSection);
        }

        return stations;
    }

    public void remove(Station station, Line line) {
        validateLineStation(station);

        Optional<Section> optionalUpStation = findByUpStation(station);
        Optional<Section> optionalDownStation = findByDownStation(station);

        if (optionalDownStation.isPresent() && optionalUpStation.isPresent()) {
            Section upSection = optionalDownStation.get();
            Section downSection = optionalUpStation.get();

            Long distance = upSection.getDistance() + downSection.getDistance();
            this.addSection(new Section(upSection.getUpStation(), downSection.getDownStation(), distance), line);
        }

        optionalUpStation.ifPresent(section -> this.sections.remove(section));
        optionalDownStation.ifPresent(section -> this.sections.remove(section));
    }

    private void validateLineStation(Station station) {
        if (this.canNotDelete()) {
            throw new IllegalArgumentException("노선에 남은 구간이 하나이기 떄문에 삭제할 수 없습니다.");
        }

        List<Section> sections = findAllByStation(station);
        if (sections.size() == 0) {
            throw new IllegalArgumentException("노선에 해당 지하철역이 존재하지 않습니다.");
        }
    }

    private void validateSection(Section section) {
        Optional<Section> optionalUpSection = findByStation(section.getUpStation());
        Optional<Section> optionalDownSection = findByStation(section.getDownStation());

        if (!optionalUpSection.isPresent() && !optionalDownSection.isPresent()) {
            throw new IllegalArgumentException("종점역이 노선에 등록되어있지 않습니다.");
        }

        if (optionalUpSection.isPresent() && optionalDownSection.isPresent()) {
            throw new IllegalArgumentException("이미 상하행종점역이 모두 노선에 존재합니다.");
        }
    }

    private void addAndChangeSection(Section section, Line line) {
        Section currentSection = findCurrentSection(section);
        if (currentSection != null) {
            currentSection.changeSection(section);
        }
        addSection(section, line);
    }

    private void addSection(Section section, Line line) {
        sections.add(section);
        section.changeLine(line);
    }

    private Section findStartSection(Section section) {
        Optional<Section> optionalSection = findByDownStation(section.getUpStation());
        return optionalSection.map(this::findStartSection).orElse(section);
    }

    private Optional<Section> findByDownStation(Station station) {
        return this.sections.stream()
            .filter(s -> s.containDownStation(station))
            .findFirst();
    }

    private Optional<Section> findByUpStation(Station station) {
        return this.sections.stream()
            .filter(s -> s.containUpStation(station))
            .findFirst();
    }

    private Optional<Section> findByStation(Station station) {
        return this.sections.stream()
            .filter(s -> s.containUpStation(station) || s.containDownStation(station))
            .findFirst();
    }

    private List<Section> findAllByStation(Station station) {
        return this.sections.stream()
            .filter(s -> s.containUpStation(station) || s.containDownStation(station))
            .collect(Collectors.toList());
    }

    private Section findCurrentSection(Section section) {
        Optional<Section> optionalUpSection = findByUpStation(section.getUpStation());
        Optional<Section> optionalDownSection = findByDownStation(section.getDownStation());

        return optionalUpSection.orElseGet(() -> optionalDownSection.orElse(null));
    }

    private Section findPostSection(Section preSection) {
        return sections.stream()
            .filter(section -> section.isPostSection(preSection))
            .findFirst()
            .orElse(null);
    }

    private boolean canNotDelete() {
        return this.sections.size() == 1;
    }

}
