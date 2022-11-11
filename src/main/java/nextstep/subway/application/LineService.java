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
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Line line = lineRequest.toLine(
                findStationById(lineRequest.getUpStationId()),
                findStationById(lineRequest.getDownStationId()));

        return LineResponse.of(lineRepository.save(line));
    }

    private Station findStationById(Long lineRequest) {
        return stationRepository.findById(lineRequest)
                .orElseThrow(NoResultException::new);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return LineResponse.of(findLineById(id));
    }

    @Transactional
    public void updateNameAndColor(Long id, LineRequest lineRequest) {
        Line originLine = findLineById(id);
        originLine.changeNameAndColor(lineRequest.getName(), lineRequest.getColor());
        lineRepository.save(originLine);

    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(NoResultException::new);
    }
}
