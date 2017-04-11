package ddba;

import java.time.LocalDate;

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

	double getInterestPercentage() {
		return interestPercentage;
	}
}
