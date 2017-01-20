package com.minisheep.bean;

/**
 * Created by minisheep on 16/12/27.
 */
public class BaseAirport {
	private String airportId;
	private String iataCode;
	private String icaoCode;
	private String airportNature;
	private String airportTimeZone;
	private String displayCNName;
	private String displayENName;
	private String cnabbr1w;
	private String cnabbr2w;
	private String countryCode;
	private String lastupDated;
	private String defaultLanguage;


	public String getAirportId() {
		return airportId;
	}

	public void setAirportId(String airportId) {
		this.airportId = airportId;
	}

	public String getIataCode() {
		return iataCode;
	}

	public void setIataCode(String iataCode) {
		this.iataCode = iataCode;
	}

	public String getIcaoCode() {
		return icaoCode;
	}

	public void setIcaoCode(String icaoCode) {
		this.icaoCode = icaoCode;
	}

	public String getAirportNature() {
		return airportNature;
	}

	public void setAirportNature(String airportNature) {
		this.airportNature = airportNature;
	}

	public String getAirportTimeZone() {
		return airportTimeZone;
	}

	public void setAirportTimeZone(String airportTimeZone) {
		this.airportTimeZone = airportTimeZone;
	}

	public String getDisplayCNName() {
		return displayCNName;
	}

	public void setDisplayCNName(String displayCNName) {
		this.displayCNName = displayCNName;
	}

	public String getDisplayENName() {
		return displayENName;
	}

	public void setDisplayENName(String displayENName) {
		this.displayENName = displayENName;
	}

	public String getCnabbr1w() {
		return cnabbr1w;
	}

	public void setCnabbr1w(String cnabbr1w) {
		this.cnabbr1w = cnabbr1w;
	}

	public String getCnabbr2w() {
		return cnabbr2w;
	}

	public void setCnabbr2w(String cnabbr2w) {
		this.cnabbr2w = cnabbr2w;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLastupDated() {
		return lastupDated;
	}

	public void setLastupDated(String lastupDated) {
		this.lastupDated = lastupDated;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
}
