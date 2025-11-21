package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios", schema = "barspring", uniqueConstraints = {
        @UniqueConstraint(name = "email", columnNames = {"email"})
})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome_completo", nullable = false, length = 500)
    private String nomeCompleto;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "senha", nullable = false, length = 200)
    private String senha;

    @Column(name = "cargo", nullable = false)
    private Integer cargo;

}