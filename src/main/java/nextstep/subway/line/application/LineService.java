package nextstep.subway.line.application;

import nextstep.subway.common.exception.EntityNotExistException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Long saveLine(Line line, long upStationId, long downStationId) {
        Station upStation = findStationById(upStationId);
        Station downStation = findStationById(downStationId);

        line.addSection(new Section(upStation, downStation));

        Line saved = lineRepository.save(line);

        return saved.getId();
    }

    public Long update(Long id, Line updateLine) {
        Line line = findById(id);

        line.update(updateLine);

        return line.getId();
    }

    public List<Line> findAll() {
        return lineRepository.findAll()
                .stream()
                .collect(Collectors.toList());
    }

    public Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);
    }

    public void deleteById(Long id) {
        lineRepository.deleteById(id);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(EntityNotExistException::new);
    }
}
