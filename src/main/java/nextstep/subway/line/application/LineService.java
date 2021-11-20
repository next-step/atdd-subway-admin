package nextstep.subway.line.application;

import nextstep.subway.common.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public LineResponse findOne(Long id) {
        Line persistLine = this.findById(id);
        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public LineResponses findAll() {
        List<Line> persistLines = lineRepository.findAll();
        return new LineResponses(persistLines);
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = this.findSection(request.getUpStationId());
        Station downStation = this.findSection(request.getDownStationId());
        Section section = new Section(upStation, downStation, new Distance(request.getDistance()));
        validateDuplicatedLine(request.getName());
        Line persistLine = lineRepository.save(request.toLine(section));
        return LineResponse.of(persistLine);
    }

    private void validateDuplicatedLine(String name) {
        Optional<Line> exist = lineRepository.findByName(name);
        if (exist.isPresent()) {
            throw new DataIntegrityViolationException("중복된 노선을 추가할 수 없습니다.");
        }
    }

    public LineResponse updateLine(Long id, LineRequest request) {
        Station upStation = this.findSection(request.getUpStationId());
        Station downStation = this.findSection(request.getDownStationId());
        Line line = this.findById(id);
        line.update(request.getName(), request.getColor(), upStation, downStation, new Distance(request.getDistance()));
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private Station findSection(Long sectionId) {
        return stationRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("찾을 수 없는 구간입니다."));
    }

    private Line findById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("찾을 수 없는 노선입니다."));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

}
