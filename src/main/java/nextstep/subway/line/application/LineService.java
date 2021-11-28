package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = request.toLine();
        line.addSection(makeSection(request));

        lineRepository.save(line);
        return LineResponse.from(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> getLines() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLine(long id) {
        Line line = findLineById(id);
        return LineResponse.from(line);
    }

    @Transactional
    public LineResponse updateLine(long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.toLine());
        lineRepository.save(line);

        return LineResponse.from(line);
    }

    @Transactional
    public void deleteLine(long id) {
        lineRepository.deleteById(id);
    }

    private Section makeSection(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        return new Section(upStation, downStation, new Distance(request.getDistance()));
    }

    private Station findStationById(long id) {
        return stationRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }

    private Line findLineById(long id) {
        return lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }
}