package nextstep.subway.line.application;

import nextstep.subway.common.exception.NoResultException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
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
        validateDuplicatedLine(request.getName());
        Section section = this.getNewSection(null, request.getUpStationId(), request.getDownStationId(), request.getDistance());
        Line line = request.toLine(new Sections(section));
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    private void validateDuplicatedLine(String name) {
        Optional<Line> exist = lineRepository.findByName(name);
        if (exist.isPresent()) {
            throw new DataIntegrityViolationException("중복된 노선을 추가할 수 없습니다.");
        }
    }

    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = this.findById(id);
        Station upStation = this.findStation(request.getUpStationId());
        Station downStation = this.findStation(request.getDownStationId());
        line.update(request.getName(), request.getColor(), upStation, downStation, new Distance(request.getDistance()));
        Line persistLine = lineRepository.save(line);
        return LineResponse.of(persistLine);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public SectionResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = this.findById(lineId);
        Section section = this.getNewSection(line, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        line.getSections().addSection(section);
        Line persistLine = lineRepository.save(line);
        return SectionResponse.of(section);
    }

    private Section getNewSection(final Line line,final Long upStationId, final Long downStationId, final int distance) {
        Station upStation = this.findStation(upStationId);
        Station downStation = this.findStation(downStationId);
        return new Section(line, upStation, downStation, new Distance(distance));
    }

    public void removeSectionByStationId(final Long lineId, final Long stationId) {
        Line line = this.findById(lineId);
        Station station = this.findStation(stationId);
        line.deleteStation(station);
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoResultException("찾을 수 없는 노선입니다."));
    }

    private Station findStation(final Long sectionId) {
        return stationRepository.findById(sectionId)
                .orElseThrow(() -> new NoResultException("찾을 수 없는 구간입니다."));
    }

}
