package ddba;

import java.time.LocalDate;

/**
 * Created by ddba on 31/03/2017.
 */
public class StatutoryInterest {

	private LocalDate date;
	private double interestPercentage;

	public StatutoryInterest(LocalDate date, double interestPercentage) {
		this.date = date;
		this.interestPercentage = interestPercentage;
	}

	public LocalDate getDate() {
		return date;
	}

	public double getInterestPercentage() {
		return interestPercentage;
	}
}
