package com.sena.springecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.springecommerce.model.Producto;
import com.sena.springecommerce.repository.IProductoRepository;

@Service
public class ProductoServiceImplement implements IProductoService {

	@Autowired
	private IProductoRepository productoRepotory;

	@Override
	public Producto save(Producto producto) {
		// TODO Auto-generated method stub
		return productoRepotory.save(producto);
	}

	@Override
	public Optional<Producto> get(Integer id) {
		// TODO Auto-generated method stub
		return productoRepotory.findById(id);
	}

	@Override
	public void update(Producto producto) {
		// TODO Auto-generated method stub
		productoRepotory.save(producto);

	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		productoRepotory.deleteById(id);

	}

	@Override
	public List<Producto> findAll() {
		// TODO Auto-generated method stub
		return productoRepotory.findAll();
	}

}
