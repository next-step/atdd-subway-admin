package nextstep.subway.line.application;

import nextstep.subway.excetpion.ErrorCode;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static nextstep.subway.common.utils.ValidationUtils.isNull;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public Line findLine(Long id) {
        Optional<Line> findLine = findLineById(id);
        return findLine.orElse(null);
    }

    public List<Line> findLine() {
        List<Line> lines = findLineAll();
        return lines;
    }

    @Transactional
    public LineResponse saveLineAndTerminalStation(LineRequest request) {
        Line persistLine = saveLine(request.toLine());
        saveTerminalStation(persistLine, request.getUpStationId(), request.getDownStationId(), request.getDistance());
        return LineResponse.of(persistLine);
    }

    private Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    private void saveTerminalStation(Line line, Long upStationId, Long downStationId, int distance) {
        Station upStation = findOneStation(upStationId).orElse(null);
        Station downStation = findOneStation(downStationId).orElse(null);
        line.addSection(new Section(line, upStation, downStation, distance));
    }

    private Optional<Station> findOneStation(Long stationId) {
        if (isNull(stationId)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "검색할 역 아이디 입력은 필수입니다.");
        }
        return stationRepository.findById(stationId);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id).orElseThrow(() ->
                        new LineNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "노선이 존재하지 않습니다."))
                .update(lineRequest.toLine());

        lineRepository.save(line);
    }

    public void deleteLine(Long id) {
        Line line = findLineById(id).orElseThrow(() ->
                new LineNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "노선이 존재하지 않습니다."));

        lineRepository.delete(line);
    }

    private Optional<Line> findLineById(Long id) {
        if (isNull(id)) {
            throw new LineNotFoundException(ErrorCode.NOT_FOUND_ARGUMENT, "검색할 노선 아이디 입력은 필수입니다.");
        }
        return lineRepository.findById(id);
    }

    private List<Line> findLineAll() {
        return lineRepository.findAll();
    }

}
