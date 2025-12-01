package com.recetas.recetas.service;

import com.recetas.recetas.model.Usuario;
import com.recetas.recetas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class DetalleUserService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        Collection<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                .collect(Collectors.toList());

        boolean enabled = usuario.getEnabled() != null && usuario.getEnabled() == 1;

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                enabled,
                true,
                true,
                true,
                authorities
        );
    }
}
