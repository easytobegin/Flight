package com.minisheep.bean;

//航班信息:例如厦门 飞 杭州

/**
 * Created by minisheep on 16/12/27.
 */
public class BaseFlightInfo {
    private long flightId;
    private String carrier;
    private String flight;
    private String opdate;
    private String registration;
    private String aircraftType;
    private String flightTask;
    private String direction;
    private long linkedFlightId;
    private String scheduleTime;
    private String estimateTime;
    private String actualTime;
    private String flightStatus;
    private String irregularStatus;
    private String flightNature;
    private String vipType;
    private long combineFlightId;
    private String stand;
    private String gate;
    private String terminal;
    private String generalAgent;
    private String irregularCode;
    private String publishIrregularCode;
    private String origin;
    private String passby;
    private String destination;
    private String checkinCounter;
    private String carouselCode;
    private String alternateAirport;
    private String flightShareNo;
    private String scheduleCheckinOpen;
    private String scheduleCheckinClose;
    private String actualCheckinOpen;
    private String actualCheckinClose;
    private String carouselScheduleOpen;
    private String carouselScheduleClose;
    private String carouselActualOpen;
    private String carouselActualClose;
    private String gateScheduleOpen;
    private String gateScheduleClose;
    private String gateActualOpen;
    private String gateActualClose;
    private String lastUpdated;
    private String oldGate;
    private String oldGatetime;
    private String allocationMode;
    private String gateEstimateOpen;
    private String gateEstimateClose;
    private String oldGate2;
    private String oldGateTime2;
    private int istop;
    private String oldStand;
    private String oldStandTime;

    public void setFlightId(long flightId) {
        this.flightId = flightId;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public void setOpdate(String opdate) {
        this.opdate = opdate;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public void setFlightTask(String flightTask) {
        this.flightTask = flightTask;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setLinkedFlightId(long linkedFlightId) {
        this.linkedFlightId = linkedFlightId;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public void setActualTime(String actualTime) {
        this.actualTime = actualTime;
    }

    public void setFlightStatus(String flightStatus) {
        this.flightStatus = flightStatus;
    }

    public void setIrregularStatus(String irregularStatus) {
        this.irregularStatus = irregularStatus;
    }

    public void setFlightNature(String flightNature) {
        this.flightNature = flightNature;
    }

    public void setVipType(String vipType) {
        this.vipType = vipType;
    }

    public void setCombineFlightId(long combineFlightId) {
        this.combineFlightId = combineFlightId;
    }

    public void setStand(String stand) {
        this.stand = stand;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public void setGeneralAgent(String generalAgent) {
        this.generalAgent = generalAgent;
    }

    public void setIrregularCode(String irregularCode) {
        this.irregularCode = irregularCode;
    }

    public void setPublishIrregularCode(String publishIrregularCode) {
        this.publishIrregularCode = publishIrregularCode;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setPassby(String passby) {
        this.passby = passby;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setCheckinCounter(String checkinCounter) {
        this.checkinCounter = checkinCounter;
    }

    public void setCarouselCode(String carouselCode) {
        this.carouselCode = carouselCode;
    }

    public void setAlternateAirport(String alternateAirport) {
        this.alternateAirport = alternateAirport;
    }

    public void setFlightShareNo(String flightShareNo) {
        this.flightShareNo = flightShareNo;
    }

    public void setScheduleCheckinOpen(String scheduleCheckinOpen) {
        this.scheduleCheckinOpen = scheduleCheckinOpen;
    }

    public void setScheduleCheckinClose(String scheduleCheckinClose) {
        this.scheduleCheckinClose = scheduleCheckinClose;
    }

    public void setActualCheckinOpen(String actualCheckinOpen) {
        this.actualCheckinOpen = actualCheckinOpen;
    }

    public void setActualCheckinClose(String actualCheckinClose) {
        this.actualCheckinClose = actualCheckinClose;
    }

    public void setCarouselScheduleOpen(String carouselScheduleOpen) {
        this.carouselScheduleOpen = carouselScheduleOpen;
    }

    public void setCarouselScheduleClose(String carouselScheduleClose) {
        this.carouselScheduleClose = carouselScheduleClose;
    }

    public void setCarouselActualOpen(String carouselActualOpen) {
        this.carouselActualOpen = carouselActualOpen;
    }

    public void setCarouselActualClose(String carouselActualClose) {
        this.carouselActualClose = carouselActualClose;
    }

    public void setGateScheduleOpen(String gateScheduleOpen) {
        this.gateScheduleOpen = gateScheduleOpen;
    }

    public void setGateScheduleClose(String gateScheduleClose) {
        this.gateScheduleClose = gateScheduleClose;
    }

    public void setGateActualOpen(String gateActualOpen) {
        this.gateActualOpen = gateActualOpen;
    }

    public void setGateActualClose(String gateActualClose) {
        this.gateActualClose = gateActualClose;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setOldGate(String oldGate) {
        this.oldGate = oldGate;
    }

    public void setOldGatetime(String oldGatetime) {
        this.oldGatetime = oldGatetime;
    }

    public void setAllocationMode(String allocationMode) {
        this.allocationMode = allocationMode;
    }

    public void setGateEstimateOpen(String gateEstimateOpen) {
        this.gateEstimateOpen = gateEstimateOpen;
    }

    public void setGateEstimateClose(String gateEstimateClose) {
        this.gateEstimateClose = gateEstimateClose;
    }

    public void setOldGate2(String oldGate2) {
        this.oldGate2 = oldGate2;
    }

    public void setOldGateTime2(String oldGateTime2) {
        this.oldGateTime2 = oldGateTime2;
    }

    public void setIstop(int istop) {
        this.istop = istop;
    }

    public void setOldStand(String oldStand) {
        this.oldStand = oldStand;
    }

    public void setOldStandTime(String oldStandTime) {
        this.oldStandTime = oldStandTime;
    }

    public long getFlightId() {

        return flightId;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getFlight() {
        return flight;
    }

    public String getOpdate() {
        return opdate;
    }

    public String getRegistration() {
        return registration;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public String getFlightTask() {
        return flightTask;
    }

    public String getDirection() {
        return direction;
    }

    public long getLinkedFlightId() {
        return linkedFlightId;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public String getActualTime() {
        return actualTime;
    }

    public String getFlightStatus() {
        return flightStatus;
    }

    public String getIrregularStatus() {
        return irregularStatus;
    }

    public String getFlightNature() {
        return flightNature;
    }

    public String getVipType() {
        return vipType;
    }

    public long getCombineFlightId() {
        return combineFlightId;
    }

    public String getStand() {
        return stand;
    }

    public String getGate() {
        return gate;
    }

    public String getTerminal() {
        return terminal;
    }

    public String getGeneralAgent() {
        return generalAgent;
    }

    public String getIrregularCode() {
        return irregularCode;
    }

    public String getPublishIrregularCode() {
        return publishIrregularCode;
    }

    public String getOrigin() {
        return origin;
    }

    public String getPassby() {
        return passby;
    }

    public String getDestination() {
        return destination;
    }

    public String getCheckinCounter() {
        return checkinCounter;
    }

    public String getCarouselCode() {
        return carouselCode;
    }

    public String getAlternateAirport() {
        return alternateAirport;
    }

    public String getFlightShareNo() {
        return flightShareNo;
    }

    public String getScheduleCheckinOpen() {
        return scheduleCheckinOpen;
    }

    public String getScheduleCheckinClose() {
        return scheduleCheckinClose;
    }

    public String getActualCheckinOpen() {
        return actualCheckinOpen;
    }

    public String getActualCheckinClose() {
        return actualCheckinClose;
    }

    public String getCarouselScheduleOpen() {
        return carouselScheduleOpen;
    }

    public String getCarouselScheduleClose() {
        return carouselScheduleClose;
    }

    public String getCarouselActualOpen() {
        return carouselActualOpen;
    }

    public String getCarouselActualClose() {
        return carouselActualClose;
    }

    public String getGateScheduleOpen() {
        return gateScheduleOpen;
    }

    public String getGateScheduleClose() {
        return gateScheduleClose;
    }

    public String getGateActualOpen() {
        return gateActualOpen;
    }

    public String getGateActualClose() {
        return gateActualClose;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getOldGate() {
        return oldGate;
    }

    public String getOldGatetime() {
        return oldGatetime;
    }

    public String getAllocationMode() {
        return allocationMode;
    }

    public String getGateEstimateOpen() {
        return gateEstimateOpen;
    }

    public String getGateEstimateClose() {
        return gateEstimateClose;
    }

    public String getOldGate2() {
        return oldGate2;
    }

    public String getOldGateTime2() {
        return oldGateTime2;
    }

    public int getIstop() {
        return istop;
    }

    public String getOldStand() {
        return oldStand;
    }

    public String getOldStandTime() {
        return oldStandTime;
    }

}
