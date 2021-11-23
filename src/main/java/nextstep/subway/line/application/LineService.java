package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.dto.LineInfoResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Line saveLine(Line line, Section section) {
        Line savedLine = lineRepository.save(line);
        
        if (section != null) {
            section.addSectionAtLine(line);
        }

        return savedLine;
    }

    @Transactional(readOnly = true)
    public List<LineInfoResponse> findAllForLineInfo() {
        return lineRepository.findAll()
                                .stream()
                                .map(LineInfoResponse::of)
                                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineInfoResponse findLineInfo(Long lineId) {
        Line line = lineRepository.findById(lineId)
                                    .orElseThrow(() -> new NoSuchElementException("조회된 라인이 없습니다."));

        return LineInfoResponse.of(line);
    }

    @Transactional
    public void updateLineInfo(Long lineId, Line newLine) {
        Line line = lineRepository.findById(lineId)
                                    .orElseThrow(() -> new NoSuchElementException("조회된 라인이 없습니다."));

        line.update(newLine);
    }

    @Transactional
    public void deleteLineInfo(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    @Transactional
    public Section saveSection(Section section) {
        return sectionRepository.save(section);
    }

    @Transactional
    public Sections addSection(long lineId, Section section) {
        Line line = lineRepository.findById(lineId)
                                    .orElseThrow(() -> new NoSuchElementException("조회되는 라인이 없습니다."));

        section.addSectionAtLine(line);
        
        return line.getSections();
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                                    .orElseThrow(() -> new NoSuchElementException("조회되는 라인이 없습니다."));

        Station deletingStation = stationRepository.findById(stationId)
                                                    .orElseThrow(() -> new NoSuchElementException("조회되는 역이 없습니다."));

        line.deleteStation(deletingStation);
    }
}