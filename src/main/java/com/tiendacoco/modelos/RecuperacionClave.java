package com.tiendacoco.modelos;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="recuperacion_claves")
public class RecuperacionClave {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable=false)
    private Usuario usuario;

    @Column(name="clave_temporal", nullable=false)
    private String claveTemporal;

    @Column(name="fecha_expiracion", nullable=false)
    private LocalDateTime fechaExpiracion;

    @Column(name="utilizada", nullable=false)
    private boolean utilizada = false;

    // ————————————————
    // getters y setters
    public int getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getClaveTemporal() { return claveTemporal; }
    public void setClaveTemporal(String claveTemporal) { this.claveTemporal = claveTemporal; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public boolean isUtilizada() { return utilizada; }
    public void setUtilizada(boolean utilizada) { this.utilizada = utilizada; }
}
