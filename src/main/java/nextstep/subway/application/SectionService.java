package nextstep.subway.application;

import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void addSection(Long id, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(EntityNotFoundException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(EntityNotFoundException::new);
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Section newSection = Section.from(upStation, downStation, Distance.from(sectionRequest.getDistance()));
        line.addSection(newSection);
    }

    @Transactional
    public void deleteSection(Long id, Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(EntityNotFoundException::new);
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        line.deleteSection(station);
    }
}
