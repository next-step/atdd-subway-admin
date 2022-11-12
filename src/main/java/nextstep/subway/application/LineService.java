package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public LineDto.Response saveLine(LineDto.CreateRequest lineCreateRequest) {
        Station upStation = stationRepository.findById((long) lineCreateRequest.getUpStationId())
                .orElseThrow(()-> new IllegalArgumentException("상행종점역을 찾을 수 없습니다."));
        Station downStation = stationRepository.findById((long) lineCreateRequest.getDownStationId())
                .orElseThrow(()-> new IllegalArgumentException("하행종점역을 찾을 수 없습니다."));
        Line persistLine = lineRepository.save(lineCreateRequest.of(upStation, downStation));
        return LineDto.Response.of(persistLine);
    }

    public List<LineDto.Response> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineDto.Response::of)
                .collect(Collectors.toList());
    }

    public LineDto.Response findLineById(Long id) {
        Line lines = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 노선을 찾을 수 없습니다."));
        return LineDto.Response.of(lines);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLine(Long id, LineDto.UpdateRequest updateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선을 찾을 수 없습니다."));
        line.update(updateRequest.getName(), updateRequest.getColor());
        lineRepository.save(line);
    }
}
