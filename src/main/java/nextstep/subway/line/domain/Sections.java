package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {

  @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
  private List<Section> sections = new ArrayList<>();

  public Sections(Section upSection, Section downSection) {
    this.sections = Arrays.asList(upSection, downSection);
  }

  public void addSection(Section newSection) {
    boolean isIncludeUpStation = findByStation(newSection.getUpStation());
    boolean isIncludeDownStation = findByStation(newSection.getDownStation());

    if (isIncludeUpStation) {
      updateSectionWhenEqualUpStation(newSection);
    }

    if (isIncludeDownStation) {
      updateSectionWhenEqualDownStation(newSection);
    }

    this.sections.add(newSection);
  }

  private boolean findByStation(Station station) {
    return this.sections.stream()
        .anyMatch(section -> section.isIncludeInSection(station));
  }

  public void updateSectionWhenEqualUpStation(Section newSection) {
    this.sections.stream()
        .filter(section -> section.isUpStationInSection(newSection.getUpStation()))
        .findFirst()
        .ifPresent(section -> section.updateUpToDown(newSection.getDownStation(), newSection.getDistance()));
  }

  public void updateSectionWhenEqualDownStation(Section newSection) {
    this.sections.stream()
        .filter(section -> section.isIncludeInSection(newSection.getDownStation()))
        .findFirst()
        .ifPresent(section -> section.updateDownToUp(newSection.getUpStation(), newSection.getDistance()));
  }

  public List<Section> getOrderedSections() {
    // 출발지점 찾기
    Optional<Section> preLineStation = sections.stream()
        .filter(it -> it.getUpStation() == null)
        .findFirst();

    List<Section> result = new ArrayList<>();
    while (preLineStation.isPresent()) {
      Section preStation = preLineStation.get();
      result.add(preStation);
      preLineStation = sections.stream()
          .filter(it -> it.getUpStation() == preStation.getDownStation())
          .findFirst();
    }
    return result;
  }

  public List<Station> getStations() {
    return this.getOrderedSections().stream()
        .map(Section::getDownStation)
        .collect(Collectors.toList());
  }
}
