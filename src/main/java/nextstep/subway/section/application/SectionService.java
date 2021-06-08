package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class SectionService {
    private SectionRepository sectionRepository;
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public SectionResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);

        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(NoSuchElementException::new);

        Section newSection = Section.of(upStation, downStation, sectionRequest.getDistance());

        line.addSection(newSection);

        return SectionResponse.of(newSection);
    }
}
