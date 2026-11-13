package com.sena.springecommerce.controller;

import com.sena.springecommerce.dto.AuthRequest;
import com.sena.springecommerce.dto.AuthResponse;
import com.sena.springecommerce.dto.RegisterRequest;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.repository.IUsuarioRepository;
import com.sena.springecommerce.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Endpoint para iniciar sesión y obtener el token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Validar usuario y contraseña usando Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Cargar datos del usuario
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());

            // Generar el token JWT con los claims
            String token = jwtService.generateToken(userDetails, usuario.getRol(), usuario.getNombre());

            AuthResponse response = new AuthResponse(
                    token,
                    usuario.getEmail(),
                    usuario.getRol(),
                    usuario.getNombre(),
                    "Inicio de sesión exitoso. Use este token en la cabecera: Authorization: Bearer <token>"
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.UNAUTHORIZED.value());
            error.put("error", "Unauthorized");
            error.put("mensaje", "Credenciales incorrectas: correo o contraseña inválidos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            error.put("error", "Internal Server Error");
            error.put("mensaje", "Error al procesar autenticación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para registrar nuevos usuarios con contraseña hasheada (BCrypt)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "El email ya se encuentra registrado");
            return ResponseEntity.badRequest().body(error);
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        // IMPORTANTE: Se cifra la contraseña con BCrypt antes de guardar
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());

        // Asignar rol (por defecto USER si no se especifica)
        String rol = (request.getRol() != null && !request.getRol().trim().isEmpty())
                ? request.getRol().toUpperCase()
                : "USER";
        usuario.setRol(rol);

        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generateToken(userDetails, usuario.getRol(), usuario.getNombre());

        AuthResponse response = new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getNombre(),
                "Usuario registrado exitosamente"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
