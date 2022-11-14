package nextstep.subway.application;

import javax.persistence.NoResultException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    SectionRepository sectionRepository;
    LineRepository lineRepository;
    StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, LineRepository lineRepository,
                          StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRequest.toSection(findLineById(lineId),
                findStationById(sectionRequest.getUpStationId()),
                findStationById(sectionRequest.getDownStationId()));

        sectionRepository.save(section);
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(NoResultException::new);
    }

    private Station findStationById(Long lineRequest) {
        return stationRepository.findById(lineRequest)
                .orElseThrow(NoResultException::new);
    }
}
