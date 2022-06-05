package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest request) {
        Line newLine = lineRepository.save(request.getLine());
        Station upStation = stationRepository.findById(request.getUpStationId())
                                             .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));

        Station downStation = stationRepository.findById(request.getDownStationId())
                                               .orElseThrow(() -> new NoSuchElementException("지하철 역이 존재하지 않습니다"));

        upStation.setLine(newLine);
        downStation.setLine(newLine);

        sectionRepository.save(new Section(upStation, downStation, request.getDistance()));

        return LineResponse.of(newLine, newLine.getStations());
    }

    public List<LineResponse> getLines() {
        List<LineResponse> lines = new LinkedList<>();
        for (Line line : lineRepository.findAll()) {
            lines.add(LineResponse.of(line, line.getStations()));
        }
        return lines;
    }

    public LineResponse getLineById(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));
        return LineResponse.of(line, line.getStations());
    }

    @Transactional
    public void deleteLineById(Long id) {
        List<Station> stations = stationRepository.findAllByLineId(id);
        for (Station station : stations) {
            station.setLine(null);
        }
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLineById(Long id, Line param) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(() -> new NoSuchElementException("지하철 노선이 존재하지 않습니다"));
        line.setNameColor(param);
    }

    public void addSection(Long lineId, SectionRequest request) {
        Line line = lineRepository.findById(lineId)
                                  .get();

        for (Section section : sectionRepository.findAll()) {
            // 노선 중간에 등록
            if (section.getUpStation().getId()
                       .equals(request.getUpStationId())) {
                Station newDownStation = stationRepository.findById(request.getDownStationId())
                                                       .get();
                newDownStation.setLine(line); // 노선 등록

                if (section.getDistance() - request.getDistance() <= 0) {
                    throw new RuntimeException("구간 길이 예외");
                }

                sectionRepository.save(new Section(newDownStation, section.getDownStation(), section.getDistance() - request.getDistance()));

                section.setDistance(request.getDistance()); // 길이 조정
                section.setDownStation(newDownStation); // 중간역

                return;
            }

            // 노선 마지막에 등록
            if (section.getDownStation().getId()
                       .equals(request.getUpStationId())) {
                Station upStation = section.getUpStation();
                Station downStation = stationRepository.findById(request.getDownStationId())
                                                       .get();
                downStation.setLine(line);

                sectionRepository.save(new Section(upStation, downStation, request.getDistance()));
                return;
            }

            // 노선 시작에 등록
            if (section.getUpStation().getId()
                       .equals(request.getDownStationId())) {
                Station upStation = stationRepository.findById(request.getUpStationId())
                                                     .get();

                upStation.setLine(line);

                sectionRepository.save(new Section(upStation, section.getDownStation(), request.getDistance()));

                return;
            }
        }
    }
}
