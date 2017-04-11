package ddba;


public class Output {
	private String period;
	private long daysOverDeadline;
	private double interestPercentage;
	private double interest;

	public long getDaysOverDeadline() {
		return daysOverDeadline;
	}

	public void setDaysOverDeadline(long daysOverDeadline) {
		this.daysOverDeadline = daysOverDeadline;
	}

	public double getInterestPercentage() {
		return interestPercentage;
	}

	public void setInterestPercentage(double interestPercentage) {
		this.interestPercentage = interestPercentage;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
}
