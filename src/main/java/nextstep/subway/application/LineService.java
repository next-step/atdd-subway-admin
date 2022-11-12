package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;
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

    private LineRepository lineRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
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
}
