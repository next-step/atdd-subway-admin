package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.EntityNotFoundException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class LineService {

    private LineRepository lineRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        Line line = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(getLine(id));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse updateById(Long id, LineRequest lineRequest) {
        Line line = getLine(id);
        return LineResponse.of(lineRepository.save(line.updateInfo(lineRequest)));
    }

    @Transactional
    public void deleteById(Long id) {
        checkExistLine(lineRepository.findById(id), id);
        lineRepository.deleteById(id);
    }

    private Line getLine(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        checkExistLine(line, id);
        return line.get();
    }

    private void checkExistLine(Optional<Line> line, Long id) {
        line.orElseThrow(() -> new EntityNotFoundException("Line", id));
    }

    private Station getStation(Long id) {
        Optional<Station> station = stationRepository.findById(id);
        checkExistStation(station, id);
        return station.get();
    }

    private void checkExistStation(Optional<Station> station, Long id) {
        station.orElseThrow(() -> new EntityNotFoundException("Station", id));
    }

}
