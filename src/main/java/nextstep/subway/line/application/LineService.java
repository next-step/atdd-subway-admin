package nextstep.subway.line.application;

import static java.util.stream.Collectors.*;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line saveLine = lineRepository.save(request.toLine());
        List<Station> allByIdIsIn = stationRepository.findAllByIdIsIn(request.toStationIds());
        saveLine.toStations(allByIdIsIn);
        saveLine.changeStation();
        return LineResponse.of(saveLine);
    }

    public List<LineResponse> findAllLine() {
        List<Line> all = lineRepository.findAll();
        return all.stream()
                  .map(LineResponse::of)
                  .collect(toList());
    }

    public LineResponse findLineById(final Long id) {

        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        return LineResponse.of(line);
    }

    public LineResponse updateLine(final Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        line.update(lineRequest.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(line);
    }
}
