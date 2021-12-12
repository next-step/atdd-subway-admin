package nextstep.subway.line.application;

import nextstep.subway.common.consts.OrderIdConst;
import nextstep.subway.common.exception.ElementNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<Station> upStation = stationRepository.findById(request.getUpStationId());
        Optional<Station> downStation = stationRepository.findById(request.getDownStationId());

        if (!upStation.isPresent() || !downStation.isPresent()) {
            throw new ElementNotFoundException();
        }

        Section section = new Section(request.getDistance(), OrderIdConst.INITIAL_ORDER_ID, upStation.get(), downStation.get());
        Line line = request.toLine();
        line.addSection(section);
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public void updateLine(Long id, LineRequest request) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        Line line = optionalLine.orElseThrow(() -> new ElementNotFoundException());
        line.update(request.toLine());
        lineRepository.save(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        return optionalLine.map(LineResponse::of)
                .orElseThrow(() -> new ElementNotFoundException());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
