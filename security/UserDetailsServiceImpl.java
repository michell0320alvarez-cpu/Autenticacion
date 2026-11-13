package com.sena.springecommerce.security;

import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        String rol = usuario.getRol();
        if (rol == null || rol.trim().isEmpty()) {
            rol = "USER";
        }
        
        // Formatear rol para Spring Security (debe comenzar con ROLE_)
        String roleWithPrefix = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol.toUpperCase();
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                authorities
        );
    }
}
