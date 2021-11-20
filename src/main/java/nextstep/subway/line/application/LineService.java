package nextstep.subway.line.application;

import nextstep.subway.common.exception.DuplicateParameterException;
import nextstep.subway.common.exception.NotFoundException;
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
import java.util.Objects;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAllLines() {
        return LineResponse.ofList(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Line findByLineId(Long id) throws NotFoundException {
        return lineRepository.findById(id).orElseThrow(() -> new NotFoundException("노선이 존재하지 않습니다."));
    }

    public LineResponse saveLine(LineRequest request) {
        final Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new NotFoundException("역이 존재하지 않습니다."));
        final Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new NotFoundException("역이 존재하지 않습니다."));
        Line line = request.toLine();
        line.addSection(new Section(request.getDistance(), upStation, downStation));
        Line savedLine = lineRepository.save(line);
        return LineResponse.of(savedLine);
    }

    public LineResponse findOne(Long id) {
        return LineResponse.of(findByLineId(id));
    }

    public LineResponse update(Long id, LineRequest request) {
        final Line line = findByLineId(id);
        line.update(request.toLine());
        return LineResponse.of(line);
    }

    public void delete(Long id) {
        Line line = findByLineId(id);
        lineRepository.deleteById(line.getId());
    }
}
