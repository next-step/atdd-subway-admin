package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationQueryUseCase;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionCommandService implements SectionCommandUseCase {
    private final LineQueryUseCase lineQueryUseCase;
    private final StationQueryUseCase stationQueryUseCase;
    private final SectionRepository sectionRepository;

    public SectionCommandService(LineQueryUseCase lineQueryUseCase, StationQueryUseCase stationQueryUseCase, SectionRepository sectionRepository) {
        this.lineQueryUseCase = lineQueryUseCase;
        this.stationQueryUseCase = stationQueryUseCase;
        this.sectionRepository = sectionRepository;
    }

    @Override
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineQueryUseCase.findById(lineId);
        Station upStation = stationQueryUseCase.findById(sectionRequest.getUpStationId());
        Station downStation = stationQueryUseCase.findById(sectionRequest.getDownStationId());
        line.addSection(Section.of(upStation, downStation, sectionRequest.getDistance()));
    }

    @Override
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Line line = lineQueryUseCase.findById(lineId);
        Station station = stationQueryUseCase.findById(stationId);
        Section section = line.removeSection(station);
        sectionRepository.delete(section);
    }
}
