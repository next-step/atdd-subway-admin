package nextstep.subway.line.domain;

import nextstep.subway.common.ServiceException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;
import org.springframework.http.HttpStatus;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
  private static final int DELETABLE_SIZE = 2;
  @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Section> sections = new ArrayList<>();

  protected Sections() {}

  public Sections(List<Section> sections) {
    this.sections.addAll(sections);
  }

  public Sections of(List<Section> sections) {
    return new Sections(sections);
  }

  public void add(Section section) {
    if (isUpdateSection(section)) {
      checkAddSectionValidation(section);
      updateSection(section);
    }
    sections.add(section);
  }

  private boolean isUpdateSection(Section section) {
    return !sections.isEmpty()
      && !isUpEndPointStation(section.getDownStation())
      && !isDownEndPointStation(section.getUpStation());
  }

  private boolean isDownEndPointStation(Station station) {
    return downEndPoint().getDownStation().equals(station);
  }

  private boolean isUpEndPointStation(Station station) {
    return upEndPoint().getUpStation().equals(station);
  }

  private void checkAddSectionValidation(Section section) {
    if (!containsStation(section)) {
      throw new NoSuchElementException("해당 구간에 연결된 역들과 연결할 수 있는 역이 없습니다. 입력: "
        + section.getUpStation() + ", " + section.getDownStation());
    }

    if (containSection(section)) {
      throw new IllegalArgumentException("이미 연결된 구간이 존재합니다. 입력: " + section);
    }
  }

  private boolean containSection(Section section) {
    return sections.stream()
      .anyMatch(existSection -> existSection.isMatch(section));
  }

  private boolean containsStation(Section section) {
    List<Station> stationList = new ArrayList<>(Arrays.asList(section.getUpStation(), section.getDownStation()));
    return getUpToDownStations().containsAny(stationList);
  }

  private void updateSection(Section newSection) {
    if (hasMatchUpStation(newSection)) {
      updateMatchedUpSideSection(newSection);
      return;
    }

    updateMatchedDownSideSection(newSection);
  }

  private void updateMatchedDownSideSection(Section newSection) {
    sections.stream()
      .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
      .findFirst()
      .ifPresent(section -> section.updateDownSideSection(newSection));
  }

  private void updateMatchedUpSideSection(Section newSection) {
    sections.stream()
      .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
      .findFirst()
      .ifPresent(section -> section.updateUpSideSection(newSection));
  }

  private boolean hasMatchUpStation(Section section) {
    return sections.stream()
      .map(Section::getUpStation)
      .anyMatch(station -> station.equals(section.getUpStation()));
  }

  private Section upEndPoint() {
    return sections.stream()
      .filter(this::isUpEndPoint)
      .findAny()
      .orElseThrow(IllegalStateException::new);
  }

  private Section downEndPoint() {
    return sections.stream()
      .filter(this::isDownEndPoint)
      .findAny()
      .orElseThrow(IllegalStateException::new);
  }

  private boolean isDownEndPoint(Section parentSection) {
    return sections.stream()
      .map(Section::getUpStation)
      .noneMatch(station -> station.equals(parentSection.getDownStation()));
  }

  private boolean isUpEndPoint(Section parentSection) {
    return sections.stream()
      .map(Section::getDownStation)
      .noneMatch(station -> station.equals(parentSection.getUpStation()));
  }

  public List<Section> getSections() {
    return sections;
  }

  public Stations getUpToDownStations() {
    List<Station> stations = new ArrayList<>(Arrays.asList(upEndPoint().getUpStation(), upEndPoint().getDownStation()));
    while (!isDownEndPointStation(stations)) {
      stations.add(findNextLinkedStation(stations));
    }

    return new Stations(stations);
  }

  private Station findNextLinkedStation(List<Station> stations) {
    return sections.stream()
      .filter(section -> section.getUpStation().equals(stationsLastElement(stations)))
      .findAny()
      .orElseThrow(IllegalArgumentException::new)
      .getDownStation();
  }

  private boolean isDownEndPointStation(List<Station> stations) {
    return stationsLastElement(stations).equals(downEndPoint().getDownStation());
  }

  private Station stationsLastElement(List<Station> stations) {
    return stations.get(stations.size() - 1);
  }

  public List<Section> asList() {
    return sections;
  }

  public void deleteByStation(Station station) {
    checkDeletableSections(station);
    if (isEndPointStation(station)) {
      deleteEndPointSectionByStation(station);
      return;
    }

    deleteAndUpdateSectionByStation(station);
  }

  private void deleteAndUpdateSectionByStation(Station station) {
    Section deleteTargetSection = sections.stream()
        .filter(section -> section.getUpStation().equals(station))
        .findFirst()
        .orElseThrow(IllegalStateException::new);
    sections.remove(deleteTargetSection);
    updateByDeleteTargetSection(station, deleteTargetSection);
  }

  private void updateByDeleteTargetSection(Station station, Section deleteTargetSection) {
    sections.stream()
        .filter(section -> section.getDownStation().equals(station))
        .findFirst()
        .ifPresent(section -> {
          section.updateDownStation(deleteTargetSection.getDownStation());
          section.addDistance(deleteTargetSection.getDistance());
        });
  }

  private void checkDeletableSections(Station station) {
    if (sections.size() < DELETABLE_SIZE) {
      throw new ServiceException(HttpStatus.BAD_REQUEST, "구간이 하나 이하의 노선에서 마지막 구간을 제거할 수 없습니다.");
    }
    if (!getUpToDownStations().contains(station)) {
      throw new ServiceException(HttpStatus.BAD_REQUEST, "노선에 등록되지 않은 역은 제거할 수 없습니다.");
    }
  }

  private void deleteEndPointSectionByStation(Station station) {
    if (isUpEndPointStation(station)) {
      sections.removeIf(section -> section.equals(upEndPoint()));
      return;
    }

    sections.removeIf(section -> section.equals(downEndPoint()));
  }

  private boolean isEndPointStation(Station station) {
    return isUpEndPointStation(station)
        || isDownEndPointStation(station);
  }
}
