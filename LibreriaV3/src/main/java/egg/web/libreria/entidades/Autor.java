package egg.web.libreria.entidades;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Lucas
 */
@Entity
public class Autor {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(nullable = true, unique = true)
    private String nombre;

    @Temporal(TemporalType.TIMESTAMP)
    private Date alta;

    @Temporal(TemporalType.TIMESTAMP)
    private Date baja;

    @OneToOne
    private Foto foto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getAlta() {
        return alta;
    }

    public void setAlta(Date alta) {
        this.alta = alta;
    }

    public Date getBaja() {
        return baja;
    }

    public void setBaja(Date baja) {
        this.baja = baja;
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
    }

}
