package nextstep.subway.section.application;

import nextstep.subway.common.exception.EntityNotExistException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionCommandService {
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;
    private LineRepository lineRepository;

    public SectionCommandService(SectionRepository sectionRepository, StationRepository stationRepository, LineRepository lineRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public Long save(Long lineId, Long upStationId, Long downStationId, Long distance) {
        return save(
                findLineById(lineId),
                upStationId,
                downStationId,
                distance
        );
    }

    public Long save(Line line, Long upStationId, Long downStationId, Long distance) {
        return save(new Section(line, findStationById(upStationId), findStationById(downStationId), distance));
    }

    public Long save(Section section) {
        return sectionRepository.save(section)
                .getId();
    }

    public void deleteByLineId(Long lineId) {
        sectionRepository.deleteByLineId(lineId);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);
    }
}
