package com.capitole.mybookLibrary.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.capitole.mybookLibrary.models.entity.Partner;
import com.capitole.mybookLibrary.models.entity.Trabajador;

public interface ITrabajadorService {
	
	public List<Trabajador> findAll();
	
	public Page<Trabajador> findAll(Pageable pageable);
	
	public Trabajador findById(Long id);
	
	public Trabajador save(Trabajador t);
	
	public void delete(Long id);
	
	public List<Partner> findAllPartners();


}
