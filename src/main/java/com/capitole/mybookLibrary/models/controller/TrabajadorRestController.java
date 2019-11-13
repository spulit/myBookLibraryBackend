package com.capitole.mybookLibrary.models.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.capitole.mybookLibrary.models.entity.Partner;
import com.capitole.mybookLibrary.models.entity.Trabajador;
import com.capitole.mybookLibrary.models.service.ITrabajadorService;
import com.capitole.mybookLibrary.models.service.IUploadFileService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200"})
public class TrabajadorRestController {

	private ITrabajadorService trabajadorService;
	private IUploadFileService uploadService;
	private Logger log = LoggerFactory.getLogger(TrabajadorRestController.class);
			
	public TrabajadorRestController(ITrabajadorService trabajadorService, IUploadFileService uploadService) {
		this.trabajadorService = trabajadorService;
		this.uploadService=uploadService;
	}
	
	@GetMapping("/trabajadores")
	public List<Trabajador> index(){
		return this.trabajadorService.findAll();
	}
	
	@GetMapping("/trabajadores/page/{page}")
	public Page<Trabajador> index(@PathVariable Integer page){
		return this.trabajadorService.findAll(PageRequest.of(page,  4));
	}
	
	@GetMapping("/trabajadores/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Map<String,Object> map = new HashMap<String, Object>();
		Trabajador trab = null;
		try {
			trab = trabajadorService.findById(id);
		} catch(DataAccessException e) {
			map.put("mensaje", "Error al realizar la consulta.");
			map.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
		}

		if (trab == null) {
			map.put("mensaje", "El cliente con id: ".concat(id.toString()).concat(" no existe en la aplicación"));
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Trabajador>(trab, HttpStatus.OK);
	}
	
	@PostMapping("/trabajadores")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Trabajador trab, BindingResult result) {
		Trabajador trabBBDD = null;
		Map<String,Object> map = new HashMap<String, Object>();

		if (result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo: " + err.getField()+ " tiene " + err.getDefaultMessage())
					.collect(Collectors.toList());
			map.put("error", errors);
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		try {
			trabBBDD = trabajadorService.save(trab);
			
		} catch(DataAccessException e) {
			map.put("mensaje", "Error al realizar el guardado.");
			map.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		map.put("mensaje", "Cliente creado con éxito");
		map.put("cliente", trabBBDD);
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
	}
	
	@PutMapping("/trabajadores/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> modify(@Valid @RequestBody Trabajador trab, BindingResult result, @PathVariable Long id) {
		Map<String,Object> map = new HashMap<String, Object>();
		Trabajador trabUpdated = null;
		Trabajador trabToUpdate = trabajadorService.findById(id);
		
		if (result.hasErrors()) {

			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo: " + err.getField()+ " tiene " + err.getDefaultMessage())
					.collect(Collectors.toList());
			map.put("error", errors);
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if (trabToUpdate == null) {
			map.put("mensaje", "El cliente con id: ".concat(id.toString()).concat(" no existe en la aplicación"));
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
		}
		
		trabToUpdate.setApellido(trab.getApellido());
		trabToUpdate.setEmail(trab.getEmail());
		if (!trab.getNombre().isEmpty()) {
			trabToUpdate.setNombre(trab.getNombre());
		} else
			trabToUpdate.setNombre(null);
		trabToUpdate.setCreateAt(trab.getCreateAt());
		trabToUpdate.setPartner(trab.getPartner());
		
		try {
			trabUpdated = trabajadorService.save(trabToUpdate);
		} catch(DataAccessException e) {
			map.put("mensaje", "Error al realizar el guardado.");
			map.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		map.put("mensaje", "Cliente modificado con éxito");
		map.put("cliente", trabUpdated);
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
	}
	
	@DeleteMapping("/trabajadores/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			Trabajador trab = trabajadorService.findById(id);
			uploadService.eliminar(trab.getFoto());
			trabajadorService.delete(id);
		} catch(DataAccessException e) {
			map.put("mensaje", "Error al realizar el borrado.");
			map.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		map.put("mensaje", "Cliente borrado con éxito");
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
	}
	
	@PostMapping("/trabajadores/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
		Map<String,Object> response = new HashMap<String, Object>();
		
		Trabajador trab = trabajadorService.findById(id);
		
		if (!archivo.isEmpty()) {
			
			String nombreArchivo = null;
			try {
				nombreArchivo = uploadService.copiar(archivo);
			} catch (IOException e) {
				response.put("mensaje", "Error al realizar la subida de la imagen.");
				response.put("error", e.getMessage());
				return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			uploadService.eliminar(trab.getFoto());
			trab.setFoto(nombreArchivo);
			trabajadorService.save(trab);
			
			response.put("cliente", trab);
			response.put("mensaje", "Se ha subido correctamente la imagen: " + nombreArchivo);
		}
		
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
	}

	
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable("nombreFoto") String nombreFoto){


		Resource recurso = null; 
		try {
			uploadService.cargar(nombreFoto);
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		}
		
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_TYPE,"image/*");
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment); filename=\"" + recurso.getFilename() + "\"");
			
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}

	@GetMapping("/trabajadores/partners")
	public List<Partner> listaPartners(){
		return trabajadorService.findAllPartners();
	}
	
	
}
