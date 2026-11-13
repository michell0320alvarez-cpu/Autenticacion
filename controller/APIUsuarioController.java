package com.sena.springecommerce.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.springecommerce.model.Usuario;
import com.sena.springecommerce.service.IUsuarioService;

@RestController
@RequestMapping("/apiusuarios")
public class APIUsuarioController {

	@Autowired
	private IUsuarioService usuarioService;

	// endpoint GET para obtener todos los productos
	@GetMapping("/list")
	public List<Usuario> getAllUsuario() {
		return usuarioService.findAll();

	}

	// Endpoint GET para obtener un producto por id
	@GetMapping("/product/{id}")
	public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.get(id);
		return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

	}

	// Endpint POST para crear un nuevo producto
	@PostMapping("/create")
	public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
		Usuario savedUusuario = usuarioService.save(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedUusuario);
	}

	// endpint PUT para actualizar un producto
	@PutMapping("/update/{id}")
	public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id, @RequestBody Usuario userDetails) {
		Optional<Usuario> usuario = usuarioService.get(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.notFound().build();

		}
		Usuario existingUser = usuario.get();
		existingUser.setNombre(userDetails.getNombre());
		existingUser.setTelefono(userDetails.getTelefono());
		existingUser.setEmail(userDetails.getEmail());
		existingUser.setDireccion(userDetails.getDireccion());
		existingUser.setRol(userDetails.getRol());
		existingUser.setPassword(userDetails.getPassword());

		usuarioService.update(existingUser);
		return ResponseEntity.ok(existingUser);

	}

	// Endpoint DELETE para eliminar un producto
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
		Optional<Usuario> usuario = usuarioService.get(id);
		if (!usuario.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		usuarioService.delete(id);
		return ResponseEntity.ok().build();

	}
}
