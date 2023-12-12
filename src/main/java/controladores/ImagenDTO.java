package controladores;

import java.util.List;
import views.ImagenView;

public class ImagenDTO {
    private Reclamo reclamo;
    private List<ImagenView> imagenes;

    public ImagenDTO(Reclamo reclamo, List<ImagenView> imagenes) {
        this.reclamo = reclamo;
        this.imagenes = imagenes;
        
    }

    public Reclamo getReclamo() {
        return reclamo;
    }

    public void setReclamo(Reclamo reclamo) {
        this.reclamo = reclamo;
    }

    public List<ImagenView> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenView> imagenes) {
        this.imagenes = imagenes;
    }


}
