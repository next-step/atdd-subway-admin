package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineModifyRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest request) {
        Station upStationProxy = stationRepository.getById(request.getUpStationId());
        Station downStationProxy = stationRepository.getById(request.getDownStationId());
        Line line = request.toLine();
        line.setStations(upStationProxy, downStationProxy);
        Line save = lineRepository.save(line);
        return LineResponse.of(save);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
        return LineResponse.of(line);
    }

    @Transactional
    public void modifyLine(Long lineId, LineModifyRequest request) {
        Line line = lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
        line.modifyLine(request.getName(),request.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        lineRepository.deleteById(lineId);
    }
}
