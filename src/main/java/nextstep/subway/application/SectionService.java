package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final LineService lineService;
    private final StationService stationService;

    public SectionService(LineRepository lineRepository, SectionRepository sectionRepository, LineService lineService, StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineService.findById(lineId);

        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        Sections sections = line.getSections();
        Section section = new Section(upStation, downStation, sectionRequest.getDistance());

        for (Section lineSection : sections.getSections()) {
            validateSections(lineSection, section);
            reregisterStations(lineSection, section);
        }

        line.addSection(section);

        return LineResponse.of(line);
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = lineService.findByIdWithSections(lineId);
        line.getSections().addSection(reappropriateSection(line, stationId));
    }

    private Section reappropriateSection(Line line, Long stationId) {
        Sections sections = line.getSections();

        Section prevSection = sections.getPrevSectionByStationId(stationId);
        Section nextSection = sections.getNextSectionByStationId(stationId);

        Section resultSection = new Section(line, prevSection.getUpStation(), nextSection.getDownStation(), prevSection.getDistance() + nextSection.getDistance());

        line.getSections().removeSection(prevSection);
        line.getSections().removeSection(nextSection);

        return resultSection;
    }

    private void validateSections(Section lineSection, Section section) {
        lineSection.duplicateValidateCheck(section);
        lineSection.mismatchValidateCheck(section);
    }

    private void reregisterStations(Section lineSection, Section section) {
        lineSection.reregisterUpStation(section);
        lineSection.reregisterDownStation(section);
    }
}
