package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

  private static final int SINGLE_ELEMENT_SIZE = 1;
  private static final int INVALID_STATION_CONTAINING_SECTIONS_SIZE = 3;
  private static final String DUPLICATED_STATIONS = "이미 등록된 역 구간을 다시 등록 할 수 없습니다.";
  private static final String NOT_CONTAINS_NEITHER_STATIONS = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.";
  private static final String EMPTY_SECTIONS = "등록된 구간이 없습니다.";
  private static final String CAN_NOT_REMOVE_NON_REGISTERED_STATION = "등록되어 있지 않은 역은 제거할 수 없습니다.";
  private static final String CAN_NOT_REMOVE_STATION_FROM_SINGLE_SECTION = "상행 종점 - 하행 종점으로 이루어진 하나의 구간만 있을 때는 역을 제거할 수 없습니다.";
  private static final String INVALID_STATE_SECTIONS = "지하철 역의 구간 연결 상태가 정상 상태가 아닙니다.";

  @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Section> lineSections = new ArrayList<>();

  public Sections() {
  }

  public Set<Station> getDistinctStations() {
    return this.getSortedSections().stream()
        .flatMap(section -> section.getUpAndDownStations()
            .stream())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public void registerNewSection(Section newSection) {
    if (lineSections.isEmpty()) {
      this.lineSections.add(newSection);
      return;
    }
    registerNewSectionToNotEmptySections(newSection);
  }

  public void removeStation(Station stationForRemove) {
    validateSingleSection();
    List<Section> stationContainingSortedSections = getStationContainingSortedSections(stationForRemove);
    validateStationContainingSortedSections(stationContainingSortedSections);
    removeTargetStation(stationContainingSortedSections);
  }

  private void registerNewSectionToNotEmptySections(Section newSection) {
    validateNewSection(newSection);
    this.lineSections
        .forEach(lineSection -> lineSection.insertNewSection(newSection));
    this.lineSections.add(newSection);
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
    List<Section> elementDecreasingList = new ArrayList<>(lineSections);
    Iterator<Section> elementDecreasingListIterator = elementDecreasingList.iterator();
    while (elementDecreasingListIterator.hasNext()) {
      sortedSections.add(popFirstSection(elementDecreasingList));
    }
    return sortedSections;
  }

  private Section popFirstSection(List<Section> sections) {
    Iterator<Section> iterator = sections.iterator();
    while (iterator.hasNext()) {
      Section current = iterator.next();
      if (isHead(sections, current)) {
        iterator.remove();
        return current;
      }
    }
    throw new IllegalArgumentException(EMPTY_SECTIONS);
  }

  private boolean isHead(List<Section> sections, Section compare) {
    return sections.stream()
        .filter(origin -> !compare.isSameEdges(origin))
        .noneMatch(compare::isNextSection);
  }

  private void removeTargetStation(List<Section> sortedListTwoSections) {
    Section firstSection = sortedListTwoSections.get(0);
    if (sortedListTwoSections.size() == SINGLE_ELEMENT_SIZE) {
      lineSections.remove(firstSection);
      return;
    }
    Section nextSection = sortedListTwoSections.get(1);
    firstSection.removeStationBetweenSections(nextSection);
    lineSections.remove(nextSection);
  }

  private List<Section> getStationContainingSortedSections(Station targetStation) {
    return getSortedSections().stream()
          .filter(section -> section.containsStation(targetStation))
          .collect(Collectors.toList());
  }

  private void validateSingleSection() {
    if (lineSections.size() == SINGLE_ELEMENT_SIZE) {
      throw new IllegalArgumentException(CAN_NOT_REMOVE_STATION_FROM_SINGLE_SECTION);
    }
  }

  private void validateStationContainingSortedSections(List<Section> stationContainingSortedSections) {
    validateNoneRegisteredStation(stationContainingSortedSections);
    validateInvalidSectionStatus(stationContainingSortedSections);

  }

  private void validateInvalidSectionStatus(List<Section> stationContainingSortedSections) {
    if (stationContainingSortedSections.size() >= INVALID_STATION_CONTAINING_SECTIONS_SIZE) {
      throw new IllegalStateException(INVALID_STATE_SECTIONS);
    }
  }

  private void validateNoneRegisteredStation(List<Section> stationContainingSortedSections) {
    if (stationContainingSortedSections.isEmpty()) {
      throw new IllegalArgumentException(CAN_NOT_REMOVE_NON_REGISTERED_STATION);
    }
  }
}
