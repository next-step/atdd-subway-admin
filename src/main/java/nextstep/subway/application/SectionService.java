package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SectionService {
    private final LineService lineService;
    private final StationService stationService;

    public SectionService(LineService lineService, StationService stationService) {
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
        if (line.getSections().size() == 1) {
            throw new IllegalArgumentException("단일 구간인 노선입니다. 구간을 삭제할 수 없습니다.");
        }

        reappropriateSection(line, stationId);
    }

    private void reappropriateSection(Line line, Long stationId) {
        Sections sections = line.getSections();

        Optional<Section> prevSection = sections.getPrevSectionByStationId(stationId);
        Optional<Section> nextSection = sections.getNextSectionByStationId(stationId);

        sections.removeSection(prevSection);
        sections.removeSection(nextSection);

        if (prevSection.isPresent() && nextSection.isPresent()) {
            line.addSection(new Section(prevSection.get().getUpStation(), nextSection.get().getDownStation(), prevSection.get().getDistance() + nextSection.get().getDistance()));
        }
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
