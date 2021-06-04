package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {

        Section section = createSection(request);
        Line line = request.toLine();
        line.addSection(section);
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private Section createSection(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        return Section.create(upStation, downStation, request.getDistance());
    }

    private Station findStationById(long stationId) {
        return stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);
    }

    public List<LineResponse> findLines() {
        List<Line> findLines = lineRepository.findAll();
        return findLines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLine(long lineId) {
        return LineResponse.of(lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new));
    }

    @Transactional
    public void updateLine(long lineId, Line lineRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(NoSuchElementException::new);

        line.update(lineRequest);
    }

    @Transactional
    public void deleteLine(long lineId) {
        lineRepository.deleteById(lineId);
    }
}
