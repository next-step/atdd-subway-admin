package nextstep.subway.line.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        try {
            return LineResponse.of(lineRepository.save(makeLine(request)));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateKeyException("노선 생성에 실패했습니다. 이미 존재하는 노선입니다.");
        }
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(long id) throws NoSuchElementException {
        return LineResponse.of(findLineByIdOrThrow(id, "노선이 존재하지 않습니다."));
    }

    public void updateLine(long id, LineRequest updateLineRequest) {
        Line line = findLineByIdOrThrow(id, "수정 대상 노선이 존재하지 않습니다.");
        if (lineRepository.findByName(updateLineRequest.getName()).isPresent()) {
            throw new DuplicateKeyException("동일한 이름의 노선이 존재합니다.");
        }
        line.update(updateLineRequest.toLine());
    }

    public void deleteLineById(Long id) {
        Line line = findLineByIdOrThrow(id, "삭제 대상 노선이 존재하지 않습니다.");
        sectionRepository.deleteAllByLineId(id);
        lineRepository.delete(line);
    }

    private Line makeLine(LineRequest request) {
        Station upStation = this.stationRepository.findById(request.getUpStationId())
                .orElseThrow(() -> new NoSuchElementException("요청한 상행역은 등록되지 않은 역입니다. 역 ID : "
                        + request.getUpStationId()));
        Station downStation = this.stationRepository.findById(request.getDownStationId())
                .orElseThrow(() -> new NoSuchElementException("요청한 하행역은 등록되지 않은 역입니다. 역 ID : "
                        + request.getDownStationId()));
        return request.toLine()
                .addSection(new Section(upStation, downStation, request.getDistance()));
    }

    private Line findLineByIdOrThrow(Long id, String throwMessage) {
        return lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException(throwMessage));
    }
}
