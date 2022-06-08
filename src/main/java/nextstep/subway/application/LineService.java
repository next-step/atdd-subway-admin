package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.exception.DataNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final StationService stationService;
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationService.findByIdOrElseThrow(lineRequest.getUpStationId());
        Station downStation = stationService.findByIdOrElseThrow(lineRequest.getDownStationId());
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(),
                upStation, downStation, lineRequest.getDistance());
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> line = lineRepository.findAll();
        return line.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse getLineResponseById(Long id) {
        Line line = findByIdOrElseThrow(id);
        return LineResponse.of(line);
    }

    @Transactional
    public Line changeLineById(Long id, String name, String color) {
        Line line = findByIdOrElseThrow(id);
        line.change(name, color);
        return lineRepository.save(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findByIdOrElseThrow(id);
        lineRepository.delete(line);
    }

    @Transactional
    public void addSection(Long id, SectionRequest sectionRequest) {
        Line line = findByIdOrElseThrow(id);
        Station upStation = stationService.findByIdOrElseThrow(sectionRequest.getUpStationId());
        Station downStation = stationService.findByIdOrElseThrow(sectionRequest.getDownStationId());
        line.addSection(upStation, downStation, sectionRequest.getDistance());
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> findSectionsById(Long id) {
        Line line = findByIdOrElseThrow(id);
        return line.sections()
                .stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStation(Long lineId, Long stationId) {
        Line line = findByIdOrElseThrow(lineId);
        Station station = stationService.findByIdOrElseThrow(stationId);
        line.deleteStation(station);
        lineRepository.save(line);
    }

    private Line findByIdOrElseThrow(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("노선이 존재하지 않습니다.", id));
    }
}
