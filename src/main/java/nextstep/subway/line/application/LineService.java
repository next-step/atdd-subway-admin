package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.lineStation.application.LineStationService;
import nextstep.subway.lineStation.domain.LineStation;
import nextstep.subway.section.application.SectionService;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private final SectionService sectionService;
    private final StationService stationService;
    private final LineStationService lineStationService;

    public LineService(LineRepository lineRepository, final SectionService sectionService, final StationService stationService, final LineStationService lineStationService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
        this.stationService = stationService;
        this.lineStationService = lineStationService;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));

        LineStation upLineStation = lineStationService.findByLineIdAndStationId(persistLine, upStation);
        persistLine.addLineStation(upLineStation);
        upStation.addLineStation(upLineStation);

        LineStation downLineStation = lineStationService.findByLineIdAndStationId(persistLine, downStation);
        persistLine.addLineStation(downLineStation);
        downStation.addLineStation(downLineStation);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(LineRequest lineRequest, Long id) {
        Station upStation = stationService.findStationById(lineRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineRequest.getDownStationId());
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        line.updateLine(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(line);
    }

    public void deleteLineById(Long id) {
        sectionService.deleteByLineId(id);
        lineRepository.deleteById(id);
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
