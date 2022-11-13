package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(createLine(lineRequest));
        return LineResponse.of(line);
    }

    private Line createLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        return Line.from(
            lineRequest.getName(),
            lineRequest.getColor(),
            Distance.of(lineRequest.getDistance()),
            upStation,
            downStation
        );
    }

    private Station getStation(Long id) {
        return stationRepository.findById(id).orElseThrow(NoResultException::new);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return LineResponse.of(getLineById(id));
    }

    private Line getLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(NoResultException::new);
    }

    @Transactional
    public void modifyLine(Long id, LineRequest request) {
        Line line = getLineById(id);
        line.modify(request.getName(), request.getColor());
    }

    @Transactional
    public void removeLine(Long id) {
        lineRepository.deleteById(id);
    }
}
