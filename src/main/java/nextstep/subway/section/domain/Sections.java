package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

  private static final String DUPLICATED_STATIONS = "이미 등록된 역 구간을 다시 등록 할 수 없습니다.";
  private static final String NOT_CONTAINS_NEITHER_STATIONS = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";
  private static final String EMPTY_SECTIONS = "등록된 구간이 없습니다.";

  @OneToMany(mappedBy = "line")
  private List<Section> lineSections = new ArrayList<>();

  public Sections() {
  }

  public void add(Section section) {
    lineSections.add(section);
  }

  public List<Section> getLineSections() {
    return lineSections;
  }

  public Set<Station> getDistinctStations() {
    return this.getSortedSections().stream()
        .flatMap(section -> section.getUpAndDownStations()
            .stream())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public void registerNewSection(Section newSection) {
    validateNewSection(newSection);
    this.lineSections = this.getLineSections().stream()
        .flatMap(lineSection -> lineSection.insertNewSection(newSection).stream())
        .collect(Collectors.toList());
  }

  private void validateNewSection(Section newSection) {
    if (hasBothStations(newSection)) {
      throw new IllegalArgumentException(DUPLICATED_STATIONS);
    }
    if (hasNotBothStations(newSection)) {
      throw new IllegalArgumentException(NOT_CONTAINS_NEITHER_STATIONS);
    }
  }

  private boolean hasBothStations(Section section) {
    Set<Station> stations = getDistinctStations();
    return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
  }

  private boolean hasNotBothStations(Section section) {
    Set<Station> stations = getDistinctStations();
    return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
  }

  private List<Section> getSortedSections() {
    List<Section> sortedSections = new ArrayList<>();
    Section currentSection = findFirstSection();
    sortedSections.add(currentSection);
    addNextSectionIfExist(findNextSection(currentSection), sortedSections);
    return sortedSections;
  }

  private void addNextSectionIfExist(Optional<Section> maybeNextSection, List<Section> sortedSections) {
    if (!maybeNextSection.isPresent()) {
      return;
    }
    Section section = maybeNextSection.get();
    sortedSections.add(section);
    addNextSectionIfExist(findNextSection(section), sortedSections);
  }

  private Section findFirstSection() {
    return getLineSections().stream()
        .filter(this::isHead)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(EMPTY_SECTIONS));
  }

  private boolean isHead(Section compare) {
    return getLineSections().stream()
        .filter(origin -> !compare.isSameEdges(origin))
        .noneMatch(compare::isAfter);
  }

  private Optional<Section> findNextSection(Section compare) {
    return getLineSections().stream()
        .filter(origin -> !compare.isSameEdges(origin))
        .filter(origin -> origin.isAfter(compare))
        .findFirst();
  }
}
