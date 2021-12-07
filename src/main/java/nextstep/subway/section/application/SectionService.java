package nextstep.subway.section.application;

import java.util.Objects;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.application.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private final StationService stationService;
    private final SectionRepository sectionRepository;

    public SectionService(StationService stationService, SectionRepository sectionRepository) {
        this.stationService = stationService;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    /* 새로운 역이 상행/하행 종점으로 등록되는 경우인지 판단 */
    private boolean checkNewUpDownStation(Section section, Section targetSection) {
        return section.getDownStation().isSameStation(targetSection.getUpStation())
            || section.getUpStation().isSameStation(targetSection.getDownStation());
    }

    /* 역 사이에 새로운 역을 등록할 경우인지 판단 */
    private boolean checkNewStationBetweenStations(Section section, Section targetSection) {
        boolean result = false;
        if (section.getDownStation().isSameStation(targetSection.getDownStation())) {
            section.setDownStation(stationService.findStationById(targetSection.getUpStation().getId()));
            section.setDistance(section.getDistance() - targetSection.getDistance());
            result = true;
        }
        if (section.getUpStation().isSameStation(targetSection.getUpStation())) {
            section.setUpStation(stationService.findStationById(targetSection.getDownStation().getId()));
            section.setDistance(section.getDistance() - targetSection.getDistance());
            result = true;
        }
        return result;
    }


    private void validateSameStationsExist(Section section, Section targetSection) {
        if (section.getDownStation().isSameStation(targetSection.getDownStation())
            && section.getUpStation().isSameStation(targetSection.getUpStation())) {
            throw new IllegalArgumentException("동일한 상,하행역이 등록되어 있으면 추가할 수 없음");
        }
    }

    private void validateDistance(Section section, Section targetSection) {
        if ((section.getDownStation().isSameStation(targetSection.getDownStation())
            || section.getUpStation().isSameStation(targetSection.getUpStation()))
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
    public void delete(Sections sections) {
        sectionRepository.deleteAll(sections.getSections());
    }
}
