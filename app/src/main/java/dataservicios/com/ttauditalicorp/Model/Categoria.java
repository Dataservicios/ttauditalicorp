package dataservicios.com.ttauditalicorp.Model;

/**
 * Created by usuario on 30/01/2015.
 */
public class Categoria  {

    private String Nombre ;
    private int Status, Id;
    //private ArrayList<String> genre;

    public Categoria() {
    }

    public Categoria(String Nombre, int Status, int Id) {
        this.Nombre = Nombre;
        this.Status = Status;
        this.Id= Id;
    }
    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }


}