package com.example.proyecto_iot.taxista.solicitudes;

public class CarouselItemModel {
    public int imageResId;
    public String title;
    public String subtitle;
    public String location;
    public String stars;

    public CarouselItemModel(int imageResId, String title, String subtitle, String location, String stars) {
        this.imageResId = imageResId;
        this.title = title;
        this.subtitle = subtitle;
        this.location = location;
        this.stars = stars;
    }
}


