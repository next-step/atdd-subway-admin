package nextstep.subway.section.domain;

import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void initSections(Section section) {
        sections.add(section);
    }

    public void addSection(Section section) {
        CheckException(section);
        addSectionMiddleBasedOnUpStation(section);
        addSectionUpBasedOnUpStation(section);
        addSectionDownBasedOnDownStation(section);
    }

    private void CheckException(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getUpStation())
                        && !section.getDownStation().getName().equals(oldSection.getUpStation().getName())
                        && oldSection.getDistance() < section.getDistance())
                .findFirst()
                .ifPresent(oldSection -> {
                    throw new RuntimeException("추가할 구간의 거리가 기존 구간보다 더 클 수 없습니다.");
                });

        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getUpStation())
                && section.getDownStation().equals(oldSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    throw new RuntimeException("이미 존재하는 역 구간입니다.");
                });

        this.sections.stream()
                .filter(oldSection -> !section.getUpStation().equals(oldSection.getUpStation())
                        && !section.getUpStation().equals(oldSection.getDownStation())
                        && !section.getDownStation().equals(oldSection.getUpStation())
                        && !section.getDownStation().equals(oldSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    throw new RuntimeException("기존 구간에 일치하는 역이 없습니다.");
                });
    }

    private void addSectionDownBasedOnDownStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getDownStation()))
                .findFirst()
                .ifPresent(oldSection -> {sections.add(section);});
    }

    private void addSectionUpBasedOnUpStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getDownStation().equals(oldSection.getUpStation()))
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.remove(oldSection);
                    sections.add(section);
                    sections.add(oldSection);
                });
    }

    private void addSectionMiddleBasedOnUpStation(Section section) {
        this.sections.stream()
                .filter(oldSection -> section.getUpStation().equals(oldSection.getUpStation())
                                && !section.getDownStation().getName().equals(oldSection.getUpStation().getName())
                                && oldSection.getDistance() > section.getDistance())
                .findFirst()
                .ifPresent(oldSection -> {
                    sections.add(section);
                    sections.add(new Section(section.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - section.getDistance()));
                    sections.remove(oldSection);
                });
    }

    public void forEach(Consumer<Section> section) {
        sections.forEach(section);
    }

    public List<StationResponse> getStationResponse() {
        return sections.stream()
                .flatMap(list -> list.getStations().stream()
                .map(StationResponse::of))
                .distinct()
                .collect(Collectors.toList());
    }
}
