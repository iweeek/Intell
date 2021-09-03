package com.example.intell.entry;

import java.io.Serializable;
import java.util.Date;

public class EnvironmentData implements Serializable {

    private static final long serialVersionUID = 6187447685293862071L;

    private String Air_T;
    private String Air_H;
    private String PM25;
    private String PM10;
    private String Noise;
    private String Tvoc;
    private String Shine;
    private String Speed;
    private String CO;
    private String NO2;
    private String SO2;
    private String O3;

    private int id;
    private String device_id;
    private String csq;
    private int Soil_Temperature;
    private String PH;
    private int CO2;
    private int H2S;
    private int Direction;
    private int Conductivity;
    private int VOC;
    private int Water_Height;
    private String EC;
    private String Temperature;
    private String Turbidity;
    private int lat;
    private int lon;
    private String configure;
    private Date time;


    @Override
    public String toString() {
        return "EnvironmentData{" +
                "Air_T='" + Air_T + '\'' +
                ", Air_H='" + Air_H + '\'' +
                ", PM25='" + PM25 + '\'' +
                ", PM10='" + PM10 + '\'' +
                ", Noise='" + Noise + '\'' +
                ", Tvoc='" + Tvoc + '\'' +
                ", Shine='" + Shine + '\'' +
                ", Speed='" + Speed + '\'' +
                ", CO='" + CO + '\'' +
                ", NO2='" + NO2 + '\'' +
                ", SO2='" + SO2 + '\'' +
                ", O3='" + O3 + '\'' +
                ", id=" + id +
                ", device_id='" + device_id + '\'' +
                ", csq=" + csq +
                ", Soil_Temperature=" + Soil_Temperature +
                ", PH=" + PH +
                ", CO2=" + CO2 +
                ", H2S=" + H2S +
                ", Direction=" + Direction +
                ", Conductivity=" + Conductivity +
                ", VOC=" + VOC +
                ", Water_Height=" + Water_Height +
                ", EC=" + EC +
                ", Temperature=" + Temperature +
                ", Turbidity=" + Turbidity +
                ", lat=" + lat +
                ", lon=" + lon +
                ", configure=" + configure +
                ", time=" + time +
                '}';
    }

    public String getAir_T() {
        return Air_T;
    }

    public void setAir_T(String air_T) {
        Air_T = air_T;
    }

    public String getAir_H() {
        return Air_H;
    }

    public void setAir_H(String air_H) {
        Air_H = air_H;
    }

    public String getPM25() {
        return PM25;
    }

    public void setPM25(String PM25) {
        this.PM25 = PM25;
    }

    public String getPM10() {
        return PM10;
    }

    public void setPM10(String PM10) {
        this.PM10 = PM10;
    }

    public String getNoise() {
        return Noise;
    }

    public void setNoise(String noise) {
        Noise = noise;
    }

    public String getTvoc() {
        return Tvoc;
    }

    public void setTvoc(String tvoc) {
        Tvoc = tvoc;
    }

    public String getShine() {
        return Shine;
    }

    public void setShine(String shine) {
        Shine = shine;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getCO() {
        return CO;
    }

    public void setCO(String CO) {
        this.CO = CO;
    }

    public String getNO2() {
        return NO2;
    }

    public void setNO2(String NO2) {
        this.NO2 = NO2;
    }

    public String getSO2() {
        return SO2;
    }

    public void setSO2(String SO2) {
        this.SO2 = SO2;
    }

    public String getO3() {
        return O3;
    }

    public void setO3(String o3) {
        O3 = o3;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getCsq() {
        return csq;
    }

    public void setCsq(String csq) {
        this.csq = csq;
    }

    public int getSoil_Temperature() {
        return Soil_Temperature;
    }

    public void setSoil_Temperature(int soil_Temperature) {
        Soil_Temperature = soil_Temperature;
    }

    public String getPH() {
        return PH;
    }

    public void setPH(String PH) {
        this.PH = PH;
    }

    public int getCO2() {
        return CO2;
    }

    public void setCO2(int CO2) {
        this.CO2 = CO2;
    }

    public int getH2S() {
        return H2S;
    }

    public void setH2S(int h2S) {
        H2S = h2S;
    }

    public int getDirection() {
        return Direction;
    }

    public void setDirection(int direction) {
        Direction = direction;
    }

    public int getConductivity() {
        return Conductivity;
    }

    public void setConductivity(int conductivity) {
        Conductivity = conductivity;
    }

    public int getVOC() {
        return VOC;
    }

    public void setVOC(int VOC) {
        this.VOC = VOC;
    }

    public int getWater_Height() {
        return Water_Height;
    }

    public void setWater_Height(int water_Height) {
        Water_Height = water_Height;
    }

    public String getEC() {
        return EC;
    }

    public void setEC(String EC) {
        this.EC = EC;
    }

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String temperature) {
        Temperature = temperature;
    }

    public String getTurbidity() {
        return Turbidity;
    }

    public void setTurbidity(String turbidity) {
        Turbidity = turbidity;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public String getConfigure() {
        return configure;
    }

    public void setConfigure(String configure) {
        this.configure = configure;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
