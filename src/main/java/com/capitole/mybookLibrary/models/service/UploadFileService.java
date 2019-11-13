package com.capitole.mybookLibrary.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileService implements IUploadFileService {

	private static final String DIRECTORIO_UPLOADS = "uploads";
	
	Logger log = LoggerFactory.getLogger(UploadFileService.class);
	
	@Override
	public Resource cargar(String nombreFoto) throws MalformedURLException {
		Path rutaArchivo = getPath(nombreFoto);
		Resource recurso = getRecurso(rutaArchivo);

		if (!recurso.exists() && !recurso.isReadable()) {
			rutaArchivo = Paths.get("src/main/resources/static/images").resolve("bookDefault.png");
			recurso = getRecurso(rutaArchivo);
			log.error("Error al leer la imagen: " + nombreFoto);
		}
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_TYPE,"image/*");
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment); filename=\"" + recurso.getFilename() + "\"");
			
		return recurso;
	}

	private Resource getRecurso(Path rutaArchivo) {
		Resource recurso = null;
		try {
			recurso = new UrlResource(rutaArchivo.toUri());
			log.info("fichero leido de : " + recurso);
			
		} catch (IOException e) {
			log.error(e.getMessage());;
		}
		return recurso;
	}
	
	@Override
	public String copiar(MultipartFile archivo) throws IOException {
		String nombreArchivo =  UUID.randomUUID().toString().concat("_").concat(archivo.getOriginalFilename());
		Path ruta = getPath(nombreArchivo);
		log.info("fichero subido a : " + ruta);
		Files.copy(archivo.getInputStream(), ruta);
			
		return nombreArchivo;
	}

	@Override
	public boolean eliminar(String nombreFoto) {
		
		if (nombreFoto != null && !nombreFoto.isEmpty()) {
			Path rutaFotoAnt = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
			File archFotoAnt = rutaFotoAnt.toFile();
			if(archFotoAnt.exists() && archFotoAnt.canRead()) {
				archFotoAnt.delete();
				return true;
			}
		}
		return false;
	}

	@Override
	public Path getPath(String nombreFoto) {
		return Paths.get(DIRECTORIO_UPLOADS).resolve(nombreFoto).toAbsolutePath();
	}

}
