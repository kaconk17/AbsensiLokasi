package com.example.absensilokasi;

public class AbsenModel {
    private String id, tanggal, jam, status;
    private Double lat, lng;

    public String getId(){
        return id;
    }
    public void  setId(String id){
        this.id = id;
    }

    public String getTanggal(){
        return tanggal;
    }
    public void setTanggal(String tanggal){
        this.tanggal = tanggal;
    }
    public String getJam(){
        return jam;
    }
    public void setJam(String jam){
        this.jam = jam;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }


    public Double getLat(){
        return lat;
    }
    public void setLat(Double lat){
        this.lat= lat;
    }

    public Double getLng(){
        return lng;
    }
    public void setLng(Double lng){
        this.lng = lng;
    }


}
