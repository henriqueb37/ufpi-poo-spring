package ufpi.poo.spring.bar.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ufpi.poo.spring.bar.misc.CargoTipos;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "usuarios", schema = "barspring", uniqueConstraints = {
        @UniqueConstraint(name = "email", columnNames = {"email"})
})
@NoArgsConstructor
public class Usuario implements UserDetails {
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

    @Lob
    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", nullable = false)
    private CargoTipos cargo;

    public Usuario(String nomeCompleto, String email, String senha, CargoTipos cargo) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.cargo = cargo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return switch (getCargo()) {
            case ADMIN -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_GARCOM"));
            case GARCOM -> List.of(new SimpleGrantedAuthority("ROLE_GARCOM"));
        };
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return getSenha();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

}