package com.capitole.mybookLibrary.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.capitole.mybookLibrary.models.entity.Partner;
import com.capitole.mybookLibrary.models.entity.Trabajador;

public interface ITrabajadorDao extends JpaRepository<Trabajador, Long>{

	@Query("from Partner")
	public List<Partner> findAllPartners();
}
