package nextstep.subway.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final LineStationRepository lineStationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository,
                       LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional
    public Long saveLine(LineRequest lineRequest) {
        setUpDownStation(lineRequest);
        Line persistLine = lineRepository.save(lineRequest.toLine());
        lineStationRepository.save(new LineStation(persistLine, lineRequest.getDistance(), lineRequest.getUpStation(),
                lineRequest.getDownStation()));
        return persistLine.getId();
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream().map(this::getLineResponse).collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return getLineResponse(line);
    }

    public LineResponse findByName(String name) {
        Line line = lineRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        return getLineResponse(line);
    }

    @Transactional
    public void updateLine(String name, LineRequest lineRequest) {
        Line line = lineRepository.findByName(name).orElseThrow(EntityNotFoundException::new);
        line.change(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLineById(Long lineId) {
        lineStationRepository.findByLineId(lineId).ifPresent(lineStations -> lineStations.forEach(
                lineStationRepository::delete));
        lineRepository.deleteById(lineId);
    }

    private LineResponse getLineResponse(Line line) {
        return LineResponse.of(line);
    }

    private void setUpDownStation(LineRequest lineRequest) {
        List<Station> stations = stationRepository
                .findByIdIn(Arrays.asList(lineRequest.getUpStationId(), lineRequest.getDownStationId()));
        lineRequest.isUpStationThenSet(stations);
        lineRequest.isDownStationThenSet(stations);
    }
}
