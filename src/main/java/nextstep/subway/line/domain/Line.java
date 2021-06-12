package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	@Column
	private String color;

	@OneToMany(mappedBy = "line")
	private List<Section> sections = new LinkedList<>();

	protected Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public void update(Line line) {
		this.name = line.getName();
		this.color = line.getColor();
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

	public void addSections(Section section) {
		sections.add(section);
		section.setLine(this);
	}

	public List<Station> getOrderedStations() {
		Map<Station, Station> order = getStationOrder();
		return convertToOrderedList(order);
	}

	private Map<Station, Station> getStationOrder() {
		return sections.stream().collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));
	}

	private List<Station> convertToOrderedList(Map<Station, Station> order) {
		List<Station> stations = new ArrayList<>();
		Station key = getStartStation(order);

		stations.add(key);

		while (order.containsKey(key)) {
			stations.add(order.get(key));
			key = order.get(key);
		}

		return stations;
	}

	private Station getStartStation(Map<Station, Station> order) {
		Station key = null;

		for (Station station : order.keySet()) {
			if (!order.containsValue(station)) {
				key = station;
			}
		}
		return key;
	}

	public Long delete() {
		sections.stream().forEach(section -> section.setLine(null));
		sections = null;

		return id;
	}
}
