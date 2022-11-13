package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineChange;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.AlreadyDeletedException;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.exception.SameStationException;

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
    public LineResponse saveLine(LineRequest lineRequest) {
        if (lineRequest.isSameStations()) {
            throw new SameStationException();
        }

        Station upStation = findStationById(lineRequest.getUpStationId(),
            new NoStationException(lineRequest.getUpStationId()));
        Station downStation = findStationById(lineRequest.getDownStationId(),
            new NoStationException(lineRequest.getDownStationId()));

        Line persistLine = lineRepository.save(Line.of(lineRequest));
        upStation.updateLine(persistLine);
        downStation.updateLine(persistLine);

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAllLines().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = findLineById(id, new NotFoundException());
        return LineResponse.of(line);
    }

    @Transactional
    public void changeLine(Long id, LineChange lineChange) {
        Line line = findLineById(id, new NotFoundException());
        line.update(lineChange);
    }

    @Transactional
    public void removeLine(Long id) {
        Line line = findLineById(id, new AlreadyDeletedException());
        line.removeStations();
        lineRepository.delete(line);
    }

    private Station findStationById(Long id, RuntimeException exception) {
        return stationRepository.findById(id).orElseThrow(() -> exception);
    }

    private Line findLineById(Long id, RuntimeException exception) {
        return lineRepository.findById(id).orElseThrow(() -> exception);
    }
}
