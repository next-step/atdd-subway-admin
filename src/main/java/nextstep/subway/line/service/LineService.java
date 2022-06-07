package nextstep.subway.line.service;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.repository.StationRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.dto.LineRequestDto;
import nextstep.subway.line.dto.LineResponseDto;
import nextstep.subway.line.dto.UpdateLineRequestDto;
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
    public LineResponseDto createLine(LineRequestDto lineRequestDto) {
        Station upStation = findStationById(lineRequestDto.getUpStationId());
        Station downStation = findStationById(lineRequestDto.getDownStationId());

        return createLine(lineRequestDto, upStation, downStation);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundException(String.format("입력한 역을 찾을 수 없습니다. (역: %d)", stationId)));
    }

    private LineResponseDto createLine(LineRequestDto lineRequestDto, Station upStation, Station downStation) {
        Line line = new Line(lineRequestDto.getName(), lineRequestDto.getColor(), upStation, downStation,
                lineRequestDto.getDistance());
        Line persistLine = lineRepository.save(line);

        return LineResponseDto.from(persistLine);
    }

    public List<LineResponseDto> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> LineResponseDto.from(line))
                .collect(Collectors.toList());
    }

    public LineResponseDto findLine(Long id) {
        Line line = findLineById(id);
        return LineResponseDto.from(line);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("입력한 아이디(%d)의 지하철 노선을 찾을 수 없습니다.", id)));
    }

    @Transactional
    public void updateLine(Long id, UpdateLineRequestDto updateLineRequestDto) {
        Line line = findLineById(id);
        line.update(updateLineRequestDto.getName(), updateLineRequestDto.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findLineById(id);
        lineRepository.delete(line);
    }
}
