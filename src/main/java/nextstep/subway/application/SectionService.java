package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private SectionRepository sectionRepository;

    private static final String NO_SUCH_LINE_EXCEPTION = "해당 ID의 노선 정보가 없습니다.";
    private static final String NO_SUCH_STATION_EXCEPTION = "해당 ID의 역 정보가 없습니다.";

    public SectionService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public List<SectionResponse> saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Station upStation = findStationById(sectionRequest.getUpStationId());
        Station downStation = findStationById(sectionRequest.getDownStationId());

        Sections sections = line.addSection(sectionRequest.toSection(line, upStation, downStation));

        List<Section> persistSections = sectionRepository.saveAll(sections.getSectionList());

        return persistSections.stream().map(section -> SectionResponse.of(section)).collect(Collectors.toList());
    }

    public List<SectionResponse> retrieveSectionsByLine(Long lineId) {
        List<Section> sections = findLineById(lineId).getSections().getSectionList();
        return sections.stream().map(section -> SectionResponse.of(section)).collect(Collectors.toList());
    }
    @Transactional
    public SectionResponse deleteStationById(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station deleteStation = findStationById(stationId);

        Section deletedSection = line.deleteStation(deleteStation);
        sectionRepository.delete(deletedSection);

        return SectionResponse.of(deletedSection);
    }

    public Line findLineById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException(NO_SUCH_LINE_EXCEPTION));
    }

    public Station findStationById(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException(NO_SUCH_STATION_EXCEPTION));
    }
}
