package edu.ncsu.csc.itrust;

public class CauseOfDeathInfo {
	private String icdCode;
	private String causeOfDeath;
	private Integer quantityOfDeaths;	

	public CauseOfDeathInfo(String icdCode, String causeOfDeath, Integer quantityOfDeaths) {
		this.icdCode = icdCode;
		this.causeOfDeath = causeOfDeath;
		this.quantityOfDeaths = quantityOfDeaths;
	}

	public void setIcdCode(String icdCode) {
		this.icdCode = icdCode;
	}

	public String getIcdCode() {
		return this.icdCode;
	}

	public void setCauseOfDeath(String cod) {
		this.causeOfDeath = cod;
	}

	public String getCauseOfDeath() {
		return this.causeOfDeath;
	}

	public void setQuantityOfDeaths(Integer quant) {
		this.quantityOfDeaths = quant;
	}

	public Integer getQuantityOfDeaths() {
		return this.quantityOfDeaths;
	}
}
