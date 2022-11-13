package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineBridge;
import nextstep.subway.domain.Station;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineDto {

    public static class CreateRequest {

        private String name;
        private String color;
        private int upStationId;
        private int downStationId;
        private int distance;

        public Line of(Station up, Station down) {
            return new Line.Builder()
                    .setName(name)
                    .setColor(color)
                    .setLineBridge(new LineBridge(up, down, distance))
                    .build();
        }

        public String getName() {
            return name;
        }

        public CreateRequest setName(String name) {
            this.name = name;
            return this;
        }

        public String getColor() {
            return color;
        }

        public CreateRequest setColor(String color) {
            this.color = color;
            return this;
        }

        public int getUpStationId() {
            return upStationId;
        }

        public CreateRequest setUpStationId(int upStationId) {
            this.upStationId = upStationId;
            return this;
        }

        public int getDownStationId() {
            return downStationId;
        }

        public CreateRequest setDownStationId(int downStationId) {
            this.downStationId = downStationId;
            return this;
        }

        public int getDistance() {
            return distance;
        }

        public CreateRequest setDistance(int distance) {
            this.distance = distance;
            return this;
        }
    }

    public static class UpdateRequest {

        private String name;
        private String color;

        public UpdateRequest setName(String name) {
            this.name = name;
            return this;
        }

        public UpdateRequest setColor(String color) {
            this.color = color;
            return this;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }
    }

    public static class Response{
        public Long id;
        public String name;
        public String color;
        public List<StationResponse> stations;

        public Response(long id, String name, String color, List<StationResponse> stations) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.stations = stations;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }

        public List<StationResponse> getStations() {
            return stations;
        }

        public static Response of (Line line) {
            return new LineDto.Response(line.getId(),
                    line.getName(),
                    line.getColor(),
                    mapToStationResponse(line)
            );
        }

        private static List<StationResponse> mapToStationResponse(Line line) {
            return line.getLineBridges()
                    .stream()
                    .flatMap(LineDto.Response::toStationResponse)
                    .collect(Collectors.toList());
        }

        private static Stream<StationResponse> toStationResponse(LineBridge section) {
            Set<StationResponse> stationResponses = new HashSet<>();

            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();

            stationResponses.add(StationResponse.of(upStation));
            stationResponses.add(StationResponse.of(downStation));
            return stationResponses.stream();
        }

    }

}
