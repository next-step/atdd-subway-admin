package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.AlreadyDeletedException;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.exception.NotFoundException;

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
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new NoStationException(lineRequest.getUpStationId()));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new NoStationException(lineRequest.getDownStationId()));

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
        Line line = lineRepository.findLine(id).orElseThrow(NotFoundException::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void changeLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        line.update(lineRequest);
    }

    @Transactional
    public void removeLine(Long id) {
        Line line = lineRepository.findLine(id).orElseThrow(AlreadyDeletedException::new);
        line.removeStations();
        lineRepository.delete(line);
    }
}
