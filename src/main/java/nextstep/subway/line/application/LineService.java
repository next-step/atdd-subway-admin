package nextstep.subway.line.application;

import nextstep.subway.common.utils.ValidationUtils;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.excetpion.ErrorCode;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        saveTerminalStation(persistLine, request.getUpStationId(), request.getDownStationId(), request.getDistance());
        return LineResponse.of(persistLine);
    }

    public Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    public void saveTerminalStation(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = findOneStation(upStationId);
        line.addSection(new Section(line, upStation, distance));

        Station downStation = findOneStation(downStationId);
        line.addSection(new Section(line, downStation, distance));

        sectionRepository.saveAll(line.getSections());
    }

    private Station findOneStation(Long stationId) {
        if (ValidationUtils.isNull(stationId)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "종점역 입력값이 없습니다.");
        }
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
