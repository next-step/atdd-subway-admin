package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) throws DataIntegrityViolationException {
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(NoSuchElementException::new);

        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(NoSuchElementException::new);

        Line persistLine = lineRepository.save(
                new Line(request.getName(), request.getColor())
        );

        persistLine.addSection(new Section(upStation, downStation, request.getDistance()));

        return LineResponse.of(persistLine);
    }

    public List<LineResponse> getLines() {
        return lineRepository.findAll()
                .stream().map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line findLine = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);

        return LineResponse.of(findLine);
    }

    public void addSection(Long id, Long upStationId, Long downStationId, Integer distance) {
        Line findLine = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        Station upStation = stationRepository.findById(upStationId)
                .orElseThrow(NoSuchElementException::new);

        Station downStation = stationRepository.findById(downStationId)
                .orElseThrow(NoSuchElementException::new);

        findLine.addSection(new Section(upStation, downStation, distance));
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        Line findLine = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        findLine.update(
                new Line(lineRequest.getName(),
                        lineRequest.getColor())
        );

        return LineResponse.of(findLine);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
