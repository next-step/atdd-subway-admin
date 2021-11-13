package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.ErrorCode;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLineAndTerminal(LineRequest request) {
        Line persistLine = saveLine(request.toLine());
        saveTerminalStation(persistLine, request.getUpStationId(), request.getDownStationId());
        return LineResponse.of(persistLine);
    }

    public Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    public void saveTerminalStation(Line line, Long upStationId, Long downStationId) {
        Station upStation = findOneStation(upStationId);
        Station downStation = findOneStation(downStationId);
        sectionRepository.saveAll(Arrays.asList(new Section(line, upStation), new Section(line, downStation)));
    }

    private Station findOneStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "종점역이 없습니다."));
    }

    public List<LineResponse> findAllLine() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findOneLine(Long id) {
        return LineResponse.of(findOneById(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findOneNotEmpty(id);
        line.update(lineRequest.toLine());
        lineRepository.save(line);
    }

    private Line findOneById(Long id) {
        return lineRepository.findById(id).get();
    }

    private Line findOneNotEmpty(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineNotFoundException(ErrorCode.NOT_FOUND_ENTITY));
    }

    public void deleteLine(Long id) {
        Line findOne = findOneNotEmpty(id);
        lineRepository.deleteById(findOne.getId());
    }
}
