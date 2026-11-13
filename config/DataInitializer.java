package com.sena.springecommerce.config;

import com.sena.springecommerce.model.Producto;
import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.repository.IProductoRepository;
import com.sena.springecommerce.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IProductoRepository productoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Crear usuario ADMINISTRADOR si no existe
        Usuario admin = usuarioRepository.findByEmail("admin@sena.com").orElse(null);
        if (admin == null) {
            admin = new Usuario();
            admin.setNombre("Administrador Sena");
            admin.setEmail("admin@sena.com");
            // Contraseña cifrada con BCrypt: admin123
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setTelefono("3001234567");
            admin.setDireccion("Centro de Formacion SENA");
            admin = usuarioRepository.save(admin);
            System.out.println(">>> [SECURITY SEED] Usuario ADMIN creado: admin@sena.com / clave: admin123");
        } else if (!admin.getPassword().startsWith("$2a$") && !admin.getPassword().startsWith("$2b$")) {
            // Si la clave no está en BCrypt, la actualizamos
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            usuarioRepository.save(admin);
        }

        // 2. Crear usuario NORMAL (USER) si no existe
        Usuario user = usuarioRepository.findByEmail("user@sena.com").orElse(null);
        if (user == null) {
            user = new Usuario();
            user.setNombre("Estudiante Aprendiz");
            user.setEmail("user@sena.com");
            // Contraseña cifrada con BCrypt: user123
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRol("USER");
            user.setTelefono("3109876543");
            user.setDireccion("Calle 100 # 20-30");
            user = usuarioRepository.save(user);
            System.out.println(">>> [SECURITY SEED] Usuario USER creado: user@sena.com / clave: user123");
        } else if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            usuarioRepository.save(user);
        }

        // 3. Crear producto de prueba si la tabla de productos está vacía
        if (productoRepository.count() == 0) {
            Producto p1 = new Producto();
            p1.setNombre("Laptop Dell Inspiron");
            p1.setDescripcion("Laptop potente con procesador Intel Core i7 y 16GB RAM");
            p1.setPrecio(3200000.0);
            p1.setCantidad(10);
            p1.setImagen("laptop.jpg");
            p1.setUsuario(admin);
            productoRepository.save(p1);

            Producto p2 = new Producto();
            p2.setNombre("Mouse Inalambrico Logitech");
            p2.setDescripcion("Mouse ergonomico recargable");
            p2.setPrecio(85000.0);
            p2.setCantidad(25);
            p2.setImagen("mouse.jpg");
            p2.setUsuario(admin);
            productoRepository.save(p2);

            System.out.println(">>> [DATA SEED] Productos iniciales creados para pruebas.");
        }
    }
}
