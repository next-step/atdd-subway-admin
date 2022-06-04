package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineRepository lineRepository;

    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequest.toLine(
                stationRepository.findStationById(lineRequest.getUpStationId()),
                stationRepository.findStationById(lineRequest.getDownStationId())
        ));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Optional<Line> result = lineRepository.findById(id);
        return result.map(LineResponse::of).orElseGet(LineResponse::new);
    }

    public ResponseEntity updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            Line persistLine = line.get();
            persistLine.setName(lineUpdateRequest.getName());
            persistLine.setColor(lineUpdateRequest.getColor());
            lineRepository.save(persistLine);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }
}
