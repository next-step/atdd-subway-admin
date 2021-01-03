package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

	private String color;

//	private int distance;

	@OneToMany(mappedBy = "line")
	private List<LineStation> lineStations = new ArrayList<>();

	public Line() {
	}

	public Line(String name, String color) {
		this.name = name;
		this.color = color;
	}

//	public Line(String name, String color, int distance) {
//		this.name = name;
//		this.color = color;
//		this.distance = distance;
//	}

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

//	public int getDistance() {
//		return distance;
//	}

	public List<LineStation> getLineStations() {
		return lineStations;
	}

	public Line(List<LineStation> lineStations) {
		this.lineStations = lineStations;
	}

//	public void addDistance(int distance) {
//		this.distance += distance;
//	}

//	public boolean checkDistanceValidate(int newDistance) {
//		return this.distance <= newDistance;
//	}
}
