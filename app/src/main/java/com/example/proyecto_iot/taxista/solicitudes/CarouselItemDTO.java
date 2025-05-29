package com.example.proyecto_iot.taxista.solicitudes;

public class CarouselItemDTO {
    public String imageResName;  // nombre drawable como "hotel1"
    public String title;
    public String subtitle;
    public String location;
    public String stars;

    // Constructor vacío necesario para Gson
    public CarouselItemDTO() {}

    public CarouselItemDTO(String imageResName, String title, String subtitle, String location, String stars) {
        this.imageResName = imageResName;
        this.title = title;
        this.subtitle = subtitle;
        this.location = location;
        this.stars = stars;
    }
}

