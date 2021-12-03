package nextstep.subway.line.application;

import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.exception.LineDuplicateException;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        checkDuplicateLine(request);

        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
          .orElseThrow(StationNotFoundException::new);
    }

    private void checkDuplicateLine(LineRequest request) {
        if (lineRepository.findByName(request.getName()).isPresent()) {
            throw new LineDuplicateException(request.getName());
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> persistLines = lineRepository.findAll();
        return LineResponse.ofList(persistLines);
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id)
          .orElseThrow(LineNotFoundException::new);
        return LineResponse.of(line);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
          .orElseThrow(LineNotFoundException::new);
        line.update(lineRequest.toLine());
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse saveSection(Long lineId, SectionRequest request) {
        Line persistLine = lineRepository.findById(lineId).orElseThrow(LineNotFoundException::new);
        Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(StationNotFoundException::new);
        Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(StationNotFoundException::new);

        persistLine.addSection(Section.of(upStation, downStation, Distance.of(request.getDistance())));
        return LineResponse.of(persistLine);
    }
}
