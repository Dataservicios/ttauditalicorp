package dataservicios.com.ttauditalicorp.Model;

/**
 * Created by Jaime on 19/03/2016.
 */
public class Dex {

    private int id;
    private String fullname ;
    private String codigo;
    private String region ;


    public Dex(int id, String fullname, String codigo, String region) {
        this.id = id;
        this.fullname = fullname;
        this.codigo = codigo;
        this.region = region;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
