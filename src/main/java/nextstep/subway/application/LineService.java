package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.request.LineRequest;
import nextstep.subway.dto.response.LineResponse;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {


    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    private final LineStationRepository lineStationRepository;

    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
        LineStationRepository lineStationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
        this.sectionRepository = sectionRepository;
    }


    @Transactional
    public LineResponse createLine(LineRequest lineRequest) {
        Station upStation = getStationOrThrow(lineRequest.getUpStationId());
        Station downStation = getStationOrThrow(lineRequest.getDownStationId());
        Line line = lineRequest.toLine(upStation, downStation);

        line.addStation(upStation);
        line.addStation(downStation);
        lineRepository.save(line);

        Section section = Section.of(upStation, downStation, line,
            new Distance(lineRequest.getDistance()));
        sectionRepository.save(section);

        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
            .map(line -> LineResponse.of(line))
            .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLineById(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
        Station upStation = getStationSafely(lineRequest.getUpStationId());
        Station downStation = getStationSafely(lineRequest.getDownStationId());

        Line lineUpdate = lineRequest.toLine(upStation, downStation);

        line.update(lineUpdate);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
        lineStationRepository.deleteAllByLine(line);
        sectionRepository.deleteAllByLine(line);
        lineRepository.deleteById(line.getId());
    }

    private Station getStationSafely(Long stationId) {
        if (ObjectUtils.isEmpty(stationId)) {
            return null;
        }
        return stationRepository.findById(stationId)
            .orElse(null);
    }

    private Station getStationOrThrow(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
    }
}
