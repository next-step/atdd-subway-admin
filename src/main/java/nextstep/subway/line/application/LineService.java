package nextstep.subway.line.application;

import nextstep.subway.station.application.StationService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRequest.toLine();
        lineRepository.save(persistLine);
        Section section = new Section(stationService.findById(lineRequest.getUpStationId())
                                      ,stationService.findById(lineRequest.getDownStationId())
                                      ,lineRequest.getDistance());
        persistLine.addSection(section);

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void saveSection(Long id, Station upStation, Station downStation, Integer distance) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        Section section = new Section(upStation, downStation, distance);
        line.addSection(section);
    }
}
