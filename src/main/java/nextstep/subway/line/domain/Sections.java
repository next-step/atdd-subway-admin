package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.line.exception.SectionException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {

  private static final String ALREADY_SECTION_EXIST = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음";
  private static final String MUST_HAVING_UP_OR_DOWN_STATION = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음";
  private static final String NOT_REGISTERED_STATION = "노선에 등록되지 않은 역은 제거할 수 없습니다.";
  private static final String LAST_SECTION = "제거 가능한 역이 없습니다. 마지막 구간입니다.";
  private static final int REMOVABLE_SECTION_SIZE = 2;

  @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
  private List<Section> sections = new ArrayList<>();

  public Sections(Section upSection, Section downSection) {
    this.sections = Arrays.asList(upSection, downSection);
  }

  public List<Section> getOrderedSections() {
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

  public void addSection(Section newSection) {
    boolean isIncludeUpStation = isIncludeStationList(newSection.getUpStation());
    boolean isIncludeDownStation = isIncludeStationList(newSection.getDownStation());

    addValidateSection(isIncludeUpStation, isIncludeDownStation);

    if (isIncludeUpStation) {
      updateSectionWhenEqualUpStation(newSection);
    }

    if (isIncludeDownStation) {
      updateSectionWhenEqualDownStation(newSection);
    }

    this.sections.add(newSection);
  }

  public void removeSection(Station station) {

    removeValidationCheck();

    Section removeSection = findSectionByStationId(station);
    updateSectionToRemove(removeSection);

    this.sections.remove(removeSection);
  }

  public void updateSectionWhenEqualUpStation(Section newSection) {
    this.sections.stream()
        .filter(section -> section.isUpStationInSection(newSection.getUpStation()))
        .findFirst()
        .ifPresent(section -> section.updateUpToDown(newSection.getDownStation(), newSection.getDistance()));
  }

  public void updateSectionWhenEqualDownStation(Section newSection) {
    this.sections.stream()
        .filter(section -> section.isDownStationInSection(newSection.getDownStation()))
        .findFirst()
        .ifPresent(section -> section.updateDownToUp(newSection.getUpStation(), newSection.getDistance()));
  }

  private boolean isIncludeStationList(Station station) {
    return this.sections.stream()
        .anyMatch(section -> section.isDownStationInSection(station));
  }

  private Section findSectionByStationId(Station station) {
    return this.sections.stream()
        .filter(section -> section.isDownStationInSection(station))
        .findFirst()
        .orElseThrow(() -> new SectionException(NOT_REGISTERED_STATION));
  }

  private void updateSectionToRemove(Section removeSection) {
    this.sections.stream()
        .filter(it -> it.isUpStationInSection(removeSection.getDownStation()))
        .findFirst()
        .ifPresent(section -> section.updateUpStationToRemove(removeSection.getUpStation(), removeSection.getDistance()));
  }

  private void addValidateSection(boolean isIncludeUpStation, boolean isIncludeDownStation) {
    if (isIncludeUpStation && isIncludeDownStation) {
      throw new SectionException(ALREADY_SECTION_EXIST);
    }
    if (!isIncludeUpStation && !isIncludeDownStation) {
      throw new SectionException(MUST_HAVING_UP_OR_DOWN_STATION);
    }
  }

  private void removeValidationCheck() {
    if (this.sections.size() <= REMOVABLE_SECTION_SIZE) {
      throw new SectionException(LAST_SECTION);
    }
  }
}
