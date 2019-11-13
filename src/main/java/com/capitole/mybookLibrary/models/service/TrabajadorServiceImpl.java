package com.capitole.mybookLibrary.models.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.capitole.mybookLibrary.models.dao.ITrabajadorDao;
import com.capitole.mybookLibrary.models.entity.Partner;
import com.capitole.mybookLibrary.models.entity.Trabajador;

@Service
public class TrabajadorServiceImpl implements ITrabajadorService{

	private ITrabajadorDao trabajadorDao;
	
	public TrabajadorServiceImpl(ITrabajadorDao trabajadorDao) {
		this.trabajadorDao = trabajadorDao;
	}
	
	public List<Trabajador> findAll() {
		
		return (List<Trabajador>) trabajadorDao.findAll();
	}

	@Transactional
	public Trabajador findById(Long id) {
		return trabajadorDao.findById(id).orElse(null);
	
	}
	
	@Transactional
	public Trabajador save(Trabajador t) {
		
		return trabajadorDao.save(t);
	}

	@Transactional
	public void delete(Long id) {
		trabajadorDao.deleteById(id);
		
	}

	public Page<Trabajador> findAll(Pageable pageable) {
		return trabajadorDao.findAll(pageable);
	}

	@Override
	public List<Partner> findAllPartners() {
		return trabajadorDao.findAllPartners();
	}

}
