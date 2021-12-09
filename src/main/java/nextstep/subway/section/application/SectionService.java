package nextstep.subway.section.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private final StationService stationService;
    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;

    public SectionService(StationService stationService, SectionRepository sectionRepository, LineRepository lineRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    /* 새로운 역이 상행/하행 종점으로 등록되는 경우인지 판단 */
    private boolean checkNewUpDownStation(Section section, Section targetSection) {
        return section.isDownStation(targetSection.getUpStation())
            || section.isUpStation(targetSection.getDownStation());
    }

    /* 역 사이에 새로운 역을 등록할 경우인지 판단 */
    private boolean checkNewStationBetweenStations(Section section, Section targetSection) {
        boolean result = false;
        if (section.isDownStation(targetSection.getDownStation())) {
            Station newDownStation = stationService.findStationById(targetSection.getUpStation().getId());
            section.updateDownStation(newDownStation, targetSection);
            result = true;
        }
        if (section.isUpStation(targetSection.getUpStation())) {
            Station newUpStation = stationService.findStationById(targetSection.getDownStation().getId());
            section.updateUpStation(newUpStation, targetSection);
            result = true;
        }
        return result;
    }

    private void validateSameStationsExist(Section section, Section targetSection) {
        if (section.isDownStation(targetSection.getDownStation())
            && section.isUpStation(targetSection.getUpStation())) {
            throw new IllegalArgumentException("동일한 상,하행역이 등록되어 있으면 추가할 수 없음");
        }
    }

    private void validateDistance(Section section, Section targetSection) {
        if ((section.isDownStation(targetSection.getDownStation())
            || section.isUpStation(targetSection.getUpStation()))
            && section.getDistance() <= targetSection.getDistance()) {
            throw new IllegalArgumentException(
                "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
    }

    @Transactional
    public SectionResponse createSection(SectionRequest request, Line line) {
        Section targetSection = getSectionOf(request, line);
        Sections sections = new Sections(sectionRepository.findByLineId(line.getId()));

        for (int i = 0; i < sections.getSections().size() && Objects.isNull(targetSection.getId()); i++) {
            Section section = sections.getSection(i);
            targetSection = createSection(section, targetSection);
        }

        if (!Objects.isNull(targetSection.getId())) {
            return SectionResponse.of(targetSection);
        }
        throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음");
    }

    private Section createSection(Section section, Section targetSection) {
        validateDistance(section, targetSection);
        validateSameStationsExist(section, targetSection);

        if (checkNewStationBetweenStations(section, targetSection)
            || checkNewUpDownStation(section, targetSection)) {
            return saveSection(targetSection);
        }
        return targetSection;
    }

    @Transactional(readOnly = true)
    public Section getSectionOf(LineRequest lineRequest) {
        return new Section(
            stationService.findStationById(lineRequest.getUpStationId()),
            stationService.findStationById(lineRequest.getDownStationId()),
            lineRequest.getDistance());
    }

    @Transactional(readOnly = true)
    public Section getSectionOf(SectionRequest sectionRequest, Line line) {
        Section section = new Section(
            stationService.findStationById(sectionRequest.getUpStationId()),
            stationService.findStationById(sectionRequest.getDownStationId()),
            sectionRequest.getDistance());
        section.setLine(line);
        return section;
    }

    @Transactional
    public List<SectionResponse> getSections(Line line) {
        Sections sections = new Sections(sectionRepository.findByLineId(line.getId()));
        return SectionResponse.of(sections.getSortedSections());
    }

    @Transactional
    public void delete(Sections sections) {
        sectionRepository.deleteAll(sections.getSections());
    }

    @Transactional
    public void removeSectionByStationId(Long stationId, Long lineId) {
        Station station = stationService.findStationById(stationId);
        Sections sections =  new Sections(sectionRepository.findByLineId(lineId));
        Optional<Section> upSection = sections.getUpSectionOf(station);
        Optional<Section> downSection = sections.getDownSectionOf(station);
        if (upSection.isPresent() && downSection.isPresent()) {
            updateSectionsWithMerge(sections, upSection.get(), downSection.get());
            return;
        }
        if ((upSection.isPresent() || downSection.isPresent()) && sections.onlyLastSectionRemains()) {
            throw new RuntimeException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.");
        }

        if (upSection.isPresent()) {
            removeSection(sections, upSection.get());
            return;
        }
        if (downSection.isPresent()) {
            removeSection(sections, downSection.get());
            return;
        }
        throw new NoSuchElementException("해당 id의 정류장이 노선 내에 존재하지 않습니다. id = " + stationId);
    }

    @Transactional
    public void removeSection(Sections sections, Section deleteSection) {
        sectionRepository.delete(deleteSection);
        sections.removeSection(deleteSection);
    }

    @Transactional
    public void updateSectionsWithMerge(Sections sections, Section upSection, Section downSection) {
        sectionRepository.save(sections.getMergedSection(upSection, downSection));
        sectionRepository.delete(upSection);
        sectionRepository.delete(downSection);
        sections.mergeSections(upSection, downSection);
    }
}
