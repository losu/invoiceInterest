package ddba;

import java.time.LocalDate;

public class Payment {

	private String paymentTitle;
	private LocalDate paymentDate;
	private double payment;

	public Payment(LocalDate paymentDate, double payment) {
		this.paymentDate = paymentDate;
		this.payment = payment;
		this.paymentTitle = "";
	}

	public Payment(String paymentTitle, LocalDate paymentDate, double payment) {
		this.paymentTitle = paymentTitle;
		this.paymentDate = paymentDate;
		this.payment = payment;
	}

	public String getPaymentTitle() {
		return paymentTitle;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public double getPayment() {
		return payment;
	}

	public void setPayment(double payment) {
		this.payment = payment;
	}
}
